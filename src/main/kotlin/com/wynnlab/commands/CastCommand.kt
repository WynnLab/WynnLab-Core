package com.wynnlab.commands

import com.wynnlab.api.getWynnClass
import com.wynnlab.events.SpellCastEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CastCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }
        if (args.size != 1) {
            sender.sendMessage("§cPlease specify a spell id")
            return false
        }
        val wynnClass = sender.getWynnClass()
        if (wynnClass == null) {
            sender.sendMessage("§cPLease select a class first")
            return true
        }
        val spell = args[0].toIntOrNull()
        if (spell == null || spell < 0 || spell > 4) {
            sender.sendMessage("§c'${args[0]}' is not a valid spell id")
            return false
        }
        Bukkit.getPluginManager().callEvent(SpellCastEvent(sender, spell))
        return true
    }
}