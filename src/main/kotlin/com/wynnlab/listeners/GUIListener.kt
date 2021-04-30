package com.wynnlab.listeners

import com.wynnlab.api.*
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GUIListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked as? Player ?: return

        if (player.gameMode != GameMode.ADVENTURE)
            return

        val clickedInventory = e.clickedInventory ?: return
        val playerInventory = player.inventory
        val upperInventory = e.inventory

        val slot = e.slot

        e.currentItem?.let { item ->
            if (upperInventory != playerInventory && upperInventory == clickedInventory && e.isShiftClick) {
                val barriers = booleanArrayOf(false, false, false, false)

                while (playerInventory.firstEmpty() in 9..12) {
                    val fe = playerInventory.firstEmpty()
                    playerInventory.setItem(fe, barrier)
                    barriers[fe - 9] = true
                }

                playerInventory.addItem(item)
                barriers.forEachIndexed { i, b -> if (b) playerInventory.setItem(i + 9, null) }
                upperInventory.setItem(slot, null)

                e.isCancelled = true
            } else if (player.openInventory.type == InventoryType.CRAFTING) {
                when (val type = item.itemMeta?.data?.getString("type")) {
                    "RING", "BRACELET", "NECKLACE" -> {
                        if (e.isShiftClick) {
                            e.isCancelled = when (slot) {
                                9, 10, 11, 12 -> if (clickedInventory == playerInventory) shiftOutAccessory(playerInventory, slot) else
                                    shiftInAccessory(type, playerInventory, slot)
                                else -> shiftInAccessory(type, playerInventory, slot)
                            }
                        }
                    }
                }
            }
        }

        if (clickedInventory == playerInventory) {
            when (slot) {
                // Prevent moving hotbar action items
                6, 7, 8 -> e.isCancelled = true

                // Magic pouch
                13 -> {
                    if (e.cursor != null && !e.cursor!!.itemMeta.data.getBoolean("pouch")) {
                        player.playSound(player.location, Sound.ENTITY_HORSE_SADDLE, 1f, .9f)
                        player.updatePouch(e.cursor)
                        e.cursor = null
                    } else {
                        player.showPouch()
                    }
                    e.isCancelled = true
                }
            }
        } else if (e.view.topInventory == clickedInventory) {
            inventories[e.view.title]?.invoke(e)
        }

        (player as? Player)?.testInventory()
    }

    val inventories = hashMapOf<String, (InventoryClickEvent) -> Unit>()

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        inventories.remove(e.view.title)
    }

    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        if (e.player.inventory.heldItemSlot in 6..8 && e.player.gameMode == GameMode.ADVENTURE) e.isCancelled = true
    }

    @EventHandler
    fun onPlayerSwapItem(e: PlayerSwapHandItemsEvent) {
        if (e.player.inventory.heldItemSlot in 6..8 && e.player.gameMode == GameMode.ADVENTURE) e.isCancelled = true
    }

    private fun shiftOutAccessory(inv: Inventory, slot: Int): Boolean {
        val itemToMove = inv.getItem(slot)
        val barriers = booleanArrayOf(false, false, false, false)
        while (inv.firstEmpty() in 9..12) {
            val fe = inv.firstEmpty()
            inv.setItem(fe, barrier)
            barriers[fe - 9] = true
        }
        itemToMove?.let { inv.addItem(it) }
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
        val barrier = ItemStack(Material.SNOW)
    }
}