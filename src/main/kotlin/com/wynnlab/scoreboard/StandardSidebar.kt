package com.wynnlab.scoreboard

import com.wynnlab.COLOR_GOLD
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object StandardSidebar : ConfiguredSidebar {
    override fun apply(player: Player, sidebar: Sidebar) {
        sidebar.setForUpdate(1, quests(player))

        sidebar.setForUpdate(3, pos(player))

        sidebar.setForUpdate(5, Component.text("5 ✯✯✯✯✯", COLOR_GOLD))

        sidebar.clearForUpdate(4)
        var i = 6
        while (i < 16)
            sidebar.clearForUpdate(i++)
        sidebar.displayLines = 5
    }
}