package com.wynnlab.scoreboard

import com.wynnlab.api.data
import com.wynnlab.api.getLocalizedText
import com.wynnlab.api.setString
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.RenderType
import org.bukkit.scoreboard.Scoreboard as _Sb

abstract class Scoreboard(
    val id: String
) {
    fun update(player: Player) /*= Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable*/ {
        player.data.setString("scoreboard", id)

        val sb = scoreboards[player]
            ?: Bukkit.getScoreboardManager().newScoreboard.also { scoreboards[player] = it }

        sb.getObjective("sb_title")?.unregister()

        val o = sb.registerNewObjective(
            "sb_title",
            "dummy",
            player.getLocalizedText("sidebars.$id"),
            RenderType.INTEGER
        )

        o.displaySlot = DisplaySlot.SIDEBAR

        setScores(player, o)

        player.scoreboard = sb
    }//)

    abstract fun setScores(player: Player, o: Objective)

    companion object {
        val scoreboards = mutableMapOf<Player, _Sb>()
    }
}