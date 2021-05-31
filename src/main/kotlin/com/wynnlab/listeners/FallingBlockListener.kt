package com.wynnlab.listeners

import com.wynnlab.wynnlab
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityChangeBlockEvent

class FallingBlockListener : BaseListener() {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onTouchGround(e: EntityChangeBlockEvent) {
        if (e.entity is FallingBlock) {
            if (e.to == Material.PACKED_ICE || e.to == Material.OBSIDIAN || e.to == Material.DIRT || e.to == Material.GLOWSTONE) {
                e.entity.world.spawnParticle(Particle.BLOCK_CRACK, e.entity.location, 16, 1.0, 0.25, 1.0, 1.0, e.to.createBlockData())
                val from = e.block.type
                Bukkit.getScheduler().runTaskLater(wynnlab, Runnable { e.block.type = from }, 1L)
                e.isCancelled = true
            }
        }
    }
}