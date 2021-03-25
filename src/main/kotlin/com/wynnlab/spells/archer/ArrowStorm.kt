package com.wynnlab.spells.archer

import com.wynnlab.random
import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellData
import com.wynnlab.util.times
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Snowball
import org.bukkit.inventory.ItemStack

class ArrowStorm(player: Player) : Spell(player, 21, SpellData.ARROW_STORM) {
    private val arrows = mutableListOf<Projectile>()

    override fun tick() {
        if (tick == 0)
            player.spawnParticle(if (clone) Particle.VILLAGER_HAPPY else Particle.CRIT, player.location, 5)

        player.playSound(player.location, Sound.ENTITY_ARROW_SHOOT, 1f, 1f)
        if (clone) player.playSound(player.location, Sound.BLOCK_GLASS_BREAK, .05f, .6f)

        for (arrow in arrows) {
            player.spawnParticle(Particle.FLAME, arrow.location, 1, .0, .0, .0, .01)
            player.spawnParticle(if (clone) Particle.VILLAGER_HAPPY else Particle.CRIT, arrow.location, 2, .0, .0, .0, .0)
        }

        val arrowM = player.launchProjectile(if (clone) Snowball::class.java else Arrow::class.java, player.eyeLocation.direction * 3.0)
        val arrowL = player.launchProjectile(if (clone) Snowball::class.java else Arrow::class.java, (player.eyeLocation.direction * 3.0).rotateAroundY(.4))
        val arrowR = player.launchProjectile(if (clone) Snowball::class.java else Arrow::class.java, (player.eyeLocation.direction * 3.0).rotateAroundY(-.4))

        for (arrow in arrayOf(arrowM, arrowL, arrowR)) {
            arrow.shooter = player
            arrow.addScoreboardTag("storm_arrow")
            arrows.add(arrow)
            if (clone) {
                (arrow as Snowball).item = ItemStack(Material.FLINT)
                arrow.setRotation(random.nextFloat(), 0f)
            }
        }
    }
}