package com.wynnlab.scoreboard

import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Objective

class DamageSidebar(
    id: String,
    private val players: (Player) -> List<Player>
) : Scoreboard(id){
    override fun setScores(player: Player, sb: org.bukkit.scoreboard.Scoreboard, o: Objective) {
        clear(sb, o)
        players(player).forEach { m ->
            val d = m.getStatistic(Statistic.DAMAGE_DEALT) // TODO: Statistic
            //o.getScore("§${if (d > 0) 'f' else '7'}${m.name}").score = d
            setScore(sb, o, "§${if (d > 0) 'f' else '7'}${m.name}", d)
        }
    }
}