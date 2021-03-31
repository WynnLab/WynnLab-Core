package com.wynnlab

import com.wynnlab.api.standardActionBar
import org.bukkit.Bukkit

object MainThread : Runnable {
    @Volatile
    var tick = 0

    override fun run() {
        val manaRegen = tick % 100 == 0

        for (player in Bukkit.getServer().onlinePlayers) {
            player.standardActionBar()
            player.exhaustion = 0f
            if (manaRegen) player.foodLevel += 2
        }

        ++tick
    }

    fun schedule() {
        Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L)
    }
}