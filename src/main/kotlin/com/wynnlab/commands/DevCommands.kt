package com.wynnlab.commands

import com.wynnlab.api.data
import com.wynnlab.api.getContainer
import com.wynnlab.api.getId
import com.wynnlab.api.sendWynnMessage
import com.wynnlab.localization.Language
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataContainer

/**
 * Commands:
 * - itemdata
 * - script //TODO
 */
class DevCommands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("§4You need to be an operator to execute this command")
            return true
        }
        return when (label) {
            "itemdata" -> itemdata(sender, args)
            "getid" -> getid(sender, args)
            else -> false
        }
    }

    private fun itemdata(sender: CommandSender, args: Array<out String>): Boolean {
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
        return true
    }

    private fun getid(sender: CommandSender, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            if (sender is Player)
                sender.sendWynnMessage("commands.no_op")
            else
                sender.sendMessage(Language.en_us.getMessage("commands.no_op"))
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("§cPlease specify an id name")
            return false
        }

        val id = args[0]

        val target: Player = if (args.size == 1) {
            if (sender is Player) sender else null
        } else {
            if (args.size == 2) sender.server.selectEntities(sender, args[1]).filterIsInstance<Player>().let {
                if (it.size != 1) null
                else it[0]
            } else null
        } ?: run {
            sender.sendMessage("§cPlease specify one valid target")
            return false
        }

        sender.sendMessage("§b${target.name}§7's id '§e$id§7' is: ${target.getId(id).let { if (it > 0) "§a+$it" else "§c$it" }}")

        return true
    }
}