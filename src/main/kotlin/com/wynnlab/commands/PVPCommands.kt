package com.wynnlab.commands

import com.wynnlab.api.hasScoreboardTag
import com.wynnlab.api.sendWynnMessage
import com.wynnlab.gui.PVPGUI
import com.wynnlab.plugin
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.StandardCopyOption

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

        PVPGUI(sender).show()

        return true
    }

    fun hub(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.isNotEmpty())
            return false

        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players!")
            return true
        }

        sender.teleport(Bukkit.getWorld("neww")!!.spawnLocation)

        return true
    }

    fun leave(sender: CommandSender, args: Array<out String>): Boolean {
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

    fun stats(sender: CommandSender, args: Array<out String>): Boolean {
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

    internal fun world(player: Player) {
        player.sendMessage("Copying...")
        Bukkit.getScheduler().runTaskAsynchronously(plugin) { ->
            try {
                Files.copy(File("./world/").toPath(), File("./duel_instance_${player.uniqueId}/").toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS)
            } catch (_: DirectoryNotEmptyException) {
                deleteDir(File("./duel_instance_${player.uniqueId}/"))
                Files.copy(File("./world/").toPath(), File("./duel_instance_${player.uniqueId}/").toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS)
            }
            copyDir(File("./world"), File("./duel_instance_${player.uniqueId}"))
            File("./duel_instance_${player.uniqueId}/uid.dat").delete()
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                loadWorld(player)
            }
        }
    }

    fun loadWorld(player: Player) {
        player.sendMessage("Starting wc")

        val wc = WorldCreator.ofNameAndKey("duel_instance_${player.uniqueId}",NamespacedKey(plugin, "duel_instance_${player.uniqueId}"))
        player.sendMessage("Initialized wc")

        wc.type(WorldType.FLAT)
        wc.generateStructures(false)
        wc.environment(World.Environment.NORMAL)
        wc.generatorSettings("""{"layers":[{"height":0,"block":"minecraft:air"}],"biome":"minecraft:the_void","structures":{"stronghold":{"distance":0,"count":0,"spread":0},"structures":{}}}""")
        wc.hardcore(true)
        player.sendMessage("Set wc options")

        val world = wc.createWorld() ?: run {
            player.sendMessage("something went wrong")
            return
        }
        player.sendMessage("Created world")

        world.keepSpawnInMemory = false
        world.isAutoSave = false
        world.noTickViewDistance = 2
        world.viewDistance = 8

        plugin.setGameRules(world)
        player.sendMessage("Set game rules")

        world.worldBorder.apply {
            center = Location(world, .0, .0, .0)
            size = 64.0
        }
        player.sendMessage("Set world border")

        world.getBlockAt(Location(world, .0, .0, .0)).type = Material.STONE

        player.teleport(Location(world, .5, 1.0, .5))

        player.sendMessage("Done")

        Bukkit.getScheduler().runTaskAsynchronously(plugin) { ->
            deleteDir(File("./duel_instance_${player.uniqueId}"))
        }

        Bukkit.getScheduler().runTaskTimer(plugin, { task ->
            if (world.playerCount < 1) {
                Bukkit.getServer().unloadWorld(world, false)
                Bukkit.getScheduler().runTaskAsynchronously(plugin) { -> deleteDir(File("./duel_instance_${player.uniqueId}")) }
                task.cancel()
            }
        }, 20L, 20L)

        player.gameMode = GameMode.CREATIVE
    }

    fun copyDir(from: File, to: File) {
        from.list()!!.forEach { n ->
            val f = File(from, n)
            val t =  File(to, n)
            Files.copy(f.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS)
            if (f.isDirectory)
                copyDir(f, t)
        }
    }

    fun deleteDir(dir: File) {
        fun deleteDir(dir: File) {
            dir.listFiles()!!.forEach { f ->
                if (f.isDirectory)
                    deleteDir(f)
                f.delete()
                //println("deleted file: $f")
            }
        }
        deleteDir(dir)
        dir.delete()
    }
}