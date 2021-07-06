package com.wynnlab.commands.tab_completers

import com.wynnlab.items.Build
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BuildTabCompleter : BaseTabCompleter("build") {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? = if (sender is Player && args.size == 2) when (args[0]) {
        "save" -> emptyList()
        "equip", "delete", "publish" -> completeWord(Build.buildNamesOf(sender).toList(), args[1])
        else -> null
    } else if (args.size == 1) completeWord(subcommands, args[0]) else emptyList()

    private val subcommands = listOf("save", "equip", "delete", "publish", "show")
}