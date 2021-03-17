package com.wynnlab

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        server.broadcastMessage("Enabled WynnLab")
    }
}