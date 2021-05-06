package com.wynnlab.commands.tab_completers

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object ClassTabCompleter : BaseTabCompleter("class") {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> = if (args.size == 1)
        completeWord(classes, args[0])
    else emptyList()

    private val classes = listOf("archer", "assassin", "mage", "shaman", "warrior")
}