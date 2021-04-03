package com.wynnlab.gui

import org.bukkit.Bukkit
import org.bukkit.entity.Player

abstract class GUI(
    val player: Player,
    title: String,
    rows: Int
) {
    val inventory = Bukkit.createInventory(player, rows * 9, title)

    abstract fun addItems()

    fun show() {
        addItems()
        player.openInventory(inventory)
    }
}