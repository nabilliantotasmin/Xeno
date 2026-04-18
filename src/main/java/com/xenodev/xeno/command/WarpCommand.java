package com.xenodev.xeno.command;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.Warp;
import com.cryptomorin.xseries.XSound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {
    
    private final Xeno plugin;
    
    public WarpCommand(Xeno plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        
        switch (cmd) {
            case "setwarp":
                return handleSetwarp(sender, args);
            case "delwarp":
                return handleDelwarp(sender, args);
            case "warp":
                return handleWarp(sender, args);
            case "warps":
                return handleWarps(sender);
        }
        
        return false;
    }
    
    private boolean handleSetwarp(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        if (!sender.hasPermission("xeno.admin.warp")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /setwarp <name>");
            return true;
        }
        
        Player player = (Player) sender;
        String warpName = args[0];
        
        if (!plugin.getWarpManager().createWarp(warpName, player.getLocation())) {
            player.sendMessage("§cWarp already exists!");
            return true;
        }
        
        plugin.getWarpManager().save();
        
        String msg = plugin.getConfigManager().getMessage("warp-created")
            .replace("{name}", warpName);
        player.sendMessage(plugin.getConfigManager().getPrefix() + msg);
        
        return true;
    }
    
    private boolean handleDelwarp(CommandSender sender, String[] args) {
        if (!sender.hasPermission("xeno.admin.warp")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /delwarp <name>");
            return true;
        }
        
        String warpName = args[0];
        
        if (!plugin.getWarpManager().deleteWarp(warpName)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("warp-not-found"));
            return true;
        }
        
        plugin.getWarpManager().save();
        
        String msg = plugin.getConfigManager().getMessage("warp-deleted")
            .replace("{name}", warpName);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + msg);
        
        return true;
    }
    
    private boolean handleWarp(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /warp <name>");
            return true;
        }
        
        Player player = (Player) sender;
        String warpName = args[0];
        
        if (!plugin.getWarpManager().teleportToWarp(player, warpName)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("warp-not-found"));
            return true;
        }
        
        String msg = plugin.getConfigManager().getMessage("warp-teleported")
            .replace("{name}", warpName);
        player.sendMessage(plugin.getConfigManager().getPrefix() + msg);
        XSound.matchXSound("ENTITY_ENDERMAN_TELEPORT").ifPresent(s -> s.play(player));
        
        return true;
    }
    
    private boolean handleWarps(CommandSender sender) {
        if (plugin.getWarpManager().getAllWarps().isEmpty()) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cNo warps available!");
            return true;
        }
        
        sender.sendMessage("§6Available Warps:");
        for (Warp warp : plugin.getWarpManager().getAllWarps()) {
            sender.sendMessage("§7- §e" + warp.getName() + " §7(" + warp.getWorld() + ")");
        }
        
        return true;
    }
}