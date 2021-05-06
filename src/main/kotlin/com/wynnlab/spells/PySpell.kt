package com.wynnlab.spells

import com.wynnlab.plugin
import org.bukkit.*
import org.bukkit.attribute.Attributable
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.Vector

abstract class PySpell : Runnable {
    lateinit var player: Player
    var t = 0

    private var _taskId = -1
    val taskId get() = _taskId

    private var scheduled = false
    var maxTick = 0

    open fun init() {}

    abstract fun tick()

    fun delay() { --t }

    fun cancel() {
        Bukkit.getScheduler().cancelTask(_taskId)
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
        _taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 1L).taskId
        scheduled = true
    }

    fun castSpell(clazz: String, index: Int, vararg args: Any?) = Companion.castSpell(player, clazz, index, *args)

    ///////////////////////////////////////////////////////////////////////////
    // Util functions
    ///////////////////////////////////////////////////////////////////////////
    
    fun damage(e: LivingEntity, melee: Boolean, multiplier: Double, vararg conversion: Double) = damage(player, e, melee, multiplier, *conversion)

    fun knockback(target: Entity, amount: Double) = knockbackFromPlayer(target, player, amount)
    
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
        fun registerProjectileHit(tag: String, e: (ProjectileHitEvent) -> Unit) =
            com.wynnlab.spells.registerProjectileHit(tag, e)

        @JvmStatic
        fun damage(source: Player, e: LivingEntity, melee: Boolean, multiplier: Double, vararg conversion: Double) =
            com.wynnlab.spells.damage(source, e, melee, multiplier, *conversion)

        @JvmStatic
        fun damageBy(source: Player, e: LivingEntity, melee: Boolean, multiplier: Double, vararg conversion: Double) =
            com.wynnlab.spells.damageBy(source, e, melee, multiplier, *conversion)

        @JvmStatic
        fun <Healable> heal(e: Healable, amount: Double) where Healable : LivingEntity, Healable : Attributable =
            com.wynnlab.spells.heal(e, amount)

        @JvmStatic
        fun knockback(target: Entity, direction: Vector, amount: Double) =
            com.wynnlab.spells.knockback(target, direction, amount)

        @JvmStatic
        fun knockbackFromPlayer(target: Entity, player: Player, amount: Double) =
            com.wynnlab.spells.knockbackFromPlayer(target, player, amount)

        @JvmStatic
        fun particle(player: Player, location: Location, particle: Particle, count: Int, offX: Double, offY: Double, offZ: Double, speed: Double) =
            com.wynnlab.spells.particle(player, location, particle, count, offX, offY, offZ, speed)

        @JvmStatic
        fun <T> particle(player: Player, location: Location, particle: Particle, count: Int, offX: Double, offY: Double, offZ: Double, speed: Double, data: T?) =
            com.wynnlab.spells.particle(player, location, particle, count, offX, offY, offZ, speed, data)

        @JvmStatic
        fun sound(player: Player, sound: Sound, volume: Float, pitch: Float) =
            com.wynnlab.spells.sound(player, sound, volume, pitch)

        @JvmStatic
        fun sound(player: Player, location: Location, sound: Sound, volume: Float, pitch: Float) =
            com.wynnlab.spells.sound(player, location, sound, volume, pitch)

        @JvmStatic
        fun nearbyMobs(player: Player, x: Double, y: Double, z: Double) =
            com.wynnlab.spells.nearbyMobs(player, x, y, z)

        @[JvmStatic Suppress("unchecked_cast")]
        fun nearbyMobs(world: World, location: Location, x: Double, y: Double, z: Double): Collection<Mob> =
            com.wynnlab.spells.nearbyMobs(world, location, x, y, z)

        @[JvmStatic Suppress("unused")]
        fun nearbyMobsAndTag(world: World, location: Location, x: Double, y: Double, z: Double, tag: String): Collection<Entity> =
            com.wynnlab.spells.nearbyMobsAndTag(world, location, x, y, z, tag)

        @JvmStatic
        fun castSpell(player: Player, clazz: String, index: Int, vararg args: Any?) =
            com.wynnlab.spells.castSpell(player, clazz, index, *args)

        @[JvmStatic Suppress("unused")]
        fun colorText(text: String, color: String) = com.wynnlab.spells.colorText(text, color)
    }
}