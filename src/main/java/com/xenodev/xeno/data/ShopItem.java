package com.xenodev.xeno.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShopItem {
    private String material;
    private int amount;
    private double buyPrice;
    private double sellPrice;
    private String displayName;
    private int slot;
}