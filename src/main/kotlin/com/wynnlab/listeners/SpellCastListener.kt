package com.wynnlab.listeners

import com.wynnlab.WynnClass
import com.wynnlab.api.cooldown
import com.wynnlab.api.getWynnClass
import com.wynnlab.api.isCloneClass
import com.wynnlab.api.updateActionBar
import com.wynnlab.events.SpellCastEvent
import com.wynnlab.spells.Spell
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import kotlin.reflect.KClass

class CastListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onSpellCast(e: SpellCastEvent) {
        val player = e.player
        val spell = player.getWynnClass()?.let {  WynnClass.valueOf(it.toUpperCase()).spells[e.spellId] } ?: return
        val instance = spell.java.getConstructor(Player::class.java).newInstance(player)!!

        if (e.spellId > 0) {
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f)
            player.updateActionBar(
                "${if (player.isCloneClass) instance.data.cloneSpellName else instance.data.spellName} Cast " +
                        "[-${instance.data.cost}M]"
            )
        } else {
            if (player.cooldown()) return
        }

        instance.schedule()
    }
}