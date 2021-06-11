package com.wynnlab.scoreboard

import com.wynnlab.COLOR_DARKER_GRAY
import com.wynnlab.COLOR_PARTY
import com.wynnlab.essentials.Party
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object PartySidebar : ConfiguredSidebar {
    override fun apply(player: Player, sidebar: Sidebar) {
        val party = Party.members[player]!!

        sidebar.setForUpdate(1, pos(player))

        sidebar.setForUpdate(3, Component.text("Party: ", COLOR_PARTY)
            .append(Component.text("(${party.members.size})", COLOR_DARKER_GRAY)))

        var i = 4
        party.getSbTexts().forEach {
            sidebar.setForUpdate(i++, it)
        }

        sidebar.displayLines = i - 1

        while (i < 16)
            sidebar.clearForUpdate(i++)
    }
}