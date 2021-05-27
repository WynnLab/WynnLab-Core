package com.wynnlab.pvp

import com.wynnlab.api.sendWynnMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player


object FFA {
    val players = mutableListOf<Player>()

    fun join(player: Player) {
        val w = Bukkit.getWorld("FFA") ?: run {
            player.sendMessage("§cFFA is currently unavailable")
            return
        }

        player.teleport(w.spawnLocation)

        val console = Bukkit.getServer().consoleSender
        val command = "execute as ${player.name} at @s run spreadplayers 0 0 25 50 false @s"
        Bukkit.dispatchCommand(console, command)
    }

    fun onJoinWorld(player: Player) {
        player.addScoreboardTag("pvp")
        player.addScoreboardTag("ffa")

        players.add(player)

        player.sendWynnMessage("messages.ffa.welcome")
        player.sendWynnMessage("messages.ffa.stats")
        player.sendWynnMessage("messages.ffa.leave")

        players.forEach {
            it.sendMessage("§8[§cFFA§8] §a>> §r${player.playerListName}")
        }
    }

    fun onLeaveWorld(player: Player) {
        player.removeScoreboardTag("pvp")
        player.removeScoreboardTag("ffa")

        players.remove(player)

        players.forEach {
            it.sendMessage("§8[§cFFA§8] §4<< §r${player.playerListName}")
        }
    }
}