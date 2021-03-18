package com.wynnlab

import com.wynnlab.commands.ClassCommand
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        registerCommands()
    }

    val classCommand by lazy { ClassCommand() }

    private fun registerCommands() {
        getCommand("class")?.setExecutor(classCommand)
    }
}

private lateinit var instance: Main
val plugin get() = instance