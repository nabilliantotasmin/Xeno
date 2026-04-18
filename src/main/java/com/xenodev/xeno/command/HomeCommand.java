package com.xenodev.xeno.command;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.Home;
import com.xenodev.xeno.data.PlayerData;
import com.cryptomorin.xseries.XSound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Map;

public class HomeCommand implements CommandExecutor {
    
    private final Xeno plugin;
    
    public HomeCommand(Xeno plugin) {
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
            case "sethome":
                return handleSethome(player, args);
            case "home":
                return handleHome(player, args);
            case "delhome":
                return handleDelhome(player, args);
            case "homes":
                return handleHomes(player);
        }
        
        return false;
    }
    
    private boolean handleSethome(Player player, String[] args) {
        String homeName = args.length > 0 ? args[0] : "home";
        
        if (!plugin.getHomeManager().setHome(player, homeName)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("home-limit"));
            return true;
        }
        
        String msg = plugin.getConfigManager().getMessage("home-set")
            .replace("{name}", homeName);
        player.sendMessage(plugin.getConfigManager().getPrefix() + msg);
        
        return true;
    }
    
    private boolean handleHome(Player player, String[] args) {
        String homeName = args.length > 0 ? args[0] : "home";
        
        if (!plugin.getHomeManager().teleportToHome(player, homeName)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("home-not-found"));
            return true;
        }
        
        String msg = plugin.getConfigManager().getMessage("home-teleported")
            .replace("{name}", homeName);
        player.sendMessage(plugin.getConfigManager().getPrefix() + msg);
        XSound.matchXSound("ENTITY_ENDERMAN_TELEPORT").ifPresent(s -> s.play(player));
        
        return true;
    }
    
    private boolean handleDelhome(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§cUsage: /delhome <name>");
            return true;
        }
        
        String homeName = args[0];
        
        if (!plugin.getHomeManager().deleteHome(player, homeName)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("home-not-found"));
            return true;
        }
        
        String msg = plugin.getConfigManager().getMessage("home-deleted")
            .replace("{name}", homeName);
        player.sendMessage(plugin.getConfigManager().getPrefix() + msg);
        
        return true;
    }
    
    private boolean handleHomes(Player player) {
        PlayerData data = plugin.getHomeManager().getPlayerData(player);
        if (data == null || data.getHomes().isEmpty()) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "§cYou have no homes set!");
            return true;
        }
        
        player.sendMessage("§6Your Homes:");
        for (Map.Entry<String, Home> entry : data.getHomes().entrySet()) {
            Home home = entry.getValue();
            player.sendMessage("§7- §e" + entry.getKey() + " §7(" + home.getWorld() + ")");
        }
        
        return true;
    }
}