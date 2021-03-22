package com.wynnlab.spells.mage

import com.wynnlab.api.normalizeOnXZ
import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellData
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object IceSnake : Spell(21, SpellData.METEOR) {
    private val hit: MutableList<Mob> = mutableListOf()
    private lateinit var iceLoc: Location

    override fun init() {
        iceLoc = player.location.clone().add(0.0, 1.0, 0.0)
    }

    override fun tick() {
        iceLoc.add(player.eyeLocation.direction.clone().normalizeOnXZ())
        val iceBlock = player.world.spawnFallingBlock(
            iceLoc,
            if (clone) Material.OBSIDIAN.createBlockData() else Material.PACKED_ICE.createBlockData()
        )
        iceBlock.dropItem = false
        iceBlock.setHurtEntities(false)
        player.spawnParticle(Particle.FIREWORKS_SPARK, iceLoc, 1, 1.0, 1.0, 1.0, 0.1)
        player.spawnParticle(Particle.BLOCK_CRACK, iceLoc, 9, 1.0, 1.0, 1.0, 1.0,
            if (clone) Material.OBSIDIAN.createBlockData() else Material.ICE.createBlockData()
        )
        player.playSound(iceLoc,
            if (clone) Sound.BLOCK_STONE_PLACE else Sound.BLOCK_GLASS_BREAK, if (clone) 2f else 1f, 0.75f
        )
        player.playSound(iceLoc,
            if (clone) Sound.ENTITY_WITHER_BREAK_BLOCK else Sound.BLOCK_FIRE_EXTINGUISH,
            if (clone) 0.25f else 0.5f, if (clone) 1.5f else 1f
        )
        for (e in iceLoc.getNearbyEntities(1.0, 1.0, 1.0)) {
            if (e is Player) {
                continue
            }
            if (e is Mob) {
                e.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 300, 4))
                if (hit.contains(e)) {
                    continue
                }
                hit.add(e)
                e.damage(1.0, player)
            }
        }
    }
}