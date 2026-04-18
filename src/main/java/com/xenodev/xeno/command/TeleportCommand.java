package com.xenodev.xeno.command;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.TeleportRequest;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {
    
    private final Xeno plugin;
    
    public TeleportCommand(Xeno plugin) {
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
        
        switch (cmd) {
            case "tp":
                return handleTp(player, args);
            case "tpa":
                return handleTpa(player, args);
            case "tpaccept":
            case "tpyes":
                return handleTpaccept(player);
            case "tpdeny":
            case "tpno":
                return handleTpdeny(player);
        }
        
        return false;
    }
    
    private boolean handleTp(Player player, String[] args) {
        if (!player.hasPermission("xeno.admin.tp")) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            player.sendMessage("§cUsage: /tp <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        player.teleport(target.getLocation());
        player.sendMessage(plugin.getConfigManager().getPrefix() + "§aTeleported to §e" + target.getName());
        XSound.matchXSound("ENTITY_ENDERMAN_TELEPORT").ifPresent(s -> s.play(player));
        
        return true;
    }
    
    private boolean handleTpa(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§cUsage: /tpa <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage("§cYou cannot teleport to yourself!");
            return true;
        }
        
        plugin.getTeleportManager().createRequest(player, target);
        
        String sentMsg = plugin.getConfigManager().getMessage("teleport-request-sent")
            .replace("{player}", target.getName());
        String receivedMsg = plugin.getConfigManager().getMessage("teleport-request-received")
            .replace("{player}", player.getName());
        
        player.sendMessage(plugin.getConfigManager().getPrefix() + sentMsg);
        target.sendMessage(plugin.getConfigManager().getPrefix() + receivedMsg);
        
        return true;
    }
    
    private boolean handleTpaccept(Player player) {
        TeleportRequest request = plugin.getTeleportManager().getRequest(player.getUniqueId());
        
        if (request == null) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("teleport-no-request"));
            return true;
        }
        
        Player requester = Bukkit.getPlayer(request.getRequester());
        if (requester == null || !requester.isOnline()) {
            plugin.getTeleportManager().removeRequest(player.getUniqueId());
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        if (!plugin.getTeleportManager().canTeleport(requester)) {
            player.sendMessage("§c" + requester.getName() + " is on cooldown!");
            return true;
        }
        
        requester.teleport(player.getLocation());
        plugin.getTeleportManager().updateLastTeleport(requester);
        plugin.getTeleportManager().removeRequest(player.getUniqueId());
        
        String acceptedMsg = plugin.getConfigManager().getMessage("teleport-accepted");
        player.sendMessage(plugin.getConfigManager().getPrefix() + acceptedMsg);
        requester.sendMessage(plugin.getConfigManager().getPrefix() + "§aTeleport request accepted by §e" + player.getName());
        
        XSound.matchXSound("ENTITY_ENDERMAN_TELEPORT").ifPresent(s -> s.play(requester));
        
        return true;
    }
    
    private boolean handleTpdeny(Player player) {
        TeleportRequest request = plugin.getTeleportManager().getRequest(player.getUniqueId());
        
        if (request == null) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("teleport-no-request"));
            return true;
        }
        
        Player requester = Bukkit.getPlayer(request.getRequester());
        plugin.getTeleportManager().removeRequest(player.getUniqueId());
        
        String deniedMsg = plugin.getConfigManager().getMessage("teleport-denied");
        player.sendMessage(plugin.getConfigManager().getPrefix() + deniedMsg);
        
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(plugin.getConfigManager().getPrefix() + "§cTeleport request denied by §e" + player.getName());
        }
        
        return true;
    }
}