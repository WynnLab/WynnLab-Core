package com.wynnlab.scoreboard

import com.wynnlab.*
import com.wynnlab.util.yawToDir
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

fun quests(@Suppress("unused") player: Player) = Component.text("Quests: ", COLOR_QUESTS)
    .append(Component.text("0/0", COLOR_QUESTS_COUNT))
    .append(Component.text(" [", COLOR_QUESTS_BRACKET))
    .append(Component.text("100%", COLOR_QUESTS_COUNT))
    .append(Component.text("]", COLOR_QUESTS_BRACKET))

fun pos(player: Player) = Component.text("Pos: ", COLOR_ORANGE)
    .append(Component.text("${player.location.x.toInt()} ", COLOR_DES_RED))
    .append(Component.text("${player.location.y.toInt()} ", COLOR_DES_GREEN))
    .append(Component.text("${player.location.z.toInt()} ", COLOR_DES_BLUE))
    .append(Component.text("[", COLOR_DARKER_GRAY))
    .append(Component.text(yawToDir(player.eyeLocation.yaw), COLOR_ORANGE))
    .append(Component.text("]", COLOR_DARKER_GRAY))