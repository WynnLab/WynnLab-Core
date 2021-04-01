package com.wynnlab

import com.wynnlab.commands.CastCommand
import com.wynnlab.commands.ClassCommand
import com.wynnlab.commands.ItemCommand
import com.wynnlab.commands.RankCommand
import com.wynnlab.listeners.CastListener
import com.wynnlab.listeners.FallingBlockListener
import com.wynnlab.listeners.PlayerClickListener
import com.wynnlab.listeners.PlayerEventsListener
import com.wynnlab.ranks.Rank
import com.wynnlab.spells.Spell
import com.wynnlab.util.RAD2DEG
import com.wynnlab.util.saveAllResources
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin
import org.python.util.PythonInterpreter

class Main : JavaPlugin() {
    override fun onLoad() {
        instance = this

        python.set("plugin", this)
        python.set("random", random)
        python.set("RAD2DEG", RAD2DEG)

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

    private fun registerCommands() {
        getCommand("class")?.setExecutor(classCommand)
        getCommand("item")?.setExecutor(itemCommand)
        getCommand("cast")?.setExecutor(castCommand)
        getCommand("rank")?.setExecutor(rankCommand)
    }

    val castListener by lazy { CastListener() }
    val playerClickListener by lazy { PlayerClickListener() }
    val fallingBlockListener by lazy { FallingBlockListener() }
    val playerEventsListener by lazy { PlayerEventsListener() }

    private fun registerListeners() {
        val manager = Bukkit.getPluginManager()
        manager.registerEvents(castListener, this)
        manager.registerEvents(playerClickListener, this)
        manager.registerEvents(fallingBlockListener, this)
        manager.registerEvents(playerEventsListener, this)
    }

    private fun registerSerializers() {
        ConfigurationSerialization.registerClass(WynnClass::class.java)
        ConfigurationSerialization.registerClass(Spell::class.java)
    }

    fun setGameRules() {
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

val random = java.util.Random()

val python = PythonInterpreter()