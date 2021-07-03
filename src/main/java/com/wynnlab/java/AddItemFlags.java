package com.wynnlab.java;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class AddItemFlags {
    public static void addAllItemFlags(ItemMeta meta) {
        meta.addItemFlags(ItemFlag.values());
    }
}
