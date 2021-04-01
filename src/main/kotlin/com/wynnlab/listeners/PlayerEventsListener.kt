package com.wynnlab.listeners

import com.wynnlab.Players
import com.wynnlab.api.prefix
import com.wynnlab.api.prefixes
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerEventsListener : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.joinMessage = "§7[§a+§7]§r ${e.player.prefix}${e.player.name}"
        Players.preparePlayer(e.player)
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        prefixes.remove(e.player)
        e.quitMessage = "§7[§c-§7]§r ${e.player.prefix}${e.player.name}"
    }

    @EventHandler
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        e.format = "%s: §r%s"
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
        val deathMessages = setOf("$ didn't know about RRR")
    }
}