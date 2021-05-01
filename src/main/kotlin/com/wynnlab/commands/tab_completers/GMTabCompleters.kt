package com.wynnlab.commands.tab_completers

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object GMTabCompleters : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> =
        when (alias) {
            "upload" -> if (args.size == 1)
                completeWord(uploadArgs, args[0])
                else emptyList()
            "wlrl" -> when {
                args.size == 1 -> completeWord(wlrlArgs, args[0])
                else -> emptyList()
            }
            else -> emptyList()
        }

    private val uploadArgs = listOf("item", "mob", "mob_spell", "music")

    private val wlrlArgs = listOf("mob", "mobs", "class", "classes")
}