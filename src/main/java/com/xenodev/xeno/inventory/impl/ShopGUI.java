package com.xenodev.xeno.inventory.impl;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.ShopItem;
import com.xenodev.xeno.inventory.InventoryButton;
import com.xenodev.xeno.inventory.InventoryGUI;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class ShopGUI extends InventoryGUI {
    
    private final Xeno plugin;
    
    public ShopGUI(Xeno plugin) {
        this.plugin = plugin;
    }
    
    @Override
    protected Inventory createInventory() {
        String title = plugin.getConfigManager().getConfig().getString("shop.gui-title", "&6&lShop").replace("&", "§");
        return Bukkit.createInventory(null, 54, title);
    }
    
    @Override
    public void decorate(Player player) {
        for (ShopItem shopItem : plugin.getShopManager().getShopItems()) {
            this.addButton(shopItem.getSlot(), new InventoryButton()
                .creator(p -> createShopIcon(shopItem))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    ClickType clickType = event.getClick();
                    
                    if (clickType.isLeftClick()) {
                        handleBuy(clicker, shopItem);
                    } else if (clickType.isRightClick()) {
                        handleSell(clicker, shopItem);
                    }
                })
            );
        }
        
        super.decorate(player);
    }
    
    private ItemStack createShopIcon(ShopItem shopItem) {
        ItemStack item = XMaterial.matchXMaterial(shopItem.getMaterial())
            .map(XMaterial::parseItem)
            .orElse(new ItemStack(XMaterial.STONE.parseMaterial()));
        
        if (item == null) {
            item = new ItemStack(XMaterial.STONE.parseMaterial());
        }
        
        item.setAmount(shopItem.getAmount());
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(shopItem.getDisplayName().replace("&", "§"));
            
            List<String> lore = new ArrayList<>();
            lore.add("");
            
            if (shopItem.getBuyPrice() > 0) {
                String symbol = plugin.getConfigManager().getCurrencySymbol();
                lore.add("§7Buy Price: §a" + symbol + String.format("%.2f", shopItem.getBuyPrice()));
                lore.add("§7Left-click to buy");
            }
            
            if (shopItem.getSellPrice() > 0) {
                String symbol = plugin.getConfigManager().getCurrencySymbol();
                lore.add("§7Sell Price: §e" + symbol + String.format("%.2f", shopItem.getSellPrice()));
                lore.add("§7Right-click to sell");
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private void handleBuy(Player player, ShopItem shopItem) {
        if (shopItem.getBuyPrice() <= 0) {
            player.sendMessage("§cThis item is not available for purchase!");
            return;
        }
        
        if (!plugin.getEconomyManager().hasBalance(player.getUniqueId(), shopItem.getBuyPrice())) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("insufficient-funds"));
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        ItemStack item = XMaterial.matchXMaterial(shopItem.getMaterial())
            .map(XMaterial::parseItem)
            .orElse(null);
        
        if (item == null) {
            player.sendMessage("§cFailed to create item!");
            return;
        }
        
        item.setAmount(shopItem.getAmount());
        
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cYour inventory is full!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        plugin.getEconomyManager().withdraw(player.getUniqueId(), shopItem.getBuyPrice());
        player.getInventory().addItem(item);
        
        String symbol = plugin.getConfigManager().getCurrencySymbol();
        player.sendMessage(plugin.getConfigManager().getPrefix() + "§aPurchased §e" + shopItem.getDisplayName().replace("&", "§") + " §afor §e" + symbol + String.format("%.2f", shopItem.getBuyPrice()));
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
    }
    
    private void handleSell(Player player, ShopItem shopItem) {
        if (shopItem.getSellPrice() <= 0) {
            player.sendMessage("§cThis item cannot be sold!");
            return;
        }
        
        XMaterial xMaterial = XMaterial.matchXMaterial(shopItem.getMaterial()).orElse(null);
        if (xMaterial == null) {
            player.sendMessage("§cInvalid item!");
            return;
        }
        
        int count = 0;
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && XMaterial.matchXMaterial(invItem.getType()) == xMaterial) {
                count += invItem.getAmount();
            }
        }
        
        if (count < shopItem.getAmount()) {
            player.sendMessage("§cYou don't have enough of this item!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        int remaining = shopItem.getAmount();
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && XMaterial.matchXMaterial(invItem.getType()) == xMaterial) {
                if (invItem.getAmount() >= remaining) {
                    invItem.setAmount(invItem.getAmount() - remaining);
                    remaining = 0;
                    break;
                } else {
                    remaining -= invItem.getAmount();
                    invItem.setAmount(0);
                }
            }
        }
        
        plugin.getEconomyManager().deposit(player.getUniqueId(), shopItem.getSellPrice());
        
        String symbol = plugin.getConfigManager().getCurrencySymbol();
        player.sendMessage(plugin.getConfigManager().getPrefix() + "§aSold §e" + shopItem.getDisplayName().replace("&", "§") + " §afor §e" + symbol + String.format("%.2f", shopItem.getSellPrice()));
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
    }
}