package com.wynnlab.spells.archer

/*import com.wynnlab.api.data
import com.wynnlab.api.setString
import com.wynnlab.spells.SpellData
import com.wynnlab.util.RAD2DEG
import com.wynnlab.util.minus
import com.wynnlab.util.plus
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ArrowShield(player: Player) : SpellL(player, 601, SpellData.ARROW_SHIELD) {
    private lateinit var as1: ArmorStand
    private lateinit var as2: ArmorStand
    private lateinit var as3: ArmorStand

    private var remaining = 3
    private var cooldown = 0

    override fun tick() {
        when {
            t == 0 -> {
                if ("arrow_shield" !in player.scoreboardTags) {
                    player.addScoreboardTag("arrow_shield")
                    player.data.setInt("arrow_shield", 3)
                } else {
                    val arrows = player.data.getInt("arrow_shield")!!
                    player.data.setInt("arrow_shield", 3)
                    if (arrows <= 0)
                        player.removeScoreboardTag("arrow_shield")
                    return //TODO
                }

                player.playSound(player.location, if (clone) Sound.ITEM_ARMOR_EQUIP_NETHERITE else Sound.ENTITY_EVOKER_PREPARE_SUMMON, .5f, .9f)
                player.playSound(player.location, Sound.ENTITY_ARROW_SHOOT, 1f, .8f)
                player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f)

                as1 = player.world.spawn(player.location.clone().add(1.3, if (clone) .2 else .7, .75), ArmorStand::class.java)
                as2 = player.world.spawn(player.location.clone().add(-1.3, if (clone) .2 else .7, .75), ArmorStand::class.java)
                as3 = player.world.spawn(player.location.clone().add(.0, if (clone) .2 else .7, -1.5), ArmorStand::class.java)

                for (stand in arrayOf(as1, as2, as3)) {
                    stand.isVisible = false
                    stand.isSmall = true
                    stand.isInvulnerable = true
                    stand.isMarker = true
                    stand.addScoreboardTag("shield_arrow")
                    stand.data.setString("owner", player.name)
                    stand.equipment?.helmet = ItemStack(if (clone) Material.SHEARS else Material.ARROW)
                    stand.headPose = EulerAngle(.0, .0, PI * (if (clone) -.5 else -.75))
                }
            }
            t < 600 -> {
                as1.teleport(player.location.plus(sin((10.0 * t + 60.0) * -RAD2DEG) * 1.5, if (clone) .2 else .7, cos((10.0 * t + 60.0) * RAD2DEG) * 1.5))
                as2.teleport(player.location.plus(sin((10.0 * t - 60.0) * -RAD2DEG) * 1.5, if (clone) .2 else .7, cos((10.0 * t - 60.0) * RAD2DEG) * 1.5))
                as3.teleport(player.location.plus(sin((10.0 * t + 180.0) * -RAD2DEG) * 1.5, if (clone) .2 else .7, cos((10.0 * t + 180.0) * RAD2DEG) * 1.5))

                as1.setRotation((10f * t + 60f) * -1f, 0f)
                as2.setRotation((10f * t - 60f) * -1f, 0f)
                as3.setRotation((10f * t + 180f) * -1f, 0f)

                for (ast in arrayOf(as1, as2, as3)) {
                    player.spawnParticle(
                        if (clone) Particle.VILLAGER_HAPPY else Particle.CRIT,
                        ast.location.plus(.0, if (clone) .5 else .2, .0),
                        1, .0, .0, .0, .0
                    )
                    player.spawnParticle(
                        if (clone) Particle.FIREWORKS_SPARK else Particle.CRIT_MAGIC,
                        ast.location.plus(.0, if (clone) .5 else .2, .0),
                        1, .0, .0, .0, .0
                    )
                }
            }
            else -> {
                player.playSound(player.location, Sound.ENTITY_WITHER_BREAK_BLOCK, .1f, 1.9f)
                player.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1f, 1f)
                deactivate()
            }
        }

        if (cooldown <= 0) {
            for (e in player.getNearbyEntities(3.0, 3.0, 3.0)) {
                if (e is Player)
                    continue
                if (e !is Mob)
                    continue

                activate(e)

                if (remaining > 1) {
                    cooldown = 10
                    --remaining
                } else {
                    ArrowRain(player).schedule()

                    deactivate()

                    cancel()
                }
            }
        }

        --cooldown
    }

    private fun activate(e: LivingEntity) {
        e.damage(2.0, player)
        e.noDamageTicks = 0
        e.velocity = (e.location.toVector() - player.location.toVector()).setY(0).normalize().add(Vector(.0, 1.0, .0))
        player.playSound(player.location, Sound.ENTITY_ARROW_HIT, 1f, .9f)
        player.playSound(player.location, Sound.ENTITY_WITHER_BREAK_BLOCK, .1f, 1.9f)
        player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1f, 1f)
    }

    private fun deactivate() {
        player.removeScoreboardTag("arrow_shield")
        as1.remove()
        as2.remove()
        as3.remove()
    }
}*/