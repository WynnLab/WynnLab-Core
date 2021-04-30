package com.wynnlab.listeners

import org.bukkit.entity.Arrow
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

class ProjectileHitListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onProjectileHit(e: ProjectileHitEvent) {
        val proj = e.entity

        if ("mob_projectile" in proj.scoreboardTags)
            proj.remove()

        if (proj.shooter !is Player)
            return

        for (tag in proj.scoreboardTags) {
            tags[tag]?.let {
                if (e.hitEntity is Mob) (e.hitEntity as Mob).noDamageTicks = 0
                if (proj is Arrow) proj.damage = 0.0
                it(e)
            }
        }
    }

    val tags = hashMapOf<String, (ProjectileHitEvent) -> Unit>()
}