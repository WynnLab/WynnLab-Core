package com.wynnlab.listeners

import com.wynnlab.api.data
import com.wynnlab.api.getString
import com.wynnlab.api.showPouch
import com.wynnlab.api.updatePouch
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GUIListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked.gameMode != GameMode.ADVENTURE)
            return

        val inventory = e.clickedInventory ?: return

        e.currentItem?.let { item ->
            when (val type = item.itemMeta?.data?.getString("type")) {
                "RING", "BRACELET", "NECKLACE" -> {
                    if (e.isShiftClick) {
                        e.isCancelled = when (e.slot) {
                            9, 10, 11, 12 -> if (inventory == e.whoClicked.inventory) shiftOutAccessory(e.whoClicked.inventory, e.slot) else
                                shiftInAccessory(type, e.whoClicked.inventory, e.slot)
                            else -> shiftInAccessory(type, e.whoClicked.inventory, e.slot)
                        }
                    }
                }
            }
        }

        if (inventory == e.whoClicked.inventory) {
            when (e.slot) {
                // Prevent moving hotbar action items
                6, 7, 8 -> e.isCancelled = true

                // Magic pouch
                13 -> {
                    val player = e.whoClicked as? Player ?: return
                    if (e.cursor != null) {
                        player.playSound(player.location, Sound.ENTITY_HORSE_SADDLE, 1f, .9f)
                        player.updatePouch(e.cursor)
                        e.cursor = null
                    } else {
                        player.showPouch()
                    }
                    e.isCancelled = true
                }
            }
            return
        }

        if (e.view.topInventory != inventory)
            return

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

    private fun shiftOutAccessory(inv: Inventory, slot: Int): Boolean {
        val itemToMove = inv.getItem(slot)
        val barriers = booleanArrayOf(false, false, false, false)
        while (inv.firstEmpty() in 9..12) {
            val fe = inv.firstEmpty()
            inv.setItem(fe, barrier)
            barriers[fe - 9] = true
        }
        inv.setItem(inv.firstEmpty(), itemToMove)
        barriers.forEachIndexed { i, b -> if (b) inv.setItem(i + 9, null) }
        inv.setItem(slot, null)
        return false
    }

    private fun shiftInAccessory(type: String?, inv: Inventory, slot: Int): Boolean {
        var desiredSlot = when (type) {
            "RING" -> 9
            "BRACELET" -> 11
            "NECKLACE" -> 12
            else -> return true // Never
        }
        return if (inv.getItem(desiredSlot) == null || desiredSlot == 9 && (inv.getItem(10) == null).also { desiredSlot = 10 }) {
            inv.setItem(desiredSlot, inv.getItem(slot))
            inv.setItem(slot, null)
            true
        } else false
    }

    companion object {
        val barrier = ItemStack(Material.BARRIER)
    }
}