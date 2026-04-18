package com.xenodev.xeno.data;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Role {
    private String name;
    private String prefix;
    private String suffix;
    private List<String> permissions;
    private int priority;
    
    public Role(String name) {
        this.name = name;
        this.prefix = "";
        this.suffix = "";
        this.permissions = new ArrayList<>();
        this.priority = 0;
    }
    
    public boolean hasPermission(String permission) {
        return permissions.contains(permission) || permissions.contains("*");
    }
}