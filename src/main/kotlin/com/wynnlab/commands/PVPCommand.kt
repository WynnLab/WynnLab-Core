package com.wynnlab.commands

import com.wynnlab.api.togglePVP
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PVPCommand : BaseCommand("pvp") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size != 1)
            return false

        if (sender !is Player) {
            sender.sendMessage("§cThis command ccan only be executed by players!")
            return true
        }

        sender.togglePVP()

        return false
    }
}