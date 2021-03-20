package com.wynnlab

import com.wynnlab.commands.CastCommand
import com.wynnlab.commands.ClassCommand
import com.wynnlab.commands.ItemCommand
import com.wynnlab.listeners.CastListener
import com.wynnlab.listeners.PlayerClickListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        registerListeners()
        registerCommands()
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

    private fun registerListeners() {
        val manager = Bukkit.getPluginManager()
        manager.registerEvents(castListener, this)
        manager.registerEvents(playerClickListener, this)
    }
}

private lateinit var instance: Main
val plugin get() = instance