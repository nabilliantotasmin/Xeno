package com.xenodev.xeno.command;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.inventory.impl.ShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    
    private final Xeno plugin;
    
    public ShopCommand(Xeno plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        ShopGUI shopGUI = new ShopGUI(plugin);
        plugin.getGuiManager().openGUI(shopGUI, player);
        
        return true;
    }
}