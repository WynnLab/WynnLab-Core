package com.wynnlab.util

import com.wynnlab.wynnlab
import org.bukkit.*
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.StandardCopyOption

fun sendPlayerToInstancedWorld(player: Player, worldName: String, instanceWorldName: String, location: Location = Location(null, .0, .0, .0)) {
    player.sendMessage("Copying $worldName ...")
    Bukkit.getScheduler().runTaskAsynchronously(wynnlab) { ->
        try {
            Files.copy(
                File("./$worldName/").toPath(), File("./$instanceWorldName/").toPath(),
                StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS)
        } catch (_: DirectoryNotEmptyException) {
            deleteDir(File("./$instanceWorldName/"))
            Files.copy(
                File("./$worldName/").toPath(), File("./$instanceWorldName/").toPath(),
                StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS)
        }
        copyDir(File("./$worldName"), File("./$instanceWorldName"))
        File("./$instanceWorldName/uid.dat").delete()
        Bukkit.getScheduler().scheduleSyncDelayedTask(wynnlab) {
            loadWorld(player, instanceWorldName, location)
        }
    }
}

private fun loadWorld(player: Player, name: String, location: Location) {
    player.sendMessage("Starting wc")

    val wc = WorldCreator.ofNameAndKey(name,
        NamespacedKey(wynnlab, name)
    )
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

    wynnlab.setGameRules(world)
    player.sendMessage("Set game rules")

    world.worldBorder.apply {
        center = Location(world, .0, .0, .0)
        size = 64.0
    }
    player.sendMessage("Set world border")

    //world.getBlockAt(Location(world, .0, .0, .0)).let {
    //    if (it.isPassable)
    //    it.type = Material.STONE
    //}

    player.teleport(Location(world, location.x, location.y, location.z, location.yaw, location.pitch))

    player.sendMessage("Done")

    Bukkit.getScheduler().runTaskAsynchronously(wynnlab) { ->
        deleteDir(File("./$name"))
    }

    Bukkit.getScheduler().runTaskTimer(wynnlab, { task ->
        if (world.playerCount < 1) {
            Bukkit.getServer().unloadWorld(world, false)
            Bukkit.getScheduler().runTaskAsynchronously(wynnlab) { -> deleteDir(File("./$name")) }
            task.cancel()
        }
    }, 20L, 20L)
}

private fun copyDir(from: File, to: File) {
    from.list()!!.forEach { n ->
        val f = File(from, n)
        val t =  File(to, n)
        Files.copy(f.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS)
        if (f.isDirectory)
            copyDir(f, t)
    }
}

private fun deleteDir(dir: File) {
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