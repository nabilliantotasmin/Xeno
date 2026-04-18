package com.xenodev.xeno.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageManager {
    
    private final Map<UUID, UUID> lastMessaged;
    
    public MessageManager() {
        this.lastMessaged = new HashMap<>();
    }
    
    public void setLastMessaged(UUID sender, UUID receiver) {
        lastMessaged.put(sender, receiver);
        lastMessaged.put(receiver, sender);
    }
    
    public UUID getLastMessaged(UUID player) {
        return lastMessaged.get(player);
    }
    
    public boolean hasLastMessaged(UUID player) {
        return lastMessaged.containsKey(player);
    }
}