package com.xenodev.xeno;

import com.xenodev.xeno.command.*;
import com.xenodev.xeno.inventory.gui.GUIListener;
import com.xenodev.xeno.inventory.gui.GUIManager;
import com.xenodev.xeno.listener.PlayerJoinListener;
import com.xenodev.xeno.listener.PlayerQuitListener;
import com.xenodev.xeno.manager.*;
import com.xenodev.xeno.vault.VaultEconomyProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Xeno extends JavaPlugin {
    
    private ConfigManager configManager;
    private EconomyManager economyManager;
    private ShopManager shopManager;
    private RoleManager roleManager;
    private TeleportManager teleportManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private MessageManager messageManager;
    private GUIManager guiManager;
    
    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.economyManager = new EconomyManager(this);
        this.shopManager = new ShopManager(this);
        this.roleManager = new RoleManager(this);
        this.teleportManager = new TeleportManager(this);
        this.homeManager = new HomeManager(this);
        this.warpManager = new WarpManager(this);
        this.messageManager = new MessageManager();
        this.guiManager = new GUIManager();
        
        this.configManager.load();
        this.economyManager.load();
        this.shopManager.load();
        this.roleManager.load();
        this.warpManager.load();
        
        registerCommands();
        registerListeners();
        
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupVault();
            getLogger().info("Vault integration enabled!");
        }
        
        getLogger().info("Xeno has been enabled!");
    }
    
    @Override
    public void onDisable() {
        this.economyManager.saveAll();
        this.warpManager.save();
        this.shopManager.save();
        this.roleManager.save();
        getLogger().info("Xeno has been disabled!");
    }
    
    private void registerCommands() {
        getCommand("balance").setExecutor(new EconomyCommand(this));
        getCommand("pay").setExecutor(new EconomyCommand(this));
        getCommand("eco").setExecutor(new EconomyCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("role").setExecutor(new RoleCommand(this));
        getCommand("tp").setExecutor(new TeleportCommand(this));
        getCommand("tpa").setExecutor(new TeleportCommand(this));
        getCommand("tpaccept").setExecutor(new TeleportCommand(this));
        getCommand("tpdeny").setExecutor(new TeleportCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new HomeCommand(this));
        getCommand("delhome").setExecutor(new HomeCommand(this));
        getCommand("homes").setExecutor(new HomeCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("setwarp").setExecutor(new WarpCommand(this));
        getCommand("delwarp").setExecutor(new WarpCommand(this));
        getCommand("warps").setExecutor(new WarpCommand(this));
        getCommand("msg").setExecutor(new MessageCommand(this));
        getCommand("reply").setExecutor(new MessageCommand(this));
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(guiManager), this);
    }
    
    private void setupVault() {
        VaultEconomyProvider provider = new VaultEconomyProvider(this);
        Bukkit.getServicesManager().register(
            net.milkbowl.vault.economy.Economy.class,
            provider,
            this,
            ServicePriority.Highest
        );
    }
}