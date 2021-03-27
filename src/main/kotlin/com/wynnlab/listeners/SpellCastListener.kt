package com.wynnlab.listeners

import com.wynnlab.WynnClassL
import com.wynnlab.api.cooldown
import com.wynnlab.api.getWynnClass
import com.wynnlab.api.isCloneClass
import com.wynnlab.api.updateActionBar
import com.wynnlab.events.SpellCastEvent
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class CastListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onSpellCast(e: SpellCastEvent) {
        val player = e.player
        val spellClass = player.getWynnClass()?.let {  WynnClassL.valueOf(it.toUpperCase()).spells[e.spellId] } ?: return
        val spell = spellClass.java.getConstructor(Player::class.java).newInstance(player)!!

        if (e.spellId > 0) {
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f)
            player.updateActionBar(
                "${if (player.isCloneClass) spell.data.cloneSpellName else spell.data.spellName} Cast " +
                        "§3[§b-${spell.data.cost}✺§3]"
            )
        } else {
            if (player.cooldown()) return
        }

        spell.schedule()
    }
}