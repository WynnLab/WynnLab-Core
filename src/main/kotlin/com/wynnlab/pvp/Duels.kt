package com.wynnlab.pvp

import org.bukkit.entity.Player

object Duels {
    val duels = mutableMapOf<Player, Duel>()

    operator fun get(player: Player) = duels[player]
}

class Duel {
    val players = mutableListOf<Player>()
}