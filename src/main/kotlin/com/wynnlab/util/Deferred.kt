package com.wynnlab.util

import com.wynnlab.plugin
import org.bukkit.Bukkit

class Deferred<out T : Any?>(
    val task: () -> T
) {
    inline fun execute(crossinline then: (T) -> Unit) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            then(task())
        })
    }
}