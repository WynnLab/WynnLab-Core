package com.wynnlab.gui

import com.wynnlab.api.meta
import com.wynnlab.listeners.GUIListener
import com.wynnlab.localization.Language
import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class GUI(
    val player: Player,
    val title: String,
    private val rows: Int
) {
    val inventory = Bukkit.createInventory(player, rows * 9, title)

    protected val language = Language[player.locale.toLowerCase()]

    val decorator get() = com.wynnlab.gui.decorator

    fun decorate() {
        var i = 0
        while (i < rows * 9) {
            if (i / 9 == 0 || i / 9 == rows - 1)
                inventory.setItem(i, decorator)
            else if (i % 9 == 0 || i % 9 == 8)
                inventory.setItem(i, decorator)
            ++i
        }
    }

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
        GUIListener.inventories[title] = {
            action(it)
            update()
        }
    }
}

private val decorator = ItemStack(Material.BLACK_STAINED_GLASS_PANE).meta {
    setDisplayName("§0")
}