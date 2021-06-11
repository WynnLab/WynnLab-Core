@file:JvmName("BukkitUtils")

package com.wynnlab.util

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

fun yawToDir(yaw: Float) = when {
    yaw < -157.5f -> "N"
    yaw < -112.5f -> "NE"
    yaw < -67.5f -> "E"
    yaw < -22.5f -> "SE"
    yaw < 22.5f -> "S"
    yaw < 67.5f -> "SW"
    yaw < 112.5f -> "W"
    yaw < 157.5f -> "NW"
    else -> "N"
}