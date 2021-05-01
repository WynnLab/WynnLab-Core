package com.wynnlab.commands.tab_completers

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object RankTabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> = if (args.size == 1)
        completeWord(ranks, args[0])
    else emptyList()

    private val ranks = listOf("player", "vip", "vip+", "hero", "champion", "ct", "mod", "admin")
}