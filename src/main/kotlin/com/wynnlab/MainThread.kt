package com.wynnlab

import com.wynnlab.api.getId
import com.wynnlab.api.standardActionBar
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute

object MainThread : Runnable {
    @Volatile
    var tick = 0

    override fun run() {
        val s1 = tick % 20 == 0
        val s4 = tick % 80 == 0

        for (player in Bukkit.getServer().onlinePlayers) {
            player.standardActionBar()
            player.exhaustion = 0f
            val maxHealth = (505 + player.getId("health_bonus")).coerceIn(1, 1000000).toDouble()
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = maxHealth
            if (s1) player.foodLevel = (player.foodLevel + 1).coerceAtMost(20)
            if (s4) {
                player.foodLevel = (player.foodLevel + player.getId("mana_regen")).coerceAtMost(20)
                player.health = (player.health + player.getId("health_regen_raw") * (1 + player.getId("health_regen") / 100f)).coerceAtMost(maxHealth)
            }
        }

        ++tick
    }

    fun schedule() {
        Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L)
    }
}