package com.wynnlab.spells

import com.wynnlab.api.isCloneClass
import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player

abstract class Spell(
    protected val player: Player,
    private val maxTick: Int,
    val data: SpellData
) : Runnable {
    private var taskId = -1
    private var scheduled = false

    protected var tick = 0
    protected val clone = player.isCloneClass

    abstract fun tick()

    final override fun run() {
        if (scheduled) {
            if (tick < maxTick) {
                tick()
                ++tick
            } else {
                Bukkit.getScheduler().cancelTask(taskId)
            }
        }
    }

    fun schedule() {
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 1L).taskId
        scheduled = true
    }
}