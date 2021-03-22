package com.wynnlab.spells.archer

import com.wynnlab.random
import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellData
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.inventory.ItemStack

class ArcherMain(player: Player) : Spell(player, 1, SpellData.METEOR) {
    override fun tick() {
        player.playSound(player.location, Sound.ENTITY_ARROW_SHOOT, 1f, 1f)
        if (clone) {
            val arrow = player.launchProjectile(Snowball::class.java, player.eyeLocation.direction.clone().multiply(3))
            arrow.addScoreboardTag("arrow")
            arrow.item = ItemStack(Material.FLINT)
            arrow.setRotation(random.nextFloat(), 0f)
        } else {
            val arrow = player.launchProjectile(Arrow::class.java, player.eyeLocation.direction.clone().multiply(3))
            arrow.addScoreboardTag("arrow")
        }
    }
}