package com.wynnlab.spells.archer

import com.wynnlab.api.data
import com.wynnlab.api.getInt
import com.wynnlab.api.setInt
import com.wynnlab.api.setString
import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellData
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

class ArrowShield(player: Player) : Spell(player, 1, SpellData.ARROW_SHIELD) {
    override fun tick() {
        if (tick == 0) {
            if ("shield" !in player.scoreboardTags) {
                player.addScoreboardTag("arrow_shield")
                player.data.setInt("arrow_shield", 3)
            } else {
                val arrows = player.data.getInt("arrow_shield")!!
                player.data.setInt("arrow_shield", arrows - 1)
                if (arrows <= 1)
                    player.removeScoreboardTag("arrow_shield")
                return //TODO
            }

            player.playSound(player.location, if (clone) Sound.ITEM_ARMOR_EQUIP_NETHERITE else Sound.ENTITY_EVOKER_PREPARE_SUMMON, .5f, .9f)
            player.playSound(player.location, Sound.ENTITY_ARROW_SHOOT, 1f, .8f)
            player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f)

            val as1 = player.world.spawn(player.location.clone().add(1.3, if (clone) .2 else .7, .75), ArmorStand::class.java)
            val as2 = player.world.spawn(player.location.clone().add(-1.3, if (clone) .2 else .7, .75), ArmorStand::class.java)
            val as3 = player.world.spawn(player.location.clone().add(.0, if (clone) .2 else .7, -1.5), ArmorStand::class.java)
        }
    }
}