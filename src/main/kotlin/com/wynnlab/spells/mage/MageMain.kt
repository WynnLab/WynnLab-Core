package com.wynnlab.spells.mage

import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellData
import org.bukkit.entity.Player
import org.bukkit.Sound
import org.bukkit.entity.Mob
import com.wynnlab.util.LocationIterator
import org.bukkit.Particle

class MageMain(player: Player) : Spell(player, 1, SpellData.METEOR) {
    private val hit: MutableList<Mob> = mutableListOf()

    override fun tick() {
        val l1 = player.eyeLocation.clone().add(0.0, -0.5, 0.0)
        val l2 = player.eyeLocation.clone().add(player.eyeLocation.direction.clone().multiply(7)).clone().add(0.0, -0.5, 0.0)

        for (loc in LocationIterator(l1, l2, player.eyeLocation.direction.clone(), 0.5)) {
            player.spawnParticle(if (clone) Particle.SQUID_INK else Particle.CLOUD, loc, 2, 0.0, 0.0, 0.0, 0.1)
            player.spawnParticle(if (clone) Particle.SPELL_WITCH else Particle.CRIT, loc, 1, 0.0, 0.0, 0.0, 0.0)
            player.spawnParticle(Particle.CRIT_MAGIC, loc, 1, 0.0, 0.0, 0.0, 0.1)
            for (e in loc.getNearbyEntities(0.5, 0.5, 0.5)) {
                if (e is Player) {
                    continue
                }
                if (e is Mob) {
                    if (hit.contains(e)) {
                        continue
                    }
                    hit.add(e)
                    e.damage(2.0, player)
                }
            }
        }

        player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1.5f)
        if (clone) player.playSound(player.location, Sound.ENTITY_SHULKER_SHOOT, 0.5f, 1.5f)
    }
}