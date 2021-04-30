package com.wynnlab.commands

import com.wynnlab.WynnClass
import com.wynnlab.classes
import com.wynnlab.loadClasses
import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class GMCommands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean =
        when (label) {
            "upload" -> upload(sender, args)
            "wlrl" -> wlrl(sender, args)
            else -> false
        }

    private fun upload(sender: CommandSender, args: Array<out String>): Boolean {
        if (!/*sender.hasPermission("gm")*/sender.isOp) {
            sender.sendMessage("§cYou don't have permission to perform this command")
            return true
        }

        if (args.size < 3)
            return false

        val newFileLoc = when (args[0]) {
            "item" -> File(plugin.dataFolder, "custom_items")
            "mob" -> File(plugin.dataFolder, "mobs")
            "mob_spell" -> File(File(plugin.dataFolder, "mobs"), "scripts")
            "music" -> File(Bukkit.getPluginManager().getPlugin("JukeBox")?.dataFolder, "songs")
            else -> return false
        }

        if (!newFileLoc.exists()) newFileLoc.mkdirs()

        val downloadFile = File(newFileLoc, args[1])
        if (!downloadFile.createNewFile()) {
            sender.sendMessage("The file ${args[1]} already exists")
            return true
        }

        val url = try {
            URL(args.slice(2 until args.size).joinToString("%20"))
        } catch (e: MalformedURLException) {
            sender.sendMessage("§cMalformed URL")
            return false
        }

        val text = try {
            url.openStream().reader().readText()
        } catch (e: IOException) {
            sender.sendMessage("§cIO Exception")
            return true
        }

        FileWriter(downloadFile).use { writer ->
            writer.write(text)
        }

        sender.sendMessage("§aSuccessfully uploaded your ${args[0]} to ${args[1]}")

        return true
    }

    private fun wlrl(sender: CommandSender, args: Array<out String>): Boolean {
        if (!/*sender.hasPermission("gm")*/sender.isOp) {
            sender.sendMessage("§cYou don't have permission to perform this command")
            return true
        }

        if (args.isEmpty())
            return false

        when (args[0]) {
            "mobs" -> {
                plugin.mobCommand.mobs.clear()
            }
            "mob" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cPlease specify a mob to reload")
                    return false
                }
                plugin.mobCommand.mobs.remove(args[1]) ?: run {
                    sender.sendMessage("§cThe mob ${args[1]} doesn't exist")
                    return false
                }
            }
            "classes" -> {
                classes.clear()
                loadClasses()
            }
            "class" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cPlease specify a class to reload")
                    return false
                }
                classes[args[1]] ?: run {
                    sender.sendMessage("§cThe class ${args[1]} doesn't exist")
                    return true
                }
                val configFile = (File(plugin.dataFolder, "classes").listFiles { _, name -> name.equals(args[1]) })?.let { files ->
                    if (files.isEmpty()) null
                    else files[0]
                } ?: run {
                    sender.sendMessage("§cThe class ${args[1]} doesn't exist")
                    return true
                }
                val config = YamlConfiguration()
                config.load(configFile)
                config.getSerializable("class", WynnClass::class.java)?.let { classes.replace(args[1], it) } ?: run {
                    sender.sendMessage("§cMalformed config")
                    return true
                }
            }
        }

        sender.sendMessage("§aReloaded ${args.joinToString(" ")}")

        return true
    }
}