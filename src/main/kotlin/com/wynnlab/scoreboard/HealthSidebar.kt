package com.wynnlab.scoreboard

import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Objective

class HealthSidebar(
    id: String,
    private val players: (Player) -> List<Player>
) : Scoreboard(id) {
    override fun setScores(player: Player, o: Objective) {
        players(player).forEach { m ->
            val h = m.health
            val percent = h / m.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value

            o.getScore("§${when {
                percent < .33 -> 'c'
                percent < .67 -> 'e'
                else -> 'a'
            }}${player.name}").score = h.toInt()
        }
    }
}