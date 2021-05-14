package com.wynnlab

import com.wynnlab.api.getArmorHealth
import com.wynnlab.api.getId
import com.wynnlab.api.standardActionBar
import com.wynnlab.api.testInventory
import com.wynnlab.spells.PySpell
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object MainThread : Runnable {
    @Volatile
    var tick = 0

    override fun run() {
        val s1 = tick % 20 == 0
        val s4 = tick % 80 == 0

        for (player in Bukkit.getServer().onlinePlayers) {
            // Send action bar
            player.standardActionBar()

            // Reset exhaustion to not lose mana except in spell casts
            player.exhaustion = 0f

            // Store max health and set attribute
            val maxHealth = (505 + player.getId("health_bonus") + player.getArmorHealth()).coerceIn(1, 1000000).toDouble()
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = maxHealth

            val manaDrain = player.hasPotionEffect(PotionEffectType.INVISIBILITY)

            if (!manaDrain && "invis" in player.scoreboardTags)
                PySpell.castSpell(player, "ASSASSIN", 5)

            if (s1) {
                // Natural mana regen
                player.foodLevel = (player.foodLevel + if (manaDrain) -1 else 1).coerceIn(0, 20)

                // Jump height
                val jumpHeight = player.getId("jump_height")
                if (jumpHeight != 0) player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 21, jumpHeight - 1, true, false, false))

                // Test Inventory
                player.testInventory()

                // Sidebar
                // player.updateSidebar() // TODO: a lot of things
            }

            // Mana regen and health regen
            if (s4) {
                if (!manaDrain) player.foodLevel = (player.foodLevel + player.getId("mana_regen")).coerceIn(0, 20)
                player.health = (player.health + player.getId("health_regen_raw") * (1 + player.getId("health_regen") / 100f)).coerceIn(1.0, maxHealth)
            }

            // Walk speed
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = .1 + player.getId("speed") * .001
        }

        ++tick
    }

    fun schedule() {
        Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L)
    }
}