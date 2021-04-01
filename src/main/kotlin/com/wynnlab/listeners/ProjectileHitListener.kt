package com.wynnlab.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

class ProjectileHitListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onProjectileHit(e: ProjectileHitEvent) {
        val proj = e.entity
        if (proj.shooter !is Player)
            return

        for (tag in proj.scoreboardTags) {
            tags[tag]?.let { it(e) }
        }
    }

    companion object {
        val tags = hashMapOf<String, (ProjectileHitEvent) -> Unit>()
    }
}