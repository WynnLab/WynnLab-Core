package com.wynnlab.util

import com.wynnlab.api.getInt
import com.wynnlab.api.remove
import com.wynnlab.api.setInt
import com.wynnlab.wynnlab
import org.bukkit.Bukkit
import org.bukkit.persistence.PersistentDataContainer

/**
 * A delayed runnable which delay can be refreshed to delay it further
 */
abstract class RefreshRunnable(private val data: PersistentDataContainer, private val id: String) : Runnable {
    final override fun run() {
        val delay = data.getInt("RR_$id")!!
        if (delay > 0)
            data.setInt("RR_$id", delay - 1)
        if (delay <= 1) {
            task()
            data.remove("RR_$id")
        }
    }

    abstract fun task()

    fun schedule(delay: Long) {
        Bukkit.getScheduler().runTaskLater(wynnlab, this, delay)
        data.setInt("RR_$id", (data.getInt("RR_$id") ?: 0) + 1)
    }

    companion object {
        inline operator fun invoke(data: PersistentDataContainer, id: String, crossinline task: () -> Unit) =
            object : RefreshRunnable(data, id) {
                override fun task() {
                    task()
                }
            }
    }
}