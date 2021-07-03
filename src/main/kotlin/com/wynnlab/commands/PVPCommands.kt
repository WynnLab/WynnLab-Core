package com.wynnlab.commands

import com.wynnlab.api.hasScoreboardTag
import com.wynnlab.api.sendWynnMessage
import com.wynnlab.gui.PVPGUI
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PVPCommands : BaseCommand("pvp", "hub", "leave", "stats") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return when (label) {
            "pvp" -> pvp(sender, args)
            "hub" -> hub(sender, args)
            "leave", "l" -> leave(sender, args)
            "stats" -> stats(sender, args)
            else -> false
        }
    }

    fun pvp(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.isNotEmpty())
            return false

        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players!")
            return true
        }

        if (sender.hasScoreboardTag("pvp")) {
            sender.sendMessage("§cYou can't use that command during pvp")
            return true
        }

        PVPGUI(sender).show()

        return true
    }

    private fun hub(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.isNotEmpty())
            return false

        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players!")
            return true
        }

        sender.teleport(Bukkit.getWorld("neww")!!.spawnLocation)

        return true
    }

    private fun leave(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.isNotEmpty())
            return false

        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players!")
            return true
        }

        if (!sender.hasScoreboardTag("pvp")) {
            sender.sendMessage("§cThere's nothing to leave")
            return true
        }

        sender.teleport(Bukkit.getWorld("neww")!!.spawnLocation)

        return true
    }

    private fun stats(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.size > 1)
            return false

        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players!")
            return true
        }

        if (!sender.hasScoreboardTag("ffa")) {
            sender.sendMessage("§cYou don't have stats here")
            return true
        }

        val target = if (args.isEmpty()) sender else Bukkit.getPlayer(args[0]) ?: run {
            sender.sendWynnMessage("messages.player_not_exist")
            return true
        }

        sender.sendWynnMessage("messages.stats.stats_of", target.name)
        sender.sendWynnMessage("messages.stats.kills", 0)
        sender.sendWynnMessage("messages.stats.deaths", 0)
        sender.sendWynnMessage("messages.stats.kd", 0)
        sender.sendWynnMessage("messages.stats.damage_dealt", 0)
        sender.sendWynnMessage("messages.stats.damage_taken", 0)

        return true
    }
}