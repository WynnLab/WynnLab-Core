package com.wynnlab.commands.tab_completers

import com.wynnlab.items.getAPIResults
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object ItemTabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        if (args.isEmpty())
            return emptyList()

        val itemName = args.joinToString(" ")

        if (itemName.length < 3)
            return listOf(args.last())

        val apiResults = getAPIResults(itemName).task()

        val list = apiResults.mapNotNull {
            it["name"] as String?
        }

        return if (args.size == 1)
            list
        else
            list.filter { it.startsWith(itemName, ignoreCase = true) }
                .map { it.substring(itemName.length - args.last().length) }
    }
}