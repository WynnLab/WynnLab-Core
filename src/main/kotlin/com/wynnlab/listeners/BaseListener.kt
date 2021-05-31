package com.wynnlab.listeners

import com.wynnlab.wynnlab
import org.bukkit.Bukkit
import org.bukkit.event.Listener

abstract class BaseListener : Listener

fun registerListeners() {
    registerListener(CastListener())
    registerListener(CommandListener())
    registerListener(DamageListener())
    registerListener(FallingBlockListener())
    registerListener(GUIListener())
    registerListener(PlayerClickListener())
    registerListener(PlayerEventsListener())
    registerListener(ProjectileHitListener())
    registerListener(ScriptAPIListeners())
}

private fun registerListener(listener: BaseListener) {
    Bukkit.getPluginManager().registerEvents(listener, wynnlab)
}