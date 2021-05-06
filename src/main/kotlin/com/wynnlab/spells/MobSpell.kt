package com.wynnlab.spells

import com.wynnlab.plugin
import com.wynnlab.python
import com.wynnlab.util.BaseSerializable
import com.wynnlab.util.ConfigurationDeserializable
import com.wynnlab.util.DEG2RAD
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Entity
import org.python.core.Py
import org.python.core.PyBoolean
import org.python.core.PyFunction
import java.io.File
import kotlin.math.cos
import kotlin.math.sin

data class MobSpell(
    val name: String,
    val maxTick: Int,
    val prepareTime: Int = 10,
    val hasBossBar: Boolean = false,

    val pyFunction: PyFunction,
) : BaseSerializable<MobSpell>() {
    val cooldown = prepareTime + maxTick + 20

    var bossBar: BossBar? = null

    fun newInstance(caster: Entity, target: Entity): Ticks {
        Bukkit.broadcastMessage("${caster.name} casted $name at ${target.name}")

        return object : Ticks(caster, target) {
            override fun tick() =
                (pyFunction.__call__(Py.java2py(this)) as PyBoolean).booleanValue
        }
    }

    fun spellEffects(caster: Entity) {
        // Sound
        caster.world.playSound(caster.location, Sound.BLOCK_PORTAL_TRAVEL, .5f, 2f)

        // Particles
        val particles = object : Runnable {
            var taskId = 0
            private var n = 3
            override fun run() {
                if (n > 0) {
                    --n
                    for (i in 0 until 360 step 30) {
                        caster.world.spawnParticle(Particle.FIREWORKS_SPARK, caster.location.clone().add(sin(i * DEG2RAD), 1.5, cos(i * DEG2RAD)), 1, .0, .0, .0, .0)
                    }
                }
                else
                    Bukkit.getScheduler().cancelTask(taskId)
            }
        }
        particles.taskId = Bukkit.getScheduler().runTaskTimer(plugin, particles, 0L, 20L).taskId

        // BossBar
        if (hasBossBar) {
           bossBar = if (hasBossBar) Bukkit.createBossBar(NamespacedKey(plugin, "prepare_${caster.entityId}"),
               "§5Preparing: §f$name", BarColor.PURPLE, BarStyle.SOLID, BarFlag.DARKEN_SKY) else null
        }
    }

    abstract class Ticks(
        val caster: Entity,
        val target: Entity
    ) {
        val data: Any? = null

        var t = 0

        abstract fun tick(): Boolean
    }

    override fun serialize(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override val deserializer = Companion

    companion object : ConfigurationDeserializable<MobSpell> {
        @[JvmStatic Suppress("unused", "unchecked_cast")]
        override fun deserialize(map: Map<String, Any?>): MobSpell {
            val name = map["name"] as String
            val maxTick = (map["max_tick"] as Number).toInt()
            val prepareTime = (map["prepare_time"] as Number ??: 10).toInt()
            val hasBossBar = map["has_boss_bar"] == true

            val pyFunctionFileName = map["script"] as String
            val pyFunctionFile = File(File(File(plugin.dataFolder, "mobs"), "scripts"), pyFunctionFileName)
            val pyCode = python.compile(pyFunctionFile.reader())
            python.exec(pyCode)
            val pyFunction = python.get("tick") as PyFunction

            return MobSpell(name, maxTick, prepareTime, hasBossBar, pyFunction).also { println(it) }
        }
    }
}