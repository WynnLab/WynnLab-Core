package com.wynnlab.locations

import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

@Suppress("unchecked_cast")
fun loadLocations() {
    val file = File(plugin.dataFolder, "locations.yml")
    if (!file.exists()) return

    val config = YamlConfiguration()
    config.load(file)

    val ls = config.getList("locations") as? List<Location> ?: return

    ls.forEach { locations.add(it) }

    Bukkit.broadcastMessage(locations.toString())
}

fun testLocations(player: Player): List<Location> = locations.filter { player.location in it }

fun Player.updateLocations() {
    val before = playerLocations[this]
    val now = testLocations(this)
    playerLocations[this] = now

    val changed = before?.let { b -> now.filter { it !in b } } ?: now

    var announced = false
    for (l in changed) {
        if (!announced && l.announce(this))
            announced = true
    }
}

val locations: MutableList<Location> = mutableListOf()

val playerLocations: MutableMap<Player, List<Location>> = mutableMapOf()