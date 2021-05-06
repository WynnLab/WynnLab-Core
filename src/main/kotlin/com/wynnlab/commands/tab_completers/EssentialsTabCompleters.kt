package com.wynnlab.commands.tab_completers

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object EssentialsTabCompleters : BaseTabCompleter("msg", "party") {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> =
        when (alias) {
            "msg" -> {
                if (args.size == 1) {
                        if (sender is Player)
                            sender.world.players.map { it.name }
                        else emptyList()
                } else emptyList()
            }
            "party" -> {
                when {
                    args.size == 1 -> completeWord(partyArgs, args[0])
                    args.size > 1 -> when (args[0]) {
                        "invite" -> if (sender is Player)
                            sender.world.players.map { it.name }
                        else emptyList()
                        else -> emptyList()
                    }
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }

    private val partyArgs = listOf("create", "invite", "join", "leave")
}