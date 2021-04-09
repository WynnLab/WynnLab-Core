package com.wynnlab.gui

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

    abstract fun update()

    fun show() {
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