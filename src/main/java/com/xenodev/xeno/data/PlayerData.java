package com.xenodev.xeno.data;

import lombok.Data;
import java.util.*;

@Data
public class PlayerData {
    private UUID uuid;
    private String name;
    private double balance;
    private String role;
    private Map<String, Home> homes;
    private long lastTeleport;
    
    public PlayerData(UUID uuid, String name, double startingBalance, String defaultRole) {
        this.uuid = uuid;
        this.name = name;
        this.balance = startingBalance;
        this.role = defaultRole;
        this.homes = new HashMap<>();
        this.lastTeleport = 0;
    }
    
    public void addHome(String name, Home home) {
        this.homes.put(name.toLowerCase(), home);
    }
    
    public void removeHome(String name) {
        this.homes.remove(name.toLowerCase());
    }
    
    public Home getHome(String name) {
        return this.homes.get(name.toLowerCase());
    }
    
    public boolean hasHome(String name) {
        return this.homes.containsKey(name.toLowerCase());
    }
    
    public int getHomeCount() {
        return this.homes.size();
    }
}