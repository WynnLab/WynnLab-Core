package com.wynnlab.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Commands:
 * - itemdata
 * - script //TODO
 */
class DevCommands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("§4You aren't allowed to perform this command")
            return true
        }
        when (label) {
            "itemdata" -> {
                if (sender !is Player) {
                    sender.sendMessage("§cThis command can only be performed by players")
                    return true
                }
                if (args.isEmpty()) {
                    sender.sendMessage("§cPlease specify an item index")
                    return false
                }
                sender.performCommand("data get entity @s Inventory[${args[0]}].tag.PublicBukkitValues${
                    if (args.size == 1) "" else args.slice(1 until args.size).joinToString(".", ".") { "wynnlab:$it" }
                }")
            }
        }
        return true
    }
}