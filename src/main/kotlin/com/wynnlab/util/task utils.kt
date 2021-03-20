package com.wynnlab.util

import org.bukkit.scheduler.BukkitRunnable

inline fun bukkitRunnable(crossinline task: (BukkitRunnable) -> Unit) = object : BukkitRunnable() {
    override fun run() {
        task(this)
    }
}