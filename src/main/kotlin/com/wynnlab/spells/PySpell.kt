package com.wynnlab.spells

import com.wynnlab.api.*
import com.wynnlab.classes
import com.wynnlab.entities.Hologram
import com.wynnlab.plugin
import com.wynnlab.random
import com.wynnlab.util.normalizeOnXZ
import com.wynnlab.util.plus
import org.bukkit.*
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.Attribute
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
        fun registerProjectileHit(tag: String, e: (ProjectileHitEvent) -> Unit) {
            plugin.projectileHitListener.tags[tag] = e
        }

        @JvmStatic
        fun damage(source: Player, e: LivingEntity, melee: Boolean, multiplier: Double, vararg conversion: Double) {
            // LS / MS
            source.weaponAttackSpeed?.let { attackSpeed ->
                if (random.nextDouble() < attackSpeed.stealChance) {
                    val ls = source.getId("life_steal")
                    if (ls != 0) {
                        particle(source, source.location.clone().add(.0, 1.0, .0), if (ls > 0) Particle.HEART else Particle.DAMAGE_INDICATOR, 10, .5, 1.0, .5, .2)
                        heal(source, ls.toDouble())
                    }
                }
                if (random.nextDouble() < attackSpeed.stealChance) {
                    val ms = source.getId("mana_steal")
                    if (ms != 0) {
                        val l = source.location.clone()
                        if (ms > 0)
                            repeat(5) { i ->
                                l.add(if (i % 2 == 0) -.5 else .5, .2, if (i % 2 == 1) -.5 else .5)
                                particle(source, l, Particle.SPELL_MOB, 0, 1.0, 1.0, 1.0, 1.0)
                                l.add(if (i % 2 == 1) -.5 else .5, .2, if (i % 2 == 0) -.5 else .5)
                                particle(source, l, Particle.SPELL_MOB, 0, 0.0, 1.0, 1.0, 1.0)
                            }
                        else
                            repeat(5) { i ->
                                l.add(if (i % 2 == 0) -.5 else .5, .2, if (i % 2 == 1) -.5 else .5)
                                particle(source, l, Particle.SPELL_MOB, 0, 0.0, 0.0, 0.0, 1.0)
                                l.add(if (i % 2 == 1) -.5 else .5, .2, if (i % 2 == 0) -.5 else .5)
                                particle(source, l, Particle.SPELL_MOB, 0, 0.0, 0.0, 1.0, 1.0)
                            }
                        source.foodLevel = (source.foodLevel + ms).coerceIn(1, 20)
                    }
                }
            }

            // Exploding
            if (melee && random.nextDouble() < (source.getId("exploding") / 100.0 * multiplier)) {
                sound(source, e.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                particle(source, e.location.clone().add(.0, 1.0, .0), Particle.EXPLOSION_LARGE, 3, .5, 1.0, .5, .0)
                nearbyMobs(e.world, e.location, 2.0, 2.0, 2.0).forEach { if(e != it) damageBy(source, it, melee, multiplier / 3.0, *conversion) }
            }

            // Poison
            if (melee) {
                val poison = source.getId("poison").coerceAtLeast(0)
                if (poison > 0) {
                    val task = poisonTask(e, poison, source)
                    task.id = Bukkit.getScheduler().runTaskTimer(plugin, task, 0L, 20L).taskId
                }
            }

            // Damage
            damageBy(source, e, melee, multiplier, *conversion)
        }

        private fun poisonTask(e: LivingEntity, poison: Int, source: Player) = object : Runnable {
            @Volatile
            private var c = 0

            var id = 0

            private var l = e.location.clone()

            override fun run() {
                if (c < 2)
                    ++c
                else
                    Bukkit.getScheduler().cancelTask(id)

                l = e.location.clone()

                e.damage(poison / 3.0, source)
                repeat(10) { i ->
                    l.add(if (i % 2 == 0) -.5 else .5, .2, if (i % 2 == 1) -.5 else .5)
                    particle(source, l, Particle.SPELL_MOB, 0, .2, .8, .05, 1.0)
                }
            }
        }

        @JvmStatic
        fun damageBy(source: Player, e: LivingEntity, melee: Boolean, multiplier: Double, vararg conversion: Double) {
            val damage = source.getDamage(melee, multiplier, if (conversion.isNotEmpty()) doubleArrayOf(*conversion) else standardConversion)

            e.damage(damage.sum(), source)
            e.noDamageTicks = 0

            var space = false
            val damageText = buildString {
                damage.onEachIndexed { i, d ->
                    if (space)
                        append(' ')
                    val di = d.toInt()
                    space = if (di > 0) {
                        append('§')
                        append(when (i) {
                            0 -> '4'; 1 -> '2'; 2 -> 'e'; 3 -> 'b'; 4 -> 'c'; 5 -> 'f'; else -> '0'
                        })
                        append('-')
                        append(di)
                        append(when (i) {
                            0 -> '❤'; 1 -> '✤'; 2 -> '✦'; 3 -> '❉'; 4 -> '✹'; 5 -> '❋'; else -> 'x'
                        })
                        true
                    } else {
                        false
                    }
                }
            }

            val diLocation = e.eyeLocation.clone().add(random.nextDouble() * .5, random.nextDouble() * .5 + .5, random.nextDouble() * .5)
            val di = Hologram(diLocation, damageText)
            di.spawn(e.world)
            di.bukkitEntity.velocity = diLocation.clone().subtract(e.location).multiply(0.5).toVector()
            di.removeAfter(15)
        }

        @JvmStatic
        fun <Healable> heal(e: Healable, amount: Double) where Healable : LivingEntity, Healable : Attributable {
            e.health = (e.health + amount).coerceIn(1.0, e.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value)
        }

        @JvmStatic
        fun knockback(target: Entity, direction: Vector, amount: Double) {
            target.velocity = (target.velocity + direction.clone().normalizeOnXZ().multiply(amount).multiply(.225)).add(direction.clone().normalize().multiply(.25))
        }

        @JvmStatic
        fun knockbackFromPlayer(target: Entity, player: Player, amount: Double) {
            knockback(target, player.eyeLocation.direction.add(player.velocity).multiply(.5), amount)
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

        @[JvmStatic Suppress("unchecked_cast")]
        fun nearbyMobs(world: World, location: Location, x: Double, y: Double, z: Double): Collection<Mob> =
            world.getNearbyEntities(location, x, y, z) { it !is Player && it is Mob } as Collection<Mob>

        @[JvmStatic Suppress("unused")]
        fun nearbyMobsAndTag(world: World, location: Location, x: Double, y: Double, z: Double, tag: String): Collection<Entity> =
            world.getNearbyEntities(location, x, y, z) { it !is Player && (it is Mob || it.scoreboardTags.contains(tag)) }

        @JvmStatic
        fun castSpell(player: Player, clazz: String, index: Int, vararg args: Any?) = classes[clazz]?.spells?.get(index)
            ?.cast(player, *args)

        @[JvmStatic Suppress("unused")]
        fun colorText(text: String, color: Char) = "§$color$text"
    }
}