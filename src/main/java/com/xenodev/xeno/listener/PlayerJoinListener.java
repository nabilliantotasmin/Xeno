package com.xenodev.xeno.listener;

import com.xenodev.xeno.Xeno;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    
    private final Xeno plugin;
    
    public PlayerJoinListener(Xeno plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getEconomyManager().loadPlayer(player);
    }
}