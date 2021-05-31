package com.wynnlab

import com.wynnlab.commands.registerCommands
import com.wynnlab.commands.tab_completers.registerTabCompleters
import com.wynnlab.listeners.registerListeners
import com.wynnlab.localization.loadLanguages
import com.wynnlab.locations.loadLocations
import com.wynnlab.scoreboard.Scoreboard
import com.wynnlab.util.DEG2RAD
import com.wynnlab.util.RAD2DEG
import com.wynnlab.util.registerSerializers
import com.wynnlab.util.saveAllResources
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import org.python.util.PythonInterpreter

class Main : JavaPlugin() {
    override fun onLoad() {
        instance = this

        python.set("plugin", this)
        python.set("random", random)
        python.set("RAD2DEG", RAD2DEG)
        python.set("DEG2RAD", DEG2RAD)
        python.set("VectorUP", Vector(0, 1, 0))

        saveAllResources()
    }

    override fun onEnable() {
        registerCommands()
        registerTabCompleters()
        registerCommands()
        registerListeners()
        registerSerializers()

        setGameRules()
        Players.initPlayers()

        MainThread.schedule()

        loadClasses()

        loadLanguages()

        loadLocations()

        Bukkit.getServer().spigot().spigotConfig.set("settings.attribute.maxHealth.max", 1000000)
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach(Players::removePlayerFromActivities)

        Scoreboard.scoreboards.clear()

        python.close()
    }

    private fun setGameRules() {
        Bukkit.getWorlds().forEach {
            setGameRules(it)
        }
    }

    fun setGameRules(world: World) {
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.setGameRule(GameRule.DISABLE_RAIDS, false)
        world.setGameRule(GameRule.DO_FIRE_TICK, false)
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, false)
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false)
        world.setGameRule(GameRule.FALL_DAMAGE, false)
        world.setGameRule(GameRule.MOB_GRIEFING, false)
        world.setGameRule(GameRule.NATURAL_REGENERATION, false)
        //world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true)
        world.setGameRule(GameRule.SPAWN_RADIUS, 0)
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
    }
}

private lateinit var instance: Main
val wynnlab get() = instance

const val PREFIX = "§8[§bWynnLab§8] §r"

val random = java.util.Random()

val python by lazy { PythonInterpreter() }

val NL_REGEX = Regex("\\n")