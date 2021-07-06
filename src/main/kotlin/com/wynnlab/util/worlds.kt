package com.wynnlab.util

import com.wynnlab.wynnlab
import org.bukkit.*
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.StandardCopyOption

fun createInstancedWorld(worldName: String, instanceWorldName: String) = Deferred {
    //player.sendMessage("Copying $worldName ...")
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
    loadWorld(instanceWorldName)
}

@OnlyAsync
private fun loadWorld(name: String): InstancedWorld? {
    //player.sendMessage("Starting wc")

    val wc = WorldCreator.ofNameAndKey(name,
        NamespacedKey(wynnlab, name)
    )
    //player.sendMessage("Initialized wc")

    wc.type(WorldType.FLAT)
    wc.generateStructures(false)
    wc.environment(World.Environment.NORMAL)
    wc.generatorSettings("""{"layers":[{"height":0,"block":"minecraft:air"}],"biome":"minecraft:the_void","structures":{"stronghold":{"distance":0,"count":0,"spread":0},"structures":{}}}""")
    wc.hardcore(true)
    //player.sendMessage("Set wc options")

    //player.sendMessage("Set world border")

    Bukkit.getScheduler().runTaskLaterAsynchronously(wynnlab, { ->
        deleteDir(File("./$name"))
    }, 20L)

    return InstancedWorld(name, wc)
}

class InstancedWorld(val name: String, wc: WorldCreator) {
    lateinit var world: World
    init {
        Bukkit.getScheduler().scheduleSyncDelayedTask(wynnlab) {
            world = wc.createWorld()!!
            world.worldBorder.apply {
                center = Location(world, .0, .0, .0)
                size = 64.0
            }
        }
    }

    fun sendPlayer(player: Player, x: Double, y: Double, z: Double, yaw: Float = 0f, pitch: Float = 0f) {
        player.teleport(Location(world, x, y, z, yaw, pitch))
    }

    fun unload() {
        Bukkit.getServer().unloadWorld(world, false)
        Bukkit.getScheduler().runTaskAsynchronously(wynnlab) { -> deleteDir(File("./$name")) }
    }

    fun unloadOnZeroPlayers() {
        Bukkit.getScheduler().runTaskTimer(wynnlab, { task ->
            if (world.playerCount < 1) {
                unload()
                task.cancel()
            }
        }, 20L, 20L)
    }
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