package com.wynnlab.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class GUIListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClick(e: InventoryClickEvent) {
        val inventory = e.clickedInventory ?: return

        // Prevent moving hotbar action items
        if (e.slot in 6..8) {
            e.isCancelled = true
            return
        }

        if (e.view.topInventory != inventory) return

        inventories[e.view.title]?.invoke(e)
    }

    val inventories = hashMapOf<String, (InventoryClickEvent) -> Unit>()

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        inventories.remove(e.view.title)
    }

    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        if (e.player.inventory.heldItemSlot in 6..8) e.isCancelled = true
    }

    @EventHandler
    fun onPlayerSwapItem(e: PlayerSwapHandItemsEvent) {
        if (e.player.inventory.heldItemSlot in 6..8) e.isCancelled = true
    }
}