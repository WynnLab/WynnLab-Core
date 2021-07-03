package com.wynnlab.pvp

import com.wynnlab.util.sendPlayerToInstancedWorld
import org.bukkit.Location
import org.bukkit.entity.Player

object Duels {
    private val playerDuels = mutableMapOf<Player, Duel>()

    operator fun get(player: Player) = playerDuels[player]

    //fun byUUID(uuid: UUID) = Bukkit.getPlayer(uuid)?.let { get(it) }

    val duels = mutableListOf<Duel>()

    val maps = listOf("PLAINS")

    fun startDuel(player: Player, index: Int) {
        sendPlayerToInstancedWorld(player, "Duel-World-$index", "Duel-Instance-${player.uniqueId}",
            Location(null, -25.0, 1.0, .0, -90f, 0f)
        )
        prepare(player)

        val duel = Duel(player, index)
        playerDuels[player] = duel
        duels.add(duel)
    }

    fun prepare(player: Player) {
        player.addScoreboardTag("pvp")
        player.addScoreboardTag("duel")
    }

    fun playerLeft(player: Player) {
        player.removeScoreboardTag("pvp")
        player.removeScoreboardTag("duel")

        playerDuels.remove(player)
        get(player)?.let {
            val left = if (player == it.player1) it.player2 else it.player1
            if (left == null) it.delete()
            if (left == it.player2) {
                it.player1 = left
                it.player2 = null
            }
        }
    }

    fun playerDied(player: Player) {
        playerLeft(player)
    }
}

class Duel(player: Player, val map: Int) {
    var player1: Player? = player
    var player2: Player? = null

    fun join(player: Player) {
        player1?.let { p ->
            player.teleport(Location(p.world, 25.0, 1.0, .0, 90f, 0f))
            player2 = player
        }
    }

    fun start() {
        Duels.prepare(player1 ?: return)
        Duels.prepare(player2 ?: return)
    }

    fun delete() {
        Duels.duels.remove(this)
    }
}