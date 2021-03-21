package com.wynnlab

import com.wynnlab.api.standardActionBar
import org.bukkit.Bukkit

object MainThread : Runnable {
    override fun run() {
        for (player in Bukkit.getServer().onlinePlayers) {
            player.standardActionBar()
        }
    }

    fun schedule() {
        Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L)
    }
}