package com.wynnlab.commands

import com.wynnlab.entities.WynnMob
import net.minecraft.server.v1_16_R3.EntityTypes
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DummyCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }
        if (args.isNotEmpty())
            return false

        //Dummy(sender.location).spawn(sender.world)
        val wynnMob = WynnMob("Dummy", EntityTypes.VINDICATOR, WynnMob.AI.NONE, 0, 100000, 0, 0..0,
        1.0, null, .0, .0, false, false, false, .0,
        null, null, null, null, null, .0,
            WynnMob.Equipment(null), listOf())

        wynnMob.spawn(sender.location)

        return true
    }
}