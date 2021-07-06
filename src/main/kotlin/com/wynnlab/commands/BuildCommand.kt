package com.wynnlab.commands

import com.wynnlab.items.Build
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BuildCommand : BaseCommand("build") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = (sender as? Player) ?: return true
        if (args.isEmpty()) return false

        return when (args[0]) {
            "save" -> save(player, args)
            "equip" -> equip(player, args)
            "delete" -> delete(player, args)
            "publish" -> publish(player, args)
            "show" -> show(player, args)
            else -> false
        }
    }

    private fun save(player: Player, args: Array<out String>): Boolean {
        if (args.size != 2) return false
        val eq = player.equipment
        Build[player.uniqueId, args[1]] = Build(eq?.helmet, eq?.chestplate, eq?.leggings, eq?.boots, eq?.itemInMainHand)
        return true
    }

    private fun equip(player: Player, args: Array<out String>): Boolean {
        if (args.size != 2) return false
        (Build[player.uniqueId, args[1]] ?: run {
            player.sendMessage("§cThe build §f${args[1]}§c doesn't exist")
            return true
        }).equip(player)
        return true
    }

    private fun delete(player: Player, args: Array<out String>): Boolean {
        if (args.size != 2) return false
        Build.remove(player.uniqueId, args[1]) ?: player.sendMessage("§cThe build §f${args[1]}§c doesn't exist")
        return true
    }

    private fun publish(player: Player, args: Array<out String>): Boolean {
        if (args.size == 2) player.sendMessage("§cSpecify a name for the build (e.g. §fVery cool build§c)")
        if (args.size < 3) return false
        (Build[player.uniqueId, args[1]] ?: run {
            player.sendMessage("§cThe build §f${args[1]}§c doesn't exist")
            return true
        }).publish(args.slice(2 until args.size).joinToString(" "))
        return true
    }

    private fun show(player: Player, args: Array<out String>): Boolean {
        if (args.size != 1) return false
        Build.GUI(player).show()
        return true
    }
}