package com.xenodev.xeno.listener;

import com.xenodev.xeno.Xeno;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    
    private final Xeno plugin;
    
    public PlayerQuitListener(Xeno plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getEconomyManager().unloadPlayer(player.getUniqueId());
    }
}