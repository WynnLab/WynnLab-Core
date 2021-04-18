package com.wynnlab.gui

import com.wynnlab.localization.Language
import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

abstract class GUI(
    val player: Player,
    val title: String,
    rows: Int
) {
    val inventory = Bukkit.createInventory(player, rows * 9, title)

    protected val language = Language[player.locale.toLowerCase()]

    abstract fun update()

    fun show() {
        Bukkit.getScheduler().runTaskAsynchronously(
            plugin, Runnable {
                update()
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                    player.openInventory(inventory)
                }
            })
    }

    fun showSync() {
        update()
        player.openInventory(inventory)
    }

    inline fun registerListener(crossinline action: (InventoryClickEvent) -> Unit) {
        plugin.guiListener.inventories[title] = {
            action(it)
            update()
        }
    }
}