package com.wynnlab.scoreboard

import org.bukkit.entity.Player
import org.bukkit.scoreboard.Objective

class DynamicScoreboard(
    id: String,
    val text: (Player) -> List<String>
) : Scoreboard(id) {
    override fun setScores(player: Player, sb: org.bukkit.scoreboard.Scoreboard, o: Objective)  {
        clear(sb, o)
        text(player).forEachIndexed { i, t ->
            //o.getScore(t).score = 15 - i
            setScore(sb, o, t, 15 - i)
        }
    }
}