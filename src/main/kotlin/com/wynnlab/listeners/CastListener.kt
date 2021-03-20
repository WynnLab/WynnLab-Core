package com.wynnlab.listeners

import com.wynnlab.events.CastEvent
import com.wynnlab.plugin
import com.wynnlab.spells.mage.Meteor
import com.wynnlab.util.bukkitRunnable
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable

class CastListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onSpellCast(e: CastEvent) {
        if (e.spellId > 0)
            e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f)
        val meteor = Meteor(e.player)
        meteor.schedule()
    }
}