package com.xenodev.xeno.manager;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.ShopItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopManager {
    
    private final Xeno plugin;
    private final List<ShopItem> shopItems;
    private File shopFile;
    
    public ShopManager(Xeno plugin) {
        this.plugin = plugin;
        this.shopItems = new ArrayList<>();
    }
    
    public void load() {
        shopFile = new File(plugin.getDataFolder(), "shops.yml");
        if (!shopFile.exists()) {
            createDefaultShop();
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(shopFile);
        shopItems.clear();
        
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    ShopItem item = new ShopItem(
                        itemSection.getString("material"),
                        itemSection.getInt("amount", 1),
                        itemSection.getDouble("buy-price", 0.0),
                        itemSection.getDouble("sell-price", 0.0),
                        itemSection.getString("display-name", key),
                        itemSection.getInt("slot", 0)
                    );
                    shopItems.add(item);
                }
            }
        }
    }
    
    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        
        int index = 0;
        for (ShopItem item : shopItems) {
            String path = "items." + index;
            config.set(path + ".material", item.getMaterial());
            config.set(path + ".amount", item.getAmount());
            config.set(path + ".buy-price", item.getBuyPrice());
            config.set(path + ".sell-price", item.getSellPrice());
            config.set(path + ".display-name", item.getDisplayName());
            config.set(path + ".slot", item.getSlot());
            index++;
        }
        
        try {
            config.save(shopFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save shop data!");
            e.printStackTrace();
        }
    }
    
    private void createDefaultShop() {
        shopItems.add(new ShopItem("DIAMOND", 1, 100.0, 50.0, "§bDiamond", 10));
        shopItems.add(new ShopItem("IRON_INGOT", 16, 80.0, 40.0, "§7Iron Ingot", 11));
        shopItems.add(new ShopItem("GOLD_INGOT", 16, 120.0, 60.0, "§eGold Ingot", 12));
        shopItems.add(new ShopItem("EMERALD", 1, 150.0, 75.0, "§aEmerald", 13));
        shopItems.add(new ShopItem("COAL", 32, 50.0, 25.0, "§8Coal", 14));
        shopItems.add(new ShopItem("DIAMOND_SWORD", 1, 500.0, 250.0, "§bDiamond Sword", 15));
        shopItems.add(new ShopItem("DIAMOND_PICKAXE", 1, 600.0, 300.0, "§bDiamond Pickaxe", 16));
        save();
    }
    
    public List<ShopItem> getShopItems() {
        return new ArrayList<>(shopItems);
    }
}