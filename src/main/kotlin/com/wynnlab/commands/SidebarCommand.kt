package com.wynnlab.commands

import com.wynnlab.api.hasScoreboardTag
import com.wynnlab.scoreboard.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SidebarCommand : BaseCommand("sidebar", "sb") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }
        if (args.size != 1)
            return false

        return when (args[0]) {
            "info" -> {
                InfoSidebar.update(sender)
                true
            }
            "party" -> {
                PartyHealthSidebar.update(sender)
                true
            }
            "health" -> {
                when {
                    sender.hasScoreboardTag("ffa") -> FFAHealthSidebar
                    sender.hasScoreboardTag("duel") -> DuelHealthSidebar
                    else -> PartyHealthSidebar
                }.update(sender)
                true
            }
            "damage" -> {
                when {
                    sender.hasScoreboardTag("ffa") -> FFADamageSidebar
                    sender.hasScoreboardTag("duel") -> DuelDamageSidebar
                    else -> BossDamageSidebar
                }.update(sender)
                true
            }
            "kills" -> if (sender.hasScoreboardTag("ffa")) { FFAKillsSidebar.update(sender); true }
                else false
            else -> false
        }
    }
}