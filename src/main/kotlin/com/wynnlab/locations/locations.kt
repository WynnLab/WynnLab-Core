package com.wynnlab.locations

import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
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
    val labb = playerLocations[this]

    val before = labb?.locations
    val now = testLocations(this)

    val bb = labb?.bb ?: createLBB(this)

    val entered = before?.let { b -> now.filter { it !in b } } ?: now

    var announced = false
    for (l in entered) {
        if (!announced && l.announce(this))
            announced = true

        l.discover(this)

        bb.setTitle(l.bossBarTitle)
    }

    if (!announced) {
        val left = before?.let { b -> b.filter { it !in now } } ?: emptyList()

        for (l in left) {
            if (!announced && l.announce(this, false))
                announced = true
        }
    }

    if (now.isEmpty()) {
        bb.setTitle("§c")
    }

    playerLocations[this] = LocationsAndBB(now, bb)
}

private fun createLBB(player: Player): BossBar =
    Bukkit.createBossBar(NamespacedKey(plugin, "locations_${player.name}"), "Title", BarColor.BLUE, BarStyle.SOLID).apply {
        isVisible = true
        progress = 1.0

        addPlayer(player)
    }

val locations: MutableList<Location> = mutableListOf()

class LocationsAndBB(
    val locations: List<Location>,
    val bb: BossBar,
)

val playerLocations: MutableMap<Player, LocationsAndBB> = mutableMapOf()