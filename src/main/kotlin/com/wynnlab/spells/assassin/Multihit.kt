package com.wynnlab.spells.assassin

import com.wynnlab.api.normalizeOnXZ
import com.wynnlab.spells.SpellL
import com.wynnlab.spells.SpellData
import com.wynnlab.util.plus
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

class Multihit(player: Player) : SpellL(player, 11, SpellData.MULTIHIT) {
    private lateinit var l: Location
    private lateinit var entities: Collection<Entity>

    override fun tick() {
        when {
            tick == 0 -> {
                player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_STRONG, .5f, 1f)
                player.playSound(player.location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 1.5f)
                player.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 1f, 1.3f)
                if (clone) player.playSound(player.location, Sound.ENTITY_BLAZE_AMBIENT, .5f, 1.6f)

                l = player.location + player.eyeLocation.direction.clone().normalizeOnXZ()

                entities = player.world.getNearbyEntities(l, 2.0, 2.0, 2.0) {
                    it !is Player && it is Mob
                }
            }
            tick < 10 -> {
                for (e in entities) {

                }
            }
            else -> {

            }
        }
    }
}