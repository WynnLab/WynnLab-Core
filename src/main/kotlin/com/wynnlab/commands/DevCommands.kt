package com.wynnlab.commands

import com.wynnlab.api.data
import com.wynnlab.api.get
import com.wynnlab.api.persistentDataTypeFromString
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

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
                    sender.sendMessage("§cPlease specify a data type (e.g. 'int' or 'String')")
                    return false
                }
                if (args.size < 2) {
                    sender.sendMessage("§cPlease specify a key")
                    return false
                }
                val type = persistentDataTypeFromString(args[0])
                if (type == null) {
                    sender.sendMessage("§cThe type ${args[0]} doesn't exist")
                    return true
                }
                val key = args.slice(1 until args.size).joinToString(" ")
                sender.sendMessage(
                    (sender.inventory.itemInMainHand.itemMeta.data[key, type] ?:
                    "§cThis item has no '${args[0]}' value for key '$key'").let {
                        if (it is Array<*>) it.contentToString()
                        else it.toString()
                    }
                )
            }
        }
        return true
    }
}