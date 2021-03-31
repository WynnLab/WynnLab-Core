package com.wynnlab.spells

import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player

abstract class PySpell : Runnable {
    lateinit var player: Player
    var t = 0

    private var taskId = -1
    private var scheduled = false
    var maxTick = 0

    open fun init() {}

    abstract fun tick()

    fun delay() { --t }

    fun cancel() {
        Bukkit.getScheduler().cancelTask(taskId)
    }

    final override fun run() {
        if (scheduled) {
            if (t <= maxTick) {
                tick()
                ++t
            } else {
                cancel()
            }
        }
    }

    fun schedule() {
        init()
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 1L).taskId
        scheduled = true
    }
}