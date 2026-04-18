package com.xenodev.xeno.manager;

import com.xenodev.xeno.Xeno;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final Xeno plugin;
    private FileConfiguration config;
    
    public ConfigManager(Xeno plugin) {
        this.plugin = plugin;
    }
    
    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    public FileConfiguration getConfig() {
        return this.config;
    }
    
    public double getStartingBalance() {
        return config.getDouble("economy.starting-balance", 1000.0);
    }
    
    public String getCurrencySymbol() {
        return config.getString("economy.currency-symbol", "$");
    }
    
    public String getCurrencyName() {
        return config.getString("economy.currency-name", "Dollar");
    }
    
    public String getCurrencyNamePlural() {
        return config.getString("economy.currency-name-plural", "Dollars");
    }
    
    public String getDefaultRole() {
        return config.getString("roles.default-role", "member");
    }
    
    public int getTeleportTimeout() {
        return config.getInt("teleport.request-timeout", 60);
    }
    
    public int getTeleportCooldown() {
        return config.getInt("teleport.cooldown", 5);
    }
    
    public int getMaxHomes() {
        return config.getInt("homes.max-homes", 5);
    }
    
    public int getMaxHomesVIP() {
        return config.getInt("homes.max-homes-vip", 10);
    }
    
    public String getPrefix() {
        return colorize(config.getString("messages.prefix", "&8[&6Xeno&8]&r "));
    }
    
    public String getMessage(String key) {
        return colorize(config.getString("messages." + key, "&cMessage not found: " + key));
    }
    
    public boolean isDebug() {
        return config.getBoolean("debug", false);
    }
    
    private String colorize(String text) {
        return text.replace("&", "§");
    }
}