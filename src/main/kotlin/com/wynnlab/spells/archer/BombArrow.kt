package com.wynnlab.spells.archer

import com.wynnlab.random
import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellData
import com.wynnlab.util.LocationIterator
import com.wynnlab.util.times
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

class BombArrow(player: Player) : Spell(player, 1, SpellData.BOMB_ARROW) {
    override fun tick() {
        player.playSound(player.location, Sound.ENTITY_ARROW_SHOOT, .9f, .7f)
        player.playSound(player.location, Sound.ENTITY_TNT_PRIMED, 1f, 1.3f)

        val bomb = player.launchProjectile(if (clone) Snowball::class.java else Arrow::class.java, player.eyeLocation.direction * 3.0)
        bomb.addScoreboardTag("bomb_arrow")
        if (clone) {
            (bomb as Snowball).item = ItemStack(Material.FLINT)
            bomb.setRotation(random.nextFloat(), 0f)
        }
    }
}