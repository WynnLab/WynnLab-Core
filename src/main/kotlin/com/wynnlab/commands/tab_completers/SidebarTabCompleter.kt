package com.wynnlab.commands.tab_completers

import com.wynnlab.api.hasScoreboardTag
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SidebarTabCompleter : BaseTabCompleter("sidebar", "sb") {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        if (args.size != 1 || sender !is Player) return emptyList()

        return when {
            sender.hasScoreboardTag("ffa") -> ffaSbs
            sender.hasScoreboardTag("duel") -> duelSbs
            else -> sbs
        }
    }

    private val sbs = listOf("info", "health", "damage")
    private val ffaSbs = listOf("info", "health", "damage", "kills", "partyHealth")
    private val duelSbs = listOf("info", "health", "damage", "partyHealth")
}