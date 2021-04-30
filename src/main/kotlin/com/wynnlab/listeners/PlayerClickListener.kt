package com.wynnlab.listeners

import com.wynnlab.api.*
import com.wynnlab.gui.CompassGUI
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class PlayerClickListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onLeftClick(e: PlayerAnimationEvent) {
        if (e.player.gameMode != GameMode.ADVENTURE)
            return
        if (e.animationType != PlayerAnimationType.ARM_SWING)
            return

        e.player.inventory.itemInMainHand.itemMeta?.data?.getString("type")?.let {
            lcEvents[it]?.invoke(e)
        }

        if (!e.player.checkWeapon())
            return

        e.player.addLeftClick()

        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onRightClick(e: PlayerInteractEvent) {
        if (e.player.gameMode != GameMode.ADVENTURE)
            return
        if (e.action != Action.RIGHT_CLICK_BLOCK && e.action != Action.RIGHT_CLICK_AIR)
            return
        if (e.hand != EquipmentSlot.HAND)
            return

        when (e.player.inventory.heldItemSlot) {
            6 -> {
                CompassGUI(e.player).show()
                e.isCancelled = true
            }
            7 -> {
                e.isCancelled = true
            }
            8 -> {
                e.isCancelled = true
            }
            else -> {
                e.player.inventory.itemInMainHand.itemMeta?.data?.getString("type")?.let {
                    rcEvents[it]?.invoke(e)
                }

                if (!e.player.checkWeapon())
                    return

                e.player.addRightClick()
                e.isCancelled = true
            }
        }
    }

    val rcEvents = hashMapOf<String, (PlayerInteractEvent) -> Unit>(
        "HELMET" to { e ->
            val l = e.player.inventory.helmet
            e.player.inventory.helmet = e.player.inventory.itemInMainHand
            e.player.inventory.setItemInMainHand(l)
        },
        "CHESTPLATE" to { e ->
            val l = e.player.inventory.chestplate
            e.player.inventory.chestplate = e.player.inventory.itemInMainHand
            e.player.inventory.setItemInMainHand(l)
        },
        "LEGGINGS" to { e ->
            val l = e.player.inventory.leggings
            e.player.inventory.leggings = e.player.inventory.itemInMainHand
            e.player.inventory.setItemInMainHand(l)
        },
        "BOOTS" to { e ->
            val l = e.player.inventory.boots
            e.player.inventory.boots = e.player.inventory.itemInMainHand
            e.player.inventory.setItemInMainHand(l)
        },
        "RING" to { e ->
            val l = e.player.inventory.getItem(9)
            e.player.inventory.setItem(9, e.player.inventory.itemInMainHand)
            e.player.inventory.setItemInMainHand(l)
        },
        "BRACELET" to { e ->
            val l = e.player.inventory.getItem(11)
            e.player.inventory.setItem(11, e.player.inventory.itemInMainHand)
            e.player.inventory.setItemInMainHand(l)
        },
        "NECKLACE" to { e ->
            val l = e.player.inventory.getItem(12)
            e.player.inventory.setItem(12, e.player.inventory.itemInMainHand)
            e.player.inventory.setItemInMainHand(l)
        }
    )

    val lcEvents = hashMapOf<String, (PlayerAnimationEvent) -> Unit>()
}