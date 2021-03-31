package com.wynnlab.api

import org.bukkit.Bukkit
import org.bukkit.scoreboard.Team
import org.bukkit.util.Vector

fun Vector.normalizeOnXZ() = if (x == 0.0 && z == 0.0) {
    y = 0.0; this
} else {
    y = 0.0; normalize()
}

fun registerMainTeam(name: String): Team = try {
    Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam(name)
} catch (e: IllegalArgumentException) {
    Bukkit.getScoreboardManager().mainScoreboard.getTeam(name)?.unregister()
    Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam(name)
}