package com.wynnlab.commands

import com.wynnlab.items.SpecialItems
import com.wynnlab.items.WynnItem
import com.wynnlab.items.getAPIResults
import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ItemCommand : CommandExecutor {
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

        try {
            sender.inventory.addItem(SpecialItems.valueOf(itemName).itemStack(sender))
        } catch (ignored: IllegalArgumentException) {  }

        getAPIResults(itemName).execute { jsonObjects ->
            if (jsonObjects.isEmpty()) {
                sender.sendMessage("§c\"$itemName\" was not found")
                return@execute
            }
            if (jsonObjects.size == 1) {
                val wynnItem = WynnItem.parse(jsonObjects[0])
                sender.inventory.addItem(wynnItem.generateNewItem(sender))
            } else {
                val inv = Bukkit.createInventory(sender, (jsonObjects.size / 9 + 1) * 9, "Your items")
                val wynnItems = jsonObjects.mapNotNull { WynnItem.parse(it).takeUnless { we -> we.isNormal() } }
                inv.addItem(*Array(wynnItems.size) { i ->
                    wynnItems[i].generateNewItem(sender)
                })
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                    sender.openInventory(inv)
                }
            }
        }

        return true
    }
}