package com.wynnlab

import com.destroystokyo.paper.profile.ProfileProperty
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.lang.IllegalArgumentException

object Players {
    val players get() = Bukkit.getOnlinePlayers()

    fun initPlayers() {
        //initTeams()
        for (player in players) {
            preparePlayer(player)
        }
    }

    /*fun initTeams() {
        val mainTeam = try {
            Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("WynnLab.MainTeam")
        } catch (e: IllegalArgumentException) {
            return
        }
        mainTeam.setAllowFriendlyFire(false)
        mainTeam.setCanSeeFriendlyInvisibles(false)
        mainTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
    }*/

    //WARNING: Only for thing that work if done multiple times
    fun preparePlayer(player: Player) {
        //Bukkit.getScoreboardManager().mainScoreboard.getTeam("WynnLab.MainTeam")?.addEntry(player.name)
        player.gameMode = GameMode.ADVENTURE
        player.foodLevel = 20
        player.saturation = 0f
        player.exp = 0f
        player.level = 106
    }
}