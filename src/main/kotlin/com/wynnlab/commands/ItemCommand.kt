package com.wynnlab.commands

import com.wynnlab.items.WynnItem
import com.wynnlab.items.getAPIResults
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ItemCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("§cPlease specify an item name")
            return false
        }
        val itemName = args.joinToString(" ")
        val jsonObjects = getAPIResults(itemName)
        if (jsonObjects.isEmpty()) {
            sender.sendMessage("§c\"$itemName\" was not found")
            return true
        }
        val wynnItem = WynnItem.parse(jsonObjects[0]) //TODO
        sender.inventory.addItem(wynnItem.generateNewItem(sender))
        return true
    }
}