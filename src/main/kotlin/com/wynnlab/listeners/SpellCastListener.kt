package com.wynnlab.listeners

import com.wynnlab.api.*
import com.wynnlab.classes
import com.wynnlab.events.SpellCastEvent
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class CastListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onSpellCast(e: SpellCastEvent) {
        val player = e.player
        val spellClass = player.getWynnClass()?.let { classes[it] } ?: return
        val spell = spellClass.spells[e.spellId]

        if (e.spellId > 0) {
            if (player.foodLevel > spell.cost) {
                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, .5f)
                player.updateActionBar(
                    "${player.getLocalizedText("classes.${player.getWynnClass()}.spells.${if (player.isCloneClass) "${spell.ordinal}c" else spell.ordinal.toString()}")} Cast " +
                            "§3[§b-${spell.cost}✺§3]"
                )
                player.foodLevel -= spell.cost
            } else {
                player.playSound(player.location, Sound.BLOCK_ANVIL_PLACE, 1f, 1f)
                player.updateActionBar("§4Not enough mana!")
                return
            }
        } else {
            if (player.cooldown()) return
        }

        spell.cast(player)
    }
}