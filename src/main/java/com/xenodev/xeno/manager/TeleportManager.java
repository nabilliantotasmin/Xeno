package com.xenodev.xeno.manager;

import com.xenodev.xeno.Xeno;
import com.xenodev.xeno.data.PlayerData;
import com.xenodev.xeno.data.TeleportRequest;
import org.bukkit.entity.Player;
import java.util.*;

public class TeleportManager {
    
    private final Xeno plugin;
    private final Map<UUID, TeleportRequest> pendingRequests;
    
    public TeleportManager(Xeno plugin) {
        this.plugin = plugin;
        this.pendingRequests = new HashMap<>();
    }
    
    public void createRequest(Player requester, Player target) {
        TeleportRequest request = new TeleportRequest(
            requester.getUniqueId(),
            target.getUniqueId(),
            System.currentTimeMillis()
        );
        pendingRequests.put(target.getUniqueId(), request);
    }
    
    public TeleportRequest getRequest(UUID targetUUID) {
        TeleportRequest request = pendingRequests.get(targetUUID);
        if (request != null && request.isExpired(plugin.getConfigManager().getTeleportTimeout())) {
            pendingRequests.remove(targetUUID);
            return null;
        }
        return request;
    }
    
    public void removeRequest(UUID targetUUID) {
        pendingRequests.remove(targetUUID);
    }
    
    public boolean canTeleport(Player player) {
        PlayerData data = plugin.getEconomyManager().getPlayerData(player.getUniqueId());
        if (data == null) return true;
        
        long cooldown = plugin.getConfigManager().getTeleportCooldown() * 1000L;
        long timeSinceLastTP = System.currentTimeMillis() - data.getLastTeleport();
        return timeSinceLastTP >= cooldown;
    }
    
    public void updateLastTeleport(Player player) {
        PlayerData data = plugin.getEconomyManager().getPlayerData(player.getUniqueId());
        if (data != null) {
            data.setLastTeleport(System.currentTimeMillis());
        }
    }
}