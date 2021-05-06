package com.wynnlab.commands

import com.wynnlab.api.setWynnClass
import com.wynnlab.gui.ClassGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ClassCommand : BaseCommand("class") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }
        if (args.size != 1) {
            return if (args.isEmpty()) {
                ClassGUI(sender).show()
                true
            } else false
        }
        sender.setWynnClass(args[0].toUpperCase())
        return true
    }
}