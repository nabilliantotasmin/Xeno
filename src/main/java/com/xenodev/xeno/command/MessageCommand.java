package com.xenodev.xeno.command;

import com.xenodev.xeno.Xeno;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class MessageCommand implements CommandExecutor {
    
    private final Xeno plugin;
    
    public MessageCommand(Xeno plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();
        
        if (cmd.equals("msg") || cmd.equals("tell") || cmd.equals("whisper") || cmd.equals("w")) {
            return handleMsg(player, args);
        } else if (cmd.equals("reply") || cmd.equals("r")) {
            return handleReply(player, args);
        }
        
        return false;
    }
    
    private boolean handleMsg(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /msg <player> <message>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage("§cYou cannot message yourself!");
            return true;
        }
        
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }
        String message = messageBuilder.toString().trim();
        
        plugin.getMessageManager().setLastMessaged(player.getUniqueId(), target.getUniqueId());
        
        String sentMsg = plugin.getConfigManager().getMessage("message-sent")
            .replace("{player}", target.getName())
            .replace("{message}", message);
        String receivedMsg = plugin.getConfigManager().getMessage("message-received")
            .replace("{player}", player.getName())
            .replace("{message}", message);
        
        player.sendMessage(plugin.getConfigManager().getPrefix() + sentMsg);
        target.sendMessage(plugin.getConfigManager().getPrefix() + receivedMsg);
        
        XSound.matchXSound("BLOCK_NOTE_BLOCK_PLING").ifPresent(s -> s.play(target));
        
        return true;
    }
    
    private boolean handleReply(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§cUsage: /reply <message>");
            return true;
        }
        
        UUID targetUUID = plugin.getMessageManager().getLastMessaged(player.getUniqueId());
        if (targetUUID == null) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("no-reply-target"));
            return true;
        }
        
        Player target = Bukkit.getPlayer(targetUUID);
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
        }
        String message = messageBuilder.toString().trim();
        
        String sentMsg = plugin.getConfigManager().getMessage("message-sent")
            .replace("{player}", target.getName())
            .replace("{message}", message);
        String receivedMsg = plugin.getConfigManager().getMessage("message-received")
            .replace("{player}", player.getName())
            .replace("{message}", message);
        
        player.sendMessage(plugin.getConfigManager().getPrefix() + sentMsg);
        target.sendMessage(plugin.getConfigManager().getPrefix() + receivedMsg);
        
        XSound.matchXSound("BLOCK_NOTE_BLOCK_PLING").ifPresent(s -> s.play(target));
        
        return true;
    }
}