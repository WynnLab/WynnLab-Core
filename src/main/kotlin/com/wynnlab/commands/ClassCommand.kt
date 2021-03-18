package com.wynnlab.commands

import com.wynnlab.api.setWynnClass
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ClassCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("§cPlease specify a class")
            return false
        }
        sender.setWynnClass(args[0].toUpperCase())
        return true
    }
}