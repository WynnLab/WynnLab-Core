package com.wynnlab

import com.wynnlab.commands.CastCommand
import com.wynnlab.commands.ClassCommand
import com.wynnlab.commands.ItemCommand
import com.wynnlab.listeners.CastListener
import com.wynnlab.listeners.FallingBlockListener
import com.wynnlab.listeners.PlayerClickListener
import com.wynnlab.spells.Spell
import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin
import org.python.util.PythonInterpreter

class Main : JavaPlugin() {
    override fun onLoad() {
        instance = this
        python.setOut(System.out)
        python.set("plugin", this)
        python.set("random", random)
    }

    override fun onEnable() {
        registerListeners()
        registerCommands()
        registerSerializers()

        MainThread.schedule()

        loadClasses()
    }

    override fun onDisable() {
        python.close()
    }

    val classCommand by lazy { ClassCommand() }
    val itemCommand by lazy { ItemCommand() }
    val castCommand by lazy { CastCommand() }

    private fun registerCommands() {
        getCommand("class")?.setExecutor(classCommand)
        getCommand("item")?.setExecutor(itemCommand)
        getCommand("cast")?.setExecutor(castCommand)
    }

    val castListener by lazy { CastListener() }
    val playerClickListener by lazy { PlayerClickListener() }
    val fallingBlockListener by lazy { FallingBlockListener() }

    private fun registerListeners() {
        val manager = Bukkit.getPluginManager()
        manager.registerEvents(castListener, this)
        manager.registerEvents(playerClickListener, this)
        manager.registerEvents(fallingBlockListener, this)
    }

    private fun registerSerializers() {
        ConfigurationSerialization.registerClass(WynnClass::class.java)
        ConfigurationSerialization.registerClass(Spell::class.java)
    }
}

private lateinit var instance: Main
val plugin get() = instance

val random = java.util.Random()

val python = PythonInterpreter()