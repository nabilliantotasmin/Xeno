package com.xenodev.xeno.command;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.Role;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoleCommand implements CommandExecutor {
    
    private final Xeno plugin;
    
    public RoleCommand(Xeno plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("xeno.admin.role")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /role <list|set|create|delete>");
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "list":
                sender.sendMessage("§6Available Roles:");
                for (Role role : plugin.getRoleManager().getAllRoles()) {
                    sender.sendMessage("§7- §e" + role.getName() + " §7(Priority: " + role.getPriority() + ")");
                }
                break;
                
            case "set":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /role set <player> <role>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-not-found"));
                    return true;
                }
                String roleName = args[2];
                if (plugin.getRoleManager().getRole(roleName) == null) {
                    sender.sendMessage("§cRole not found!");
                    return true;
                }
                plugin.getRoleManager().setPlayerRole(target, roleName);
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§aSet §e" + target.getName() + "§a's role to §e" + roleName);
                break;
                
            case "create":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /role create <name>");
                    return true;
                }
                plugin.getRoleManager().createRole(args[1]);
                plugin.getRoleManager().save();
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§aCreated role §e" + args[1]);
                break;
                
            case "delete":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /role delete <name>");
                    return true;
                }
                plugin.getRoleManager().deleteRole(args[1]);
                plugin.getRoleManager().save();
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§aDeleted role §e" + args[1]);
                break;
                
            default:
                sender.sendMessage("§cUsage: /role <list|set|create|delete>");
                break;
        }
        
        return true;
    }
}