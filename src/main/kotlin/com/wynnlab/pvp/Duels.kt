package com.wynnlab.pvp

import com.wynnlab.COLOR_DARKER_GRAY
import com.wynnlab.api.getLocalizedText
import com.wynnlab.util.InstancedWorld
import com.wynnlab.util.createInstancedWorld
import com.wynnlab.wynnlab
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player

object Duels {
    val CHAT_PREFIX = Component.text("[", COLOR_DARKER_GRAY)
        .append(Component.text("DUELS", NamedTextColor.GREEN))
        .append(Component.text("] ", COLOR_DARKER_GRAY))

    val playerDuels = mutableMapOf<String, Duel>()

    operator fun get(player: Player) = playerDuels[player.name]

    //fun byUUID(uuid: UUID) = Bukkit.getPlayer(uuid)?.let { get(it) }

    val duels = mutableListOf<Duel>()

    val maps = listOf("PLAINS")

    fun startDuel(player: Player, index: Int): Duel {
        val duel = Duel(player, index)
        playerDuels[player.name] = duel
        duels.add(duel)

        createInstancedWorld("Duel-World-$index", "Duel-Instance-${player.uniqueId}").execute {
            duel.world = it
            if (it != null) duel.ready = true
            else duel.cancel()
        }

        return duel
    }

    fun prepare(player: Player) {
        player.addScoreboardTag("pvp")
        player.addScoreboardTag("duel")
    }

    fun playerLeft(player: Player) {
        Bukkit.broadcastMessage("${player.name} left duel")
        player.removeScoreboardTag("pvp")
        player.removeScoreboardTag("duel")

        playerDuels[player.name]?.leave(player)
    }

    fun playerDied(player: Player) {
        playerLeft(player)
    }
}

class Duel(player: Player, val map: Int) {
    var player1: Player? = player
    var player2: Player? = null

    var world: InstancedWorld? = null
    var ready = false

    var wantToStart = false

    fun join(player: Player) {
        if (player1 != null && player2 != null)
            return
        if (player1?.name == player.name || player2?.name == player.name)
            return
        Bukkit.broadcastMessage("${player.name} joined duel")
        Duels.playerDuels[player.name] = this
        if (player1 == null) player1 = player else {
            player2 = player
            start()
        }
    }

    fun leave(player: Player) {
        Duels[player]?.let {
            val left = if (player.name == it.player1?.name) it.player2?.name else it.player1?.name
            if (left == null || left == player.name) {
                it.delete()
                return
            }
            if (left == it.player2?.name) {
                it.player1 = it.player2
                it.player2 = null
            } else {
                it.player2 = null
            }
        }
        Duels.playerDuels.remove(player.name)
    }

    fun start() {
        Bukkit.broadcastMessage("duel started")
        Duels.prepare(player1 ?: return)
        Duels.prepare(player2 ?: return)

        if (ready) {
            messageParticipants("messages.duel.starting")
            var secondsToStart = 10
            Bukkit.getScheduler().runTaskTimer(wynnlab, { task ->
                if (player1 == null || player2 == null) {
                    messageParticipants("messages.duel.canceled_start")
                    task.cancel()
                    return@runTaskTimer
                }
                when (secondsToStart) {
                    10, 5, 4, 3, 2, 1 -> {
                        player1?.sendTitle("§a$secondsToStart", null, 10, 10, 10)
                        player2?.sendTitle("§a$secondsToStart", null, 10, 10, 10)
                        player1?.playSound(player1!!.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
                        player2?.playSound(player2!!.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
                    }
                    0 -> {
                        player1?.playSound(player1!!.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                        player2?.playSound(player2!!.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

                        messageParticipants("messages.duel.started")

                        world!!.sendPlayer(player1!!, -25.0, 1.0, .0, -90f, 0f)
                        world!!.sendPlayer(player2!!, 25.0, 1.0, .0, 90f, 0f)

                        world!!.unloadOnZeroPlayers()

                        task.cancel()
                    }
                }
                --secondsToStart
            }, 10L, 20L)
        } else {
            messageParticipants("messages.duel.wait_world")
            wantToStart = true
        }
    }

    private fun delete() {
        Duels.duels.remove(this)
    }

    fun cancel() {
        Bukkit.broadcastMessage("duel canceled")
        player1?.let { Duels.playerDuels.remove(it.name) }
        player2?.let { Duels.playerDuels.remove(it.name) }
        Duels.duels.remove(this)
    }

    private fun messageParticipants(key: String, formatArgs: Array<String> = arrayOf()) {
        player1?.sendMessage(Duels.CHAT_PREFIX.append(player1!!.getLocalizedText(key, formatArgs)))
        player2?.sendMessage(Duels.CHAT_PREFIX.append(player2!!.getLocalizedText(key, formatArgs)))
    }
}