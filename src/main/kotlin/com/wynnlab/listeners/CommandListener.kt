package com.wynnlab.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CommandListener : BaseListener() {
    @EventHandler
    fun onCommandPreprocess(e: PlayerCommandPreprocessEvent) {
        if (e.message == "/kill") {
            e.player.health = .0
            e.message = "/wynnlab:class"
        }
    }
}