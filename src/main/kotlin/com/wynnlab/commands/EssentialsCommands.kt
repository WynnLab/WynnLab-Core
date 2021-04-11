package com.wynnlab.commands

import com.wynnlab.api.sendWynnMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EssentialsCommands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }
        return when (label) {
            "msg" -> msg(sender, args)
            "r" -> r(sender, args)
            "party" -> party(sender, args)
            "p" -> p(sender, args)
            else -> false
        }
    }

    private fun msg(player: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            player.sendWynnMessage("commands.no_receiver")
            return false
        }
        if (args.size < 2) {
            player.sendWynnMessage("commands.no_message")
            return false
        }
        val receivers: List<Player> = player.server.selectEntities(player, args[0]).filterIsInstance<Player>()
        val message = args.slice(1 until args.size).joinToString(" ")
        player.sendMessage("§7[§r${player.name} §6➤ §r${args[0]}§7] §r$message")
        for (receiver in receivers) {
            receiver.sendMessage("§7[§r${player.name} §6➤ §r${receiver.name}§7] §r$message")
            conversations[receiver] = player
        }
        return true
    }

    val conversations = mutableMapOf<Player, Player>()

    private fun r(player: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            player.sendWynnMessage("commands.no_message")
            return false
        }
        val receiver = conversations[player] ?: run {
            player.sendWynnMessage("commands.no_conversation")
            return true
        }
        val message = args.joinToString(" ")
        player.sendMessage("§7[§r${player.name} §6➤ §r${receiver.name}§7] §r$message")
        receiver.sendMessage("§7[§r${player.name} §6➤ §r${receiver.name}§7] §r$message")
        conversations[receiver] = player
        return true
    }

    private fun party(player: Player, args: Array<out String>): Boolean {
        return false
    }

    private fun p(player: Player, args: Array<out String>): Boolean {
        return false
    }
}