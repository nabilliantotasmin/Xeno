package com.xenodev.xeno.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class Home {
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    
    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
    
    public static Home fromLocation(Location loc) {
        return new Home(
            loc.getWorld().getName(),
            loc.getX(),
            loc.getY(),
            loc.getZ(),
            loc.getYaw(),
            loc.getPitch()
        );
    }
}