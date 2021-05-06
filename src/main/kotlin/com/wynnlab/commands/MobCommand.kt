package com.wynnlab.commands

import com.wynnlab.entities.WynnMob
import com.wynnlab.plugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

object MobCommand : BaseCommand("mob") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("§cPlease specify a mob name")
            return false
        }

        val mobName = args.joinToString(" ")

        val mob = mobs[mobName] ?: run {
            val folder = File(plugin.dataFolder, "mobs")
            folder.mkdir()

            val file = File(folder, "$mobName.yml")
            if (!file.exists()) {
                sender.sendMessage("§cThis mob doesn't exist")
                return true
            }

            val config = YamlConfiguration()
            config.load(file)

            config.getSerializable("mob", WynnMob::class.java)
        }?.also { mobs[mobName] = it } ?: run {
            sender.sendMessage("§cMalformed config")
            return true
        }

        mob.spawn(sender.location)

        return true
    }

    val mobs = hashMapOf<String, WynnMob>()
}