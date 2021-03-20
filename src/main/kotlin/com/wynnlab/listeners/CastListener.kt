package com.wynnlab.listeners

import com.wynnlab.WynnClass
import com.wynnlab.api.getWynnClass
import com.wynnlab.events.CastEvent
import com.wynnlab.plugin
import com.wynnlab.spells.Spell
import com.wynnlab.spells.mage.Meteor
import com.wynnlab.util.bukkitRunnable
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import kotlin.reflect.KClass

class CastListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onSpellCast(e: CastEvent) {
        if (e.spellId > 0)
            e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f)
        e.player.getWynnClass()?.let { castSpell(e.player, WynnClass.valueOf(it.toUpperCase()).spells[e.spellId]) }
    }
}

private fun castSpell(player: Player, spell: KClass<out Spell>) {
    val instance = spell.java.getConstructor(Player::class.java).newInstance(player)!!
    instance.schedule()
}