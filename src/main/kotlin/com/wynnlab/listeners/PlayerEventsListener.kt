package com.wynnlab.listeners

import com.wynnlab.Players
import com.wynnlab.api.*
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
        val player = e.player

        Players.preparePlayer(player)
        e.joinMessage = "§7[§a+§7]§r ${player.prefix}${player.name}"
        //e.player.sendMessage("Locale: ${e.player.locale}")
        player.getWynnClass()?.let { c ->
            player.sendWynnMessage("messages.select_class", player.getLocalizedText("classes.$c.${if (player.isCloneClass) "cloneName" else "className"}"))
            player.sendWynnMessage("messages.class_change")
        } ?: run {
            player.sendWynnMessage("messages.no_class")
            player.sendWynnMessage("messages.select_class")
            player.performCommand("wynnlab:class")
        }
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