package com.xenodev.xeno.manager;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.PlayerData;
import com.xenodev.xeno.data.Role;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class RoleManager {
    
    private final Xeno plugin;
    private final Map<String, Role> roles;
    private File rolesFile;
    
    public RoleManager(Xeno plugin) {
        this.plugin = plugin;
        this.roles = new HashMap<>();
    }
    
    public void load() {
        rolesFile = new File(plugin.getDataFolder(), "roles.yml");
        if (!rolesFile.exists()) {
            createDefaultRoles();
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(rolesFile);
        roles.clear();
        
        ConfigurationSection rolesSection = config.getConfigurationSection("roles");
        if (rolesSection != null) {
            for (String roleName : rolesSection.getKeys(false)) {
                ConfigurationSection roleSection = rolesSection.getConfigurationSection(roleName);
                if (roleSection != null) {
                    Role role = new Role(roleName);
                    role.setPrefix(roleSection.getString("prefix", ""));
                    role.setSuffix(roleSection.getString("suffix", ""));
                    role.setPermissions(roleSection.getStringList("permissions"));
                    role.setPriority(roleSection.getInt("priority", 0));
                    roles.put(roleName.toLowerCase(), role);
                }
            }
        }
    }
    
    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        
        for (Role role : roles.values()) {
            String path = "roles." + role.getName();
            config.set(path + ".prefix", role.getPrefix());
            config.set(path + ".suffix", role.getSuffix());
            config.set(path + ".permissions", role.getPermissions());
            config.set(path + ".priority", role.getPriority());
        }
        
        try {
            config.save(rolesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save roles data!");
            e.printStackTrace();
        }
    }
    
    private void createDefaultRoles() {
        Role member = new Role("member");
        member.setPrefix("§7[Member] ");
        member.setPriority(1);
        member.setPermissions(Arrays.asList("xeno.basic"));
        roles.put("member", member);
        
        Role vip = new Role("vip");
        vip.setPrefix("§6[VIP] ");
        vip.setPriority(2);
        vip.setPermissions(Arrays.asList("xeno.basic", "xeno.vip"));
        roles.put("vip", vip);
        
        Role admin = new Role("admin");
        admin.setPrefix("§c[Admin] ");
        admin.setPriority(3);
        admin.setPermissions(Arrays.asList("*"));
        roles.put("admin", admin);
        
        save();
    }
    
    public Role getRole(String name) {
        return roles.get(name.toLowerCase());
    }
    
    public Role getPlayerRole(Player player) {
        PlayerData data = plugin.getEconomyManager().getPlayerData(player.getUniqueId());
        if (data != null) {
            return getRole(data.getRole());
        }
        return getRole(plugin.getConfigManager().getDefaultRole());
    }
    
    public void setPlayerRole(Player player, String roleName) {
        PlayerData data = plugin.getEconomyManager().getPlayerData(player.getUniqueId());
        if (data != null && roles.containsKey(roleName.toLowerCase())) {
            data.setRole(roleName.toLowerCase());
        }
    }
    
    public boolean hasPermission(Player player, String permission) {
        if (player.isOp() || player.hasPermission(permission)) {
            return true;
        }
        
        Role role = getPlayerRole(player);
        return role != null && role.hasPermission(permission);
    }
    
    public Collection<Role> getAllRoles() {
        return roles.values();
    }
    
    public void createRole(String name) {
        if (!roles.containsKey(name.toLowerCase())) {
            roles.put(name.toLowerCase(), new Role(name));
        }
    }
    
    public void deleteRole(String name) {
        roles.remove(name.toLowerCase());
    }
}