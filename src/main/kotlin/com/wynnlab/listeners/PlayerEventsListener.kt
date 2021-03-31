package com.wynnlab.listeners

import com.wynnlab.Players
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerEventsListener : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.joinMessage = "§a»§r ${e.player.name}"
        Players.preparePlayer(e.player)
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        e.quitMessage = "§c«§r ${e.player.name}"
    }

    @EventHandler
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        e.format = "%s§7: %s"
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        e.keepInventory = true
        e.keepLevel = true
        e.setShouldDropExperience(false)
        e.droppedExp = 0

        e.deathMessage = deathMessages.random().replace("$", e.entity.name)
    }

    companion object {
        val deathMessages = setOf("$ didn't know about R-R-R")
    }
}