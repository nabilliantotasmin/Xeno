package com.xenodev.xeno.command;

import com.xenodev.xeno.Xeno;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class EconomyCommand implements CommandExecutor {
    
    private final Xeno plugin;
    
    public EconomyCommand(Xeno plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        
        if (cmd.equals("balance") || cmd.equals("bal") || cmd.equals("money")) {
            return handleBalance(sender, args);
        } else if (cmd.equals("pay")) {
            return handlePay(sender, args);
        } else if (cmd.equals("eco")) {
            return handleEco(sender, args);
        }
        
        return false;
    }
    
    private boolean handleBalance(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        double balance = plugin.getEconomyManager().getBalance(player.getUniqueId());
        String symbol = plugin.getConfigManager().getCurrencySymbol();
        
        sender.sendMessage(plugin.getConfigManager().getPrefix() + "§aYour balance: §e" + symbol + String.format("%.2f", balance));
        return true;
    }
    
    private boolean handlePay(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /pay <player> <amount>");
            return true;
        }
        
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null || !target.isOnline()) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                sender.sendMessage("§cAmount must be positive!");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount!");
            return true;
        }
        
        if (!plugin.getEconomyManager().hasBalance(player.getUniqueId(), amount)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("insufficient-funds"));
            return true;
        }
        
        plugin.getEconomyManager().transfer(player.getUniqueId(), target.getUniqueId(), amount);
        
        String symbol = plugin.getConfigManager().getCurrencySymbol();
        String paidMsg = plugin.getConfigManager().getMessage("economy-paid")
            .replace("{player}", target.getName())
            .replace("{amount}", symbol + String.format("%.2f", amount));
        String receivedMsg = plugin.getConfigManager().getMessage("economy-received")
            .replace("{player}", player.getName())
            .replace("{amount}", symbol + String.format("%.2f", amount));
        
        sender.sendMessage(plugin.getConfigManager().getPrefix() + paidMsg);
        target.sendMessage(plugin.getConfigManager().getPrefix() + receivedMsg);
        
        return true;
    }
    
    private boolean handleEco(CommandSender sender, String[] args) {
        if (!sender.hasPermission("xeno.admin.eco")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /eco <give|take|set> <player> <amount>");
            return true;
        }
        
        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);
        
        if (target == null || !target.isOnline()) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount!");
            return true;
        }
        
        UUID targetUUID = target.getUniqueId();
        String symbol = plugin.getConfigManager().getCurrencySymbol();
        
        switch (action) {
            case "give":
                plugin.getEconomyManager().deposit(targetUUID, amount);
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§aGave §e" + symbol + String.format("%.2f", amount) + " §ato §e" + target.getName());
                break;
            case "take":
                plugin.getEconomyManager().withdraw(targetUUID, amount);
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§aTook §e" + symbol + String.format("%.2f", amount) + " §afrom §e" + target.getName());
                break;
            case "set":
                plugin.getEconomyManager().setBalance(targetUUID, amount);
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§aSet §e" + target.getName() + "§a's balance to §e" + symbol + String.format("%.2f", amount));
                break;
            default:
                sender.sendMessage("§cUsage: /eco <give|take|set> <player> <amount>");
                break;
        }
        
        return true;
    }
}