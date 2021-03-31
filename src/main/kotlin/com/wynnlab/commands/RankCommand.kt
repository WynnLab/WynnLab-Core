package com.wynnlab.commands

import com.wynnlab.ranks.Rank
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RankCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("§cYou need to be an operator to execute this command")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("§cPlease specify a rank")
            return false
        }

        val rank: Rank = try {
            Rank.valueOf(args[0].toUpperCase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("§cPlease specify a valid rank")
            return false
        }

        val targets: List<Player> = if (args.size == 1) {
            if (sender is Player) listOf(sender) else null
        } else {
            if (args.size == 2) sender.server.selectEntities(sender, args[1]).filterIsInstance<Player>() else null
        } ?: run {
            sender.sendMessage("§cPlease specify a valid target")
            return false
        }

        targets.forEach {
            rank.apply(it)
        }

        return true
    }
}