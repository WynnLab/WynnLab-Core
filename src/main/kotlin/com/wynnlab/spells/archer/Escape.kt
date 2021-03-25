package com.wynnlab.spells.archer

import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellData
import com.wynnlab.util.RAD2DEG
import com.wynnlab.util.minus
import com.wynnlab.util.plus
import com.wynnlab.util.times
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.lang.Double.min
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Escape(player: Player) : Spell(player, 2, SpellData.ESCAPE) {
    @Suppress("depreciation")
    override fun tick() {
        when (tick) {
            0 -> {
                if (!player.world.getBlockAt(player.location.minus(.0, 1.0, .0)).isEmpty ||
                        !player.world.getBlockAt(player.location.minus(.0, 2.0, .0)).isEmpty) {
                    player.addScoreboardTag("escape")
                    val eyeDir = player.eyeLocation.direction.clone()
                    player.velocity = eyeDir.setY(min(-.4 * abs(eyeDir.y), -.4)).multiply(-4)

                    player.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 1f, 1.2f)
                    player.spawnParticle(if (clone) Particle.VILLAGER_HAPPY else Particle.SQUID_INK, player.location,
                        if (clone) 10 else 5, .3, .3, .3, .2)
                    if (clone) player.spawnParticle(Particle.CLOUD, player.location, 5, .3, .3, .3, .1)
                }
            }
            else -> {
                if (!player.isOnGround) {
                    --tick // To not cancel

                    if (player.isSneaking)
                        player.velocity = player.velocity - Vector(.0, 1.0, .0)
                    else if (player.velocity.y < 0)
                        player.velocity.y = player.velocity.y * .7
                } else {
                    if (player.isSneaking) {
                        for (i in 0..360 step 60) {
                            for (j in 0..4) {
                                val l = player.location.plus(sin(i * RAD2DEG) * j, .0, cos(i * RAD2DEG) * j)
                                player.spawnParticle(Particle.SQUID_INK, l, 2, .0, .0, .0, .2)
                                player.spawnParticle(Particle.CLOUD, l, 2, .0, .0, .0, .2)
                                player.spawnParticle(Particle.CRIT, l, 2, .0, .0, .0, .3)
                            }
                        }
                        player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, .5f, 1.2f)
                        player.playSound(player.location, Sound.ENTITY_IRON_GOLEM_DEATH, 1f, 1f)

                        for (e in player.getNearbyEntities(8.0, 8.0, 8.0)) {
                            if (e is Player)
                                continue
                            if (e !is Mob)
                                continue
                            e.damage(4.0, player)
                            e.noDamageTicks = 0
                        }
                    }
                    player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 3600, 2, true, false, true))
                }
            }
        }
    }
}