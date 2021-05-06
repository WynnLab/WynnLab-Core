package com.wynnlab.listeners

import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.event.Listener

abstract class BaseListener : Listener

fun registerListeners() {
    registerListener(CastListener())
    registerListener(DamageListener())
    registerListener(FallingBlockListener())
    registerListener(GUIListener())
    registerListener(PlayerClickListener())
    registerListener(PlayerEventsListener())
    registerListener(ProjectileHitListener())
}

private fun registerListener(listener: BaseListener) {
    Bukkit.getPluginManager().registerEvents(listener, plugin)
}