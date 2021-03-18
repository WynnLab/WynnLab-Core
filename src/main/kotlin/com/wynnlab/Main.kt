package com.wynnlab

import com.wynnlab.commands.ClassCommand
import com.wynnlab.commands.ItemCommand
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        registerCommands()
    }

    val classCommand by lazy { ClassCommand() }
    val itemCommand by lazy { ItemCommand() }

    private fun registerCommands() {
        getCommand("class")?.setExecutor(classCommand)
        getCommand("item")?.setExecutor(itemCommand)
    }
}

private lateinit var instance: Main
val plugin get() = instance