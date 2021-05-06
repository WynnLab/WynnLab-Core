package com.wynnlab.spells

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class SpellPlayer(
    val player: Player
) {
    fun getLocation() = player.location.clone()
    fun getEyeLocation() = player.eyeLocation.clone()
    fun getDirection() = player.eyeLocation.direction.clone()

    fun damage(e: LivingEntity, melee: Boolean, multiplier: Double) =
        this.damage(e, melee, multiplier, *doubleArrayOf())

    fun damage(e: LivingEntity, melee: Boolean, multiplier: Double, vararg conversion: Double) =
        damage(player, e, melee, multiplier, *conversion)

    @Suppress("unused")
    fun damageBy(e: LivingEntity, melee: Boolean, multiplier: Double) =
        this.damageBy(e, melee, multiplier, *doubleArrayOf())

    @Suppress("unused")
    fun damageBy(e: LivingEntity, melee: Boolean, multiplier: Double, vararg conversion: Double) =
        damageBy(player, e, melee, multiplier, *conversion)

    fun heal(amount: Double) =
        heal(player, amount)

    fun knockback(target: Entity, direction: Vector, amount: Double) =
        com.wynnlab.spells.knockback(target, direction, amount)

    @Suppress("unused")
    fun knockback(target: Entity, amount: Double) =
        knockbackFromPlayer(target, player, amount)

    fun particle(location: Location, particle: Particle, count: Int, offX: Double, offY: Double, offZ: Double, speed: Double) =
        particle(player, location, particle, count, offX, offY, offZ, speed)

    fun <T> particle(location: Location, particle: Particle, count: Int, offX: Double, offY: Double, offZ: Double, speed: Double, data: T?) =
        particle(player, location, particle, count, offX, offY, offZ, speed, data)

    fun sound(sound: Sound, volume: Float, pitch: Float) =
        sound(player, sound, volume, pitch)

    fun sound(location: Location, sound: Sound, volume: Float, pitch: Float) =
        sound(player, location, sound, volume, pitch)

    fun nearbyMobs(x: Double, y: Double, z: Double) =
        nearbyMobs(player, x, y, z)

    @Suppress("unchecked_cast")
    fun nearbyMobs(location: Location, x: Double, y: Double, z: Double): Collection<Mob> =
        nearbyMobs(player.world, location, x, y, z)

    @Suppress("unused")
    fun nearbyMobsAndTag(location: Location, x: Double, y: Double, z: Double, tag: String): Collection<Entity> =
        nearbyMobsAndTag(player.world, location, x, y, z, tag)

    fun castSpell(clazz: String, index: Int, vararg args: Any?) =
        castSpell(player, clazz, index, args)

    @Suppress("unused")
    fun message(message: List<String>) = player.sendMessage(message.joinToString(""))
}