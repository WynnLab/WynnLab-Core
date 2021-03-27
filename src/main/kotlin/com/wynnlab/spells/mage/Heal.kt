package com.wynnlab.spells.mage

import com.wynnlab.spells.SpellL
import com.wynnlab.spells.SpellData
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityRegainHealthEvent

class Heal(player: Player) : SpellL(player, 41, SpellData.HEAL) {
    override fun tick() {
        if (tick % 20 == 0) {
            player.spawnParticle(Particle.PORTAL, player.location.clone().add(0.0, 0.5, 0.0), 144, 4.0, 0.0, 4.0, 0.1)
            player.spawnParticle(Particle.CRIT_MAGIC, player.location.clone().add(0.0, 0.3, 0.0), 144, 4.0, 0.0, 4.0, 0.1)
            player.spawnParticle(Particle.FIREWORKS_SPARK, player.location.clone().add(0.0, 1.0, 0.0), 16, 0.3, 1.0, 0.3, 0.05)
            player.playSound(player.location, Sound.ENTITY_EVOKER_CAST_SPELL, 0.5f, 1.5f)
            player.playSound(player.location, Sound.BLOCK_LAVA_EXTINGUISH, 1f, 1f)
            player.health = (player.health + 50).coerceAtMost(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value) // Heal Health
            for (e in player.getNearbyEntities(4.0, 4.0, 4.0)) {
                if (e is Player) {
                    e.health = (e.health + 50).coerceAtMost(e.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value) // Heal Health
                    Bukkit.getPluginManager().callEvent(
                        EntityRegainHealthEvent(player, 50.0, EntityRegainHealthEvent.RegainReason.CUSTOM) // Heal Health
                    )
                    player.spawnParticle(Particle.FIREWORKS_SPARK, e.location.clone().add(0.0, 1.0, 0.0), 16, 0.3, 1.0, 0.3, 0.05)
                }
            }
        }
    }
}