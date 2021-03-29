package com.wynnlab.spells.archer

import com.wynnlab.spells.SpellL
import com.wynnlab.spells.SpellData
import com.wynnlab.util.RAD2DEG
import com.wynnlab.util.plus
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class ArrowRain(player: Player) : SpellL(player, 11, SpellData.ARROW_SHIELD /*TODO*/) {
    override fun tick() {
        when {
            t < 10 -> {
                for (i in 0..2) {
                    val l = player.location.plus(
                        sin((10.0 * t + 60.0 * (i - 1.0)) * RAD2DEG) * (1.5 - .15 * t),
                        t.toDouble(),
                        cos((10.0 * t + 60.0 * (i - 1.0)) * RAD2DEG) * (1.5 - .15 * t)
                    )
                    player.spawnParticle(if (clone) Particle.FIREWORKS_SPARK else Particle.CRIT, l, 5, .0, .0, .0, if (clone) .1 else .3)
                    player.spawnParticle(Particle.SQUID_INK, l, 3, .0, .0, .0, .0)
                    player.playSound(l, Sound.ENTITY_ARROW_SHOOT, 1f, 1f + t / 20f)
                }
            }
            else -> {
                val l = player.location.plus(.0, 10.0, .0)
                player.playSound(l, Sound.ENTITY_ARROW_SHOOT, 1f, .8f)
                player.playSound(l, if (clone) Sound.ITEM_ARMOR_EQUIP_GOLD else Sound.ENTITY_GENERIC_EXPLODE, .8f, if (clone) 1f else 1.8f)
                if (clone) player.playSound(l, Sound.ITEM_TOTEM_USE, .8f, .8f)
                for (i in 0..360 step 30) {
                    for (j in 0..2) {
                        val arrow = player.world.spawnArrow(l, Vector(sin(i * RAD2DEG), .5 * (j - 1.0) - 1.0, cos(i * RAD2DEG)), 3f, 1f)
                        arrow.shooter = player
                        arrow.addScoreboardTag("rain_arrow")
                    }
                }
            }
        }
    }
}