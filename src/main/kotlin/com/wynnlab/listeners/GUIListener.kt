package com.wynnlab.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUIListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClick(e: InventoryClickEvent) {
        val inventory = e.clickedInventory ?: return
    }
}