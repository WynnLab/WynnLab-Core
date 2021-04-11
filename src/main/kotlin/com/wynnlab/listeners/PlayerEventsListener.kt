package com.wynnlab.listeners

import com.wynnlab.Players
import com.wynnlab.api.prefix
import com.wynnlab.api.prefixes
import com.wynnlab.api.wynnPrefix
import com.wynnlab.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerEventsListener : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        Players.preparePlayer(e.player)
        e.joinMessage = "§7[§a+§7]§r ${e.player.prefix}${e.player.name}"
        e.player.sendMessage("Locale: ${e.player.locale}")
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        e.quitMessage = "§7[§c-§7]§r ${e.player.prefix}${e.player.name}"
        prefixes.remove(e.player)
        plugin.essentialsCommands.conversations.remove(e.player)
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