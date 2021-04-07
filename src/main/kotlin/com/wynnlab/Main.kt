package com.wynnlab

import com.wynnlab.commands.*
import com.wynnlab.listeners.*
import com.wynnlab.ranks.Rank
import com.wynnlab.spells.Spell
import com.wynnlab.util.DEG2RAD
import com.wynnlab.util.RAD2DEG
import com.wynnlab.util.saveAllResources
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin
import org.python.util.PythonInterpreter
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class Main : JavaPlugin() {
    override fun onLoad() {
        instance = this

        python.set("plugin", this)
        python.set("random", random)
        python.set("RAD2DEG", RAD2DEG)
        python.set("DEG2RAD", DEG2RAD)

        saveAllResources()
    }

    override fun onEnable() {
        registerListeners()
        registerCommands()
        registerSerializers()

        setGameRules()
        Players.initPlayers()

        MainThread.schedule()

        loadClasses()
    }

    override fun onDisable() {
        python.close()
    }

    val classCommand by lazy { ClassCommand() }
    val itemCommand by lazy { ItemCommand() }
    val castCommand by lazy { CastCommand() }
    val rankCommand by lazy { RankCommand() }
    val devCommands by lazy { DevCommands() }

    private fun registerCommands() {
        getCommand("class")?.setExecutor(classCommand)
        getCommand("item")?.setExecutor(itemCommand)
        getCommand("cast")?.setExecutor(castCommand)
        getCommand("rank")?.setExecutor(rankCommand)
        getCommand("itemdata")?.setExecutor(devCommands)
        getCommand("script")?.setExecutor(devCommands)
    }

    val castListener by lazy { CastListener() }
    val playerClickListener by lazy { PlayerClickListener() }
    val fallingBlockListener by lazy { FallingBlockListener() }
    val playerEventsListener by lazy { PlayerEventsListener() }
    val projectileHitListener by lazy { ProjectileHitListener() }
    val guiListener by lazy { GUIListener() }

    private fun registerListeners() {
        val manager = Bukkit.getPluginManager()
        manager.registerEvents(castListener, this)
        manager.registerEvents(playerClickListener, this)
        manager.registerEvents(fallingBlockListener, this)
        manager.registerEvents(playerEventsListener, this)
        manager.registerEvents(projectileHitListener, this)
        manager.registerEvents(guiListener, this)
    }

    private fun registerSerializers() {
        ConfigurationSerialization.registerClass(WynnClass::class.java)
        ConfigurationSerialization.registerClass(Spell::class.java)
    }

    private fun setGameRules() {
        Bukkit.getWorlds().forEach {
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
            it.setGameRule(GameRule.DISABLE_RAIDS, false)
            it.setGameRule(GameRule.DO_FIRE_TICK, false)
            it.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
            it.setGameRule(GameRule.DO_LIMITED_CRAFTING, false)
            it.setGameRule(GameRule.DO_MOB_SPAWNING, false)
            it.setGameRule(GameRule.DO_PATROL_SPAWNING, false)
            it.setGameRule(GameRule.FALL_DAMAGE, false)
            it.setGameRule(GameRule.MOB_GRIEFING, false)
            it.setGameRule(GameRule.NATURAL_REGENERATION, false)
            it.setGameRule(GameRule.REDUCED_DEBUG_INFO, true)
            it.setGameRule(GameRule.SPAWN_RADIUS, 0)
            it.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
        }
    }
}

private lateinit var instance: Main
val plugin get() = instance

const val PREFIX = "§7[§bWynnLab§7] §r"

val random = java.util.Random()

val python by lazy { PythonInterpreter() }