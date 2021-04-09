package com.wynnlab.spells

import com.wynnlab.api.data
import com.wynnlab.api.getInt
import com.wynnlab.listeners.ProjectileHitListener
import com.wynnlab.plugin
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

abstract class PySpell : Runnable {
    lateinit var player: Player
    var t = 0

    private var taskId = -1
    private var scheduled = false
    var maxTick = 0

    open fun init() {}

    abstract fun tick()

    fun delay() { --t }

    fun cancel() {
        Bukkit.getScheduler().cancelTask(taskId)
    }

    final override fun run() {
        if (scheduled) {
            if (t <= maxTick) {
                tick()
                ++t
            } else {
                cancel()
            }
        }
    }

    fun schedule() {
        init()
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 1L).taskId
        scheduled = true
    }

    ///////////////////////////////////////////////////////////////////////////
    // Util functions
    ///////////////////////////////////////////////////////////////////////////
    
    fun damage(e: LivingEntity, amount: Double) = damage(player, e, amount)
    
    fun particle(location: Location, particle: Particle, count: Int, offX: Double, offY: Double, offZ: Double, speed: Double) =
        particle(player, location, particle, count, offX, offY, offZ, speed, null)
    
    fun <T> particle(location: Location, particle: Particle, count: Int, offX: Double, offY: Double, offZ: Double, speed: Double, data: T?) =
        particle(player, location, particle, count, offX, offY, offZ, speed, data)

    fun sound(sound: Sound, volume: Float, pitch: Float) = sound(player, player.location, sound, volume, pitch)
    
    fun sound(location: Location, sound: Sound, volume: Float, pitch: Float) = sound(player, location, sound, volume, pitch)

    @Suppress("unused")
    fun nearbyMobs(x: Double, y: Double, z: Double) = nearbyMobs(player, x, y, z)

    @Suppress("unused")
    fun nearbyMobs(location: Location, x: Double, y: Double, z: Double) =
        nearbyMobs(player.world, location, x, y, z)

    companion object {
        @[JvmStatic Suppress("unused")]
        fun registerProjectileHit(tag: String, e: (ProjectileHitEvent) -> Unit) {
            plugin.projectileHitListener.tags[tag] = e
        }

        @JvmStatic
        fun damage(source: Entity, e: LivingEntity, amount: Double) {
            e.damage(amount, source)
            e.noDamageTicks = 0
        }

        @JvmStatic
        fun particle(player: Player, location: Location, particle: Particle, count: Int, offX: Double, offY: Double, offZ: Double, speed: Double) =
            particle(player, location, particle, count, offX, offY, offZ, speed, null)

        @JvmStatic
        fun <T> particle(player: Player, location: Location, particle: Particle, count: Int, offX: Double, offY: Double, offZ: Double, speed: Double, data: T?) {
            for (p in player.world.getNearbyEntities(location, 25.0, 25.0, 25.0) { it is Player }) {
                p as Player
                val pLevel = if (p == player) p.data.getInt("particles") ?: 2 else p.data.getInt("other_particles") ?:
                p.data.getInt("particles") ?: 2
                p.spawnParticle(particle, location, (count * (pLevel / 2f)).toInt(), offX, offY, offZ, speed, data)
            }
        }

        @JvmStatic
        fun sound(player: Player, sound: Sound, volume: Float, pitch: Float) = sound(player, player.location, sound, volume, pitch)

        @JvmStatic
        fun sound(player: Player, location: Location, sound: Sound, volume: Float, pitch: Float) {
            player.world.playSound(location, sound, volume, pitch)
        }

        @JvmStatic
        fun nearbyMobs(player: Player, x: Double, y: Double, z: Double) = nearbyMobs(player.world, player.location, x, y, z)

        @JvmStatic
        @Suppress("unchecked_cast")
        fun nearbyMobs(world: World, location: Location, x: Double, y: Double, z: Double): Collection<Mob> =
            world.getNearbyEntities(location, x, y, z) { it !is Player && it is Mob } as Collection<Mob>
    }
}