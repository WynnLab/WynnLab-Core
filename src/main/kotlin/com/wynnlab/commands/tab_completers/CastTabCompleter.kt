package com.wynnlab.commands.tab_completers

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object CastTabCompleter : BaseTabCompleter("cast") {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> = if (args.size == 1)
        castIds
    else emptyList()

    private val castIds = listOf("1", "2", "3", "4")
}