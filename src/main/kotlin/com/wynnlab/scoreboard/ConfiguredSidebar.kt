package com.wynnlab.scoreboard

import org.bukkit.entity.Player

interface ConfiguredSidebar {
    fun apply(player: Player, sidebar: Sidebar)
}