package com.wynnlab.scoreboard

import com.wynnlab.api.data
import com.wynnlab.api.getLocalizedText
import com.wynnlab.api.setString
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.RenderType
import org.bukkit.scoreboard.Team
import org.bukkit.scoreboard.Scoreboard as _Sb

abstract class Scoreboard(
    val id: String
) {
    val objectives = hashMapOf<Player, Objective>()

    val scores = hashMapOf<Objective, MutableSet<Int>>()

    fun update(player: Player) /*= Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable*/ {
        val sb = scoreboards[player]
            ?: Bukkit.getScoreboardManager().newScoreboard.also { scoreboards[player] = it }

        //if (player.data.getString("scoreboard") != id)
        //    sb.getObjective("display_${player.data.getString("scoreboard")}")?.unregister()

        val o = objectives[player] ?: sb.registerNewObjective(
            id,
            "dummy",
            player.getLocalizedText("sidebars.$id"),
            RenderType.INTEGER
        ).also { o ->
            o.displaySlot = DisplaySlot.SIDEBAR

            objectives[player] = o
            wynnScoreboards.putIfAbsent(player, mutableListOf())
            wynnScoreboards[player]!!.add(this)
        }

        setScores(player, sb, o)

        player.data.setString("scoreboard", id)

        player.scoreboard = sb

        //o.displaySlot = DisplaySlot.SIDEBAR
        /*++i
        if (i >= 1000)
            i = 0*/
    }//)

    abstract fun setScores(player: Player, sb: _Sb, o: Objective)

    fun setDisplayName(o: Objective, name: String) {
        o.displayName = name
    }

    fun clear(sb: _Sb, o: Objective) {
        scores[o]?.forEach { removeScore(sb, o, it) }
    }

    fun setScore(sb: _Sb, o: Objective, content: String, s: Int) {
        val t = getTeamByScore(sb, s)
        t.prefix = content
        showScore(o, s)
        scores[o]?.add(s)
    }

    fun showScore(o: Objective, s: Int) {
        val n = "§${s.toString(0x10)}"
        //if (o.getScore(n).isScoreSet)
        //    return
        o.getScore(n).score = s
    }

    fun removeScore(sb: _Sb, o: Objective, s: Int) {
        hideScore(sb, o, s)
    }

    fun hideScore(sb: _Sb, o: Objective, s: Int) {
        val n = "§${s.toString(0x10)}"
        if (o.getScore(n).isScoreSet)
            return
        sb.resetScores(n)
    }

    private fun getTeamByScore(sb: _Sb, s: Int): Team {
        val n = "§${s.toString(0x10)}"

        var t = sb.getEntryTeam(n)
        if (t != null) return t

        t = sb.registerNewTeam("ENTRY_$s")
        t.addEntry(n)

        return t
    }

    companion object {
        val scoreboards = mutableMapOf<Player, _Sb>()
        val wynnScoreboards = mutableMapOf<Player, MutableList<Scoreboard>>()

        fun clear(player: Player) {
            scoreboards.remove(player)
            wynnScoreboards[player]?.forEach { it.objectives.remove(player) }
            wynnScoreboards.remove(player)
        }
    }
}