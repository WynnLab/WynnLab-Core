package com.wynnlab.commands.tab_completers

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object NoTabCompleter : BaseTabCompleter("pvp") {
    override fun onTabComplete(
        _0: CommandSender,
        _1: Command,
        _2: String,
        _3: Array<out String>
    ): List<String>? = null
}