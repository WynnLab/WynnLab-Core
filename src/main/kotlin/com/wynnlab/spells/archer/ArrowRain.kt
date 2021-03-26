package com.wynnlab.spells.archer

import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellData
import com.wynnlab.util.RAD2DEG
import com.wynnlab.util.plus
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class ArrowRain(player: Player) : Spell(player, 11, SpellData.ARROW_SHIELD /*TODO*/) {
    override fun tick() {
        when {
            tick < 10 -> {
                for (i in 0..2) {
                    val l = player.location.plus(
                        sin((10.0 * tick + 60.0 * (i - 1.0)) * RAD2DEG) * (1.5 - .15 * tick),
                        tick.toDouble(),
                        cos((10.0 * tick + 60.0 * (i - 1.0)) * RAD2DEG) * (1.5 - .15 * tick)
                    )
                    player.spawnParticle(if (clone) Particle.FIREWORKS_SPARK else Particle.CRIT, l, 5, .0, .0, .0, if (clone) .1 else .3)
                    player.spawnParticle(Particle.SQUID_INK, l, 3, .0, .0, .0, .0)
                    player.playSound(l, Sound.ENTITY_ARROW_SHOOT, 1f, 1f + tick / 20f)
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