package com.xenodev.xeno.manager;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.Warp;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarpManager {
    
    private final Xeno plugin;
    private final Map<String, Warp> warps;
    private File warpsFile;
    
    public WarpManager(Xeno plugin) {
        this.plugin = plugin;
        this.warps = new HashMap<>();
    }
    
    public void load() {
        warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        if (!warpsFile.exists()) {
            try {
                warpsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(warpsFile);
        warps.clear();
        
        ConfigurationSection warpsSection = config.getConfigurationSection("warps");
        if (warpsSection != null) {
            for (String warpName : warpsSection.getKeys(false)) {
                ConfigurationSection warpSection = warpsSection.getConfigurationSection(warpName);
                if (warpSection != null) {
                    Warp warp = new Warp(
                        warpName,
                        warpSection.getString("world"),
                        warpSection.getDouble("x"),
                        warpSection.getDouble("y"),
                        warpSection.getDouble("z"),
                        (float) warpSection.getDouble("yaw"),
                        (float) warpSection.getDouble("pitch")
                    );
                    warps.put(warpName.toLowerCase(), warp);
                }
            }
        }
    }
    
    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        
        for (Warp warp : warps.values()) {
            String path = "warps." + warp.getName();
            config.set(path + ".world", warp.getWorld());
            config.set(path + ".x", warp.getX());
            config.set(path + ".y", warp.getY());
            config.set(path + ".z", warp.getZ());
            config.set(path + ".yaw", warp.getYaw());
            config.set(path + ".pitch", warp.getPitch());
        }
        
        try {
            config.save(warpsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save warps data!");
            e.printStackTrace();
        }
    }
    
    public boolean createWarp(String name, Location location) {
        if (warps.containsKey(name.toLowerCase())) return false;
        
        Warp warp = Warp.fromLocation(name, location);
        warps.put(name.toLowerCase(), warp);
        return true;
    }
    
    public boolean deleteWarp(String name) {
        return warps.remove(name.toLowerCase()) != null;
    }
    
    public boolean teleportToWarp(Player player, String name) {
        Warp warp = warps.get(name.toLowerCase());
        if (warp == null) return false;
        
        Location location = warp.toLocation();
        if (location.getWorld() == null) return false;
        
        player.teleport(location);
        return true;
    }
    
    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }
    
    public Collection<Warp> getAllWarps() {
        return warps.values();
    }
    
    public Set<String> getWarpNames() {
        return warps.keySet();
    }
}