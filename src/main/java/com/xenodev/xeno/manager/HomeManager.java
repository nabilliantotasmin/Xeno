package com.xenodev.xeno.manager;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.Home;
import com.xenodev.xeno.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;public class HomeManager {
    
    private final Xeno plugin;
    
    public HomeManager(Xeno plugin) {
        this.plugin = plugin;
    }
    
    public boolean setHome(Player player, String name) {
        PlayerData data = plugin.getEconomyManager().getPlayerData(player.getUniqueId());
        if (data == null) return false;
        
        int maxHomes = plugin.getRoleManager().hasPermission(player, "xeno.vip")
            ? plugin.getConfigManager().getMaxHomesVIP()
            : plugin.getConfigManager().getMaxHomes();
        
        if (!data.hasHome(name) && data.getHomeCount() >= maxHomes) {
            return false;
        }
        
        Home home = Home.fromLocation(player.getLocation());
        data.addHome(name, home);
        return true;
    }
    
    public boolean deleteHome(Player player, String name) {
        PlayerData data = plugin.getEconomyManager().getPlayerData(player.getUniqueId());
        if (data == null || !data.hasHome(name)) return false;
        
        data.removeHome(name);
        return true;
    }
    
    public boolean teleportToHome(Player player, String name) {
        PlayerData data = plugin.getEconomyManager().getPlayerData(player.getUniqueId());
        if (data == null) return false;
        
        Home home = data.getHome(name);
        if (home == null) return false;
        
        Location location = home.toLocation();
        if (location.getWorld() == null) return false;
        
        player.teleport(location);
        return true;
    }
    
    public PlayerData getPlayerData(Player player) {
        return plugin.getEconomyManager().getPlayerData(player.getUniqueId());
    }
}