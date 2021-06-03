package com.wynnlab.listeners

import com.wynnlab.api.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class GUIListener : BaseListener() {
    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked as? Player ?: return

        if (player.gameMode != GameMode.ADVENTURE)
            return

        val clickedInventory = e.clickedInventory ?: return
        val playerInventory = player.inventory
        //val upperInventory = e.inventory

        val slot = e.slot

        e.currentItem?.let { shiftAccessory(e, it, slot, player.openInventory, clickedInventory, playerInventory) }

        when (e.hotbarButton) {
            6, 7, 8 -> e.isCancelled = true
        }

        if (clickedInventory == playerInventory) {
            when (slot) {
                // Prevent moving hotbar action items
                6, 7, 8 -> e.isCancelled = true

                // Prevent from moving wrong items to accessory slots
                9, 10, 11, 12 -> protectAccessories(e, slot, playerInventory)

                // Magic pouch
                13 -> magicPouch(e, player, playerInventory)
            }
        } else if (e.view.topInventory == clickedInventory) {
            inventories[e.view.title]?.invoke(e)
        }

        (player as? Player)?.testInventory()
    }

    private fun shiftAccessory(e: InventoryClickEvent, item: ItemStack, slot: Int, openInventory: InventoryView, clickedInventory: Inventory, playerInventory: Inventory) {
        if (openInventory.type == InventoryType.CRAFTING && e.isShiftClick) {
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

    private fun protectAccessories(e: InventoryClickEvent, slot: Int, playerInventory: Inventory) {
        if (!e.isShiftClick) {
            val fromHotbar = e.hotbarButton in 0..5
            val newItem = (if (fromHotbar) playerInventory.getItem(e.hotbarButton) else e.cursor)

            val allowed = when (newItem?.itemMeta?.data?.getString("type")) {
                "RING" -> slot == 9 || slot == 10
                "BRACELET" -> slot == 11
                "NECKLACE" -> slot == 12
                else -> {
                    if (e.currentItem?.itemMeta?.data?.getString("type") != null) {
                        if (newItem == null || newItem.type == Material.AIR) {
                            e.cursor = e.currentItem
                            e.currentItem = snowForSlot(slot)
                        }
                        e.isCancelled = true
                    }
                    false
                }
            }
            if (allowed) {
                if (e.currentItem?.type != Material.SNOW)
                    e.isCancelled = false
                else {
                    e.isCancelled = true
                    e.currentItem = newItem
                    if (fromHotbar)
                        playerInventory.setItem(e.hotbarButton, null)
                    else
                        e.cursor = null
                }
            } else {
                e.isCancelled = true
            }
        }
    }

    private fun magicPouch(e: InventoryClickEvent, player: Player, playerInventory: Inventory) {
        if (e.hotbarButton in 0..5) {
            player.playSound(player.location, Sound.ENTITY_HORSE_SADDLE, 1f, .9f)

            player.updatePouch(playerInventory.getItem(e.hotbarButton))
            playerInventory.setItem(e.hotbarButton, null)
        } else if (e.cursor != null && e.cursor!!.itemMeta?.data?.getBoolean("pouch") == false) {
            player.playSound(player.location, Sound.ENTITY_HORSE_SADDLE, 1f, .9f)
            player.updatePouch(e.cursor)
            e.cursor = null
        } else {
            player.showPouch()
        }

        e.isCancelled = true
    }

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

        inv.setItem(slot, snowForSlot(slot))

        itemToMove?.let { inv.addItem(it) }

        return false
    }

    private fun shiftInAccessory(type: String?, inv: Inventory, slot: Int): Boolean {
        var desiredSlot = when (type) {
            "RING" -> 9
            "BRACELET" -> 11
            "NECKLACE" -> 12
            else -> return true // Never
        }
        return if (!( // Inverted because null item
            inv.getItem(desiredSlot)?.type != Material.SNOW
            && (desiredSlot != 9 || (inv.getItem(10)?.type != Material.SNOW).also { if (!it) desiredSlot = 10 })
        )) {
            inv.setItem(desiredSlot, inv.getItem(slot))
            inv.setItem(slot, null)
            true
        } else false
    }

    /*private fun shiftOtherItem(oldInventory: Inventory, newInventory: Inventory, slot: Int, item: ItemStack) {
        newInventory.setItem(slot, snowForSlot(slot))

        oldInventory.addItem(item)
    }*/

    companion object {
        val inventories = hashMapOf<String, (InventoryClickEvent) -> Unit>()

        fun snowForSlot(slot: Int) = ItemStack(Material.SNOW, 1).meta {
            displayName(Component.text(when (slot) {
                9 -> "§7Ring Slot§0"
                10 -> "§7Ring Slot§1"
                11 -> "§7Bracelet Slot"
                12 -> "§7Necklace Slot"
                else -> ""
            }, NamedTextColor.GRAY))
        }
    }
}