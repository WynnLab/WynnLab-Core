package com.wynnlab.spells.mage

import com.wynnlab.spells.SpellL
import com.wynnlab.spells.SpellData
import org.bukkit.entity.Player
import org.bukkit.Sound
import org.bukkit.entity.Mob
import com.wynnlab.util.LocationIterator
import org.bukkit.Particle

class Teleport(player: Player) : SpellL(player, 1, SpellData.TELEPORT) {
    override fun tick() {
        val ray = player.rayTraceBlocks(14.0)
        val target = if (ray == null || ray.hitBlock == null) player.location.clone()
            .add(player.eyeLocation.direction.clone().multiply(14)) else ray.hitPosition.toLocation(player.world)
        while (!target.block.isPassable) {
            target.add(player.eyeLocation.direction.clone().multiply(-1))
        }
        target.direction = player.eyeLocation.direction
        for (loc in LocationIterator(player.eyeLocation.clone(), target, player.eyeLocation.direction.clone(), 0.5)) {
            player.spawnParticle(
                if (clone) Particle.DRIP_LAVA else Particle.FLAME,
                if (clone) loc.clone().add(0.0, -1.0, 0.0) else loc,
                1, 0.0, 0.0, 0.0, 0.0
            )
        }
        for (loc in LocationIterator(player.eyeLocation.clone(), target, player.eyeLocation.direction.clone(), 1.0)) {
            player.spawnParticle(if (clone) Particle.VILLAGER_ANGRY else Particle.LAVA, loc, 1, 0.0, 0.0, 0.0, 0.0)
            for (e in loc.getNearbyEntities(0.5, 0.5, 0.5)) {
                if (e is Player)
                    continue
                if (e !is Mob)
                    continue
                e.damage(2.0, player)
                e.noDamageTicks = 0
            }
        }
        player.playSound(target, if (clone) Sound.ENTITY_ENDERMAN_TELEPORT else Sound.ENTITY_SHULKER_TELEPORT, 1f, 1f)
        player.teleport(target)
    }
}