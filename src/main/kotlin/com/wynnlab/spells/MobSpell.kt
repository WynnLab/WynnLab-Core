package com.wynnlab.spells

import com.wynnlab.plugin
import com.wynnlab.util.DEG2RAD
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Entity
import kotlin.math.cos
import kotlin.math.sin

class MobSpell(
    val name: String,
    val cooldown: Int,
    val maxTick: Int
) {
    fun newInstance(caster: Entity, target: Entity): Ticks {
        Bukkit.broadcastMessage("${caster.name} casted $name at $target")
        return Ticks(caster, target)
    }

    private fun spellEffects(caster: Entity) {
        caster.world.playSound(caster.location, Sound.BLOCK_PORTAL_TRAVEL, .5f, 2f)
        /*Bukkit.getScheduler().runTaskTimer(plugin, object : Runnable {
            private var n = 2
            override fun run() {
                for (i in 0 until 360 step 30) {
                    caster.world.spawnParticle(Particle.FIREWORKS_SPARK, caster.location.clone().add(sin(i * DEG2RAD), 1.5, cos(i * DEG2RAD)), 1, .0, .0, .0, .0)
                }
            }
        }, 0L, 10L)*/
    }

    class Ticks(
        val caster: Entity,
        val target: Entity
    ) {
        internal var t = 0

        fun tick() {
            Bukkit.broadcastMessage("Tick $t")
        }
    }
}