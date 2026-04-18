package com.xenodev.xeno.manager;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.Home;
import com.xenodev.xeno.data.PlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    
    private final Xeno plugin;
    private final Map<UUID, PlayerData> playerData;
    private final File dataFolder;
    
    public EconomyManager(Xeno plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<>();
        this.dataFolder = new File(plugin.getDataFolder(), "data/players");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }
    
    public void load() {
    }
    
    public PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }
    
    public PlayerData loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerData.containsKey(uuid)) {
            return playerData.get(uuid);
        }
        
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            PlayerData data = new PlayerData(
                uuid,
                player.getName(),
                config.getDouble("balance", plugin.getConfigManager().getStartingBalance()),
                config.getString("role", plugin.getConfigManager().getDefaultRole())
            );
            
            ConfigurationSection homesSection = config.getConfigurationSection("homes");
            if (homesSection != null) {
                for (String homeName : homesSection.getKeys(false)) {
                    ConfigurationSection homeSection = homesSection.getConfigurationSection(homeName);
                    if (homeSection != null) {
                        Home home = new Home(
                            homeSection.getString("world"),
                            homeSection.getDouble("x"),
                            homeSection.getDouble("y"),
                            homeSection.getDouble("z"),
                            (float) homeSection.getDouble("yaw"),
                            (float) homeSection.getDouble("pitch")
                        );
                        data.addHome(homeName, home);
                    }
                }
            }
            
            data.setLastTeleport(config.getLong("last-teleport", 0));
            playerData.put(uuid, data);
            return data;
        } else {
            PlayerData data = new PlayerData(
                uuid,
                player.getName(),
                plugin.getConfigManager().getStartingBalance(),
                plugin.getConfigManager().getDefaultRole()
            );
            playerData.put(uuid, data);
            return data;
        }
    }
    
    public void savePlayer(UUID uuid) {
        PlayerData data = playerData.get(uuid);
        if (data == null) return;
        
        File file = new File(dataFolder, uuid.toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        
        config.set("name", data.getName());
        config.set("balance", data.getBalance());
        config.set("role", data.getRole());
        config.set("last-teleport", data.getLastTeleport());
        
        for (Map.Entry<String, Home> entry : data.getHomes().entrySet()) {
            String path = "homes." + entry.getKey();
            Home home = entry.getValue();
            config.set(path + ".world", home.getWorld());
            config.set(path + ".x", home.getX());
            config.set(path + ".y", home.getY());
            config.set(path + ".z", home.getZ());
            config.set(path + ".yaw", home.getYaw());
            config.set(path + ".pitch", home.getPitch());
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player data for " + uuid);
            e.printStackTrace();
        }
    }
    
    public void unloadPlayer(UUID uuid) {
        savePlayer(uuid);
        playerData.remove(uuid);
    }
    
    public void saveAll() {
        for (UUID uuid : playerData.keySet()) {
            savePlayer(uuid);
        }
    }
    
    public double getBalance(UUID uuid) {
        PlayerData data = playerData.get(uuid);
        return data != null ? data.getBalance() : 0.0;
    }
    
    public void setBalance(UUID uuid, double amount) {
        PlayerData data = playerData.get(uuid);
        if (data != null) {
            data.setBalance(Math.max(0, amount));
        }
    }
    
    public boolean hasBalance(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }
    
    public boolean deposit(UUID uuid, double amount) {
        if (amount < 0) return false;
        PlayerData data = playerData.get(uuid);
        if (data != null) {
            data.setBalance(data.getBalance() + amount);
            return true;
        }
        return false;
    }
    
    public boolean withdraw(UUID uuid, double amount) {
        if (amount < 0) return false;
        PlayerData data = playerData.get(uuid);
        if (data != null && data.getBalance() >= amount) {
            data.setBalance(data.getBalance() - amount);
            return true;
        }
        return false;
    }
    
    public boolean transfer(UUID from, UUID to, double amount) {
        if (withdraw(from, amount)) {
            deposit(to, amount);
            return true;
        }
        return false;
    }
}