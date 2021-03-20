package com.wynnlab.listeners

import com.wynnlab.api.addLeftClick
import com.wynnlab.api.addRightClick
import com.wynnlab.api.hasWeaponInHand
import org.bukkit.Effect
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
        if (!e.player.hasWeaponInHand())
            return
        e.player.addLeftClick()
        e.player.playEffect(e.player.location, Effect.CLICK1, null)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onRightClick(e: PlayerInteractEvent) {
        if (e.player.gameMode != GameMode.ADVENTURE)
            return
        if (e.action != Action.RIGHT_CLICK_BLOCK && e.action != Action.RIGHT_CLICK_AIR)
            return
        if (e.hand != EquipmentSlot.HAND)
            return
        if (!e.player.hasWeaponInHand())
            return
        e.player.addRightClick()
        e.player.playEffect(e.player.location, Effect.CLICK1, null)
    }
}