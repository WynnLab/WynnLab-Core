package com.wynnlab.spells.mage

import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellData
import com.wynnlab.util.LocationIterator
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Damageable
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

class Meteor(player: Player) : Spell(player, 61, SpellData.METEOR) {
    private lateinit var target: Location
    private lateinit var origin: Location
    private lateinit var direction: Vector
    private val random = Random()

    override fun tick() {
        if (tick == 0) {
            val ray = player.rayTraceBlocks(21.0)
            var rayLoc = if (ray == null || ray.hitBlock == null) this.player.eyeLocation.clone()
                .add(this.player.eyeLocation.direction.clone().multiply(21)) else ray.hitBlock!!
                .location
            if (ray != null && ray.hitEntity != null) {
                rayLoc = ray.hitEntity!!.location
            }
            for (e in rayLoc.getNearbyEntities(7.0, 7.0, 7.0)) {
                if (e is Player)
                    continue
                if (e is Mob) {
                    if (!::target.isInitialized) {
                        target = e.getLocation()
                    } else if (rayLoc.distance(target) > e.getLocation().distance(target)) {
                        target = e.getLocation()
                    }
                }
            }
            if (!::target.isInitialized) {
                target = rayLoc
            }
            while (target.block.isPassable) {
                target.add(0.0, -1.0, 0.0)
            }
            origin = target.clone().add((random.nextFloat() * 5).toDouble(), 20.0, (random.nextFloat() * 5).toDouble())
            direction = origin.clone().subtract(target).toVector().normalize()
            for (loc in LocationIterator(target, origin, direction, 0.5)) {
                this.player.spawnParticle(Particle.FLAME, loc, 1, 0.0, 0.0, 0.0, 0.0)
            }
            direction.multiply(-1)
            if (clone) this.player.playSound(origin, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.5f, 0.5f)
        }
        var particleCount = 0
        when {
            tick < 5 -> particleCount = 4
            tick < 10 -> particleCount = 3
            tick < 15 -> particleCount = 2
            tick < 20 -> particleCount = 1
        }
        if (tick < 20) {
            val pLoc: Location = origin.clone().add(direction.clone().multiply(tick))
            this.player.spawnParticle(Particle.EXPLOSION_LARGE, pLoc, particleCount, 0.0, 0.0, 0.0, 0.1)
            this.player.spawnParticle(if (clone) Particle.SQUID_INK else Particle.CLOUD, pLoc, particleCount * 5, 0.0, 0.0, 0.0, 0.25)
            this.player.spawnParticle(if (clone) Particle.SPELL_WITCH else Particle.LAVA, pLoc, particleCount, 0.0, 0.0, 0.0, 0.25)
            if (tick == 19) {
                this.player.playSound(target, Sound.ENTITY_BLAZE_SHOOT, 5f, 1f)
                this.player.world.playSound(target, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 5f, 1f)
                this.player.world.playSound(target, Sound.ENTITY_GENERIC_EXPLODE, 5f, if (clone) 0.5f else 0.75f)
                if (clone) this.player.playSound(target, Sound.ENTITY_WITHER_DEATH, 1f, 0.75f)
                for (e in target.getNearbyEntities(3.0, 3.0, 3.0)) {
                    if (e is Player)
                        continue
                    if (e !is Mob)
                        continue
                    e.damage(15.0, this.player)
                    e.noDamageTicks = 0
                }
            }
        }
        if (tick >= 20) {
            if (tick % 10 == 0) {
                this.player.playSound(target, Sound.BLOCK_CAMPFIRE_CRACKLE, 2f, 1f)
                this.player.spawnParticle(
                    if (clone) Particle.SPELL_WITCH else Particle.FLAME,
                    target, 98, 7.0, 0.0, 7.0, 0.2
                )
                this.player.spawnParticle(Particle.SMOKE_NORMAL, target, 98, 7.0, 0.0, 7.0, 0.2)
            }
        }
    }
}