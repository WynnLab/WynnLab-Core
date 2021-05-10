package com.wynnlab.util

import com.wynnlab.plugin
import org.bukkit.Bukkit

abstract class TickRunnable : Runnable {
    var t = 0

    private var _taskId = -1
    val taskId get() = _taskId

    private var scheduled = false
    var maxTick = 0

    open fun init() {}

    abstract fun tick()

    fun delay() {
        --t
    }

    fun cancel() {
        Bukkit.getScheduler().cancelTask(_taskId)
    }

    final override fun run() {
        if (scheduled) {
            if (t <= maxTick) {
                try {
                    tick()
                } catch (e: Throwable) {
                    cancel()
                    throw e
                }
                ++t
            } else {
                cancel()
            }
        }
    }

    fun schedule() {
        init()
        _taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 1L).taskId
        scheduled = true
    }
}