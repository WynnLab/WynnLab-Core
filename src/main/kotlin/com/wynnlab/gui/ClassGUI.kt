package com.wynnlab.gui

import com.wynnlab.api.sendWynnMessage
import com.wynnlab.api.setWynnClass
import com.wynnlab.classes
import com.wynnlab.plugin
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class ClassGUI(player: Player) : GUI(player, "Choose a class" /*TODO: color*/, 1) {
    override fun initialize() {
        val classCount = classes.size
        val itemPositions = if (classCount > 5) 0 until classCount else (5 - classCount)..(4 + classCount) step 2
        val iterator = itemPositions.iterator()

        for (clazz in classes.values) {
            val item = ItemStack(clazz.item)
            val meta = item.itemMeta

            meta.setDisplayName("          §fSelect §l§6${clazz.className}")
            val lore = mutableListOf(" ")

            val (damage, defence, range, spells) = clazz.metaStats
            lore.add("§6⚔ Damage               ${damage.squares()}")
            lore.add("§c❤ Defence              ${defence.squares()}")
            lore.add("§a➸ Range                 ${range.squares()}")
            lore.add("§d✺ Spells                 ${spells.squares()}")

            lore.add(" ")
            lore.addAll(clazz.lore.split(Regex("\\n")).map { "§7$it" })

            lore.add("§8Right click to select §l§b${clazz.cloneName}")

            meta.lore = lore
            item.itemMeta = meta

            inventory.setItem(iterator.nextInt(), item)
        }

        registerListener { e ->
            e.isCancelled = true

            val classIndex = itemPositions.indexOf(e.slot)
            if (classIndex == -1) return@registerListener
            val clazz = classes.values.toList()[classIndex]
            val player = e.whoClicked as Player

            if (when (e.click) {
                ClickType.LEFT, ClickType.SHIFT_LEFT -> false
                ClickType.RIGHT, ClickType.SHIFT_RIGHT -> true
                else -> return@registerListener
            }) {
                player.sendWynnMessage("You are now §3[${clazz.cloneName}]")
                player.addScoreboardTag("clone")
            } else {
                player.sendWynnMessage("You are now §3[${clazz.className}]")
                player.removeScoreboardTag("clone")
            }

            player.setWynnClass(clazz.className.toUpperCase())

            player.closeInventory()
        }
    }

    private fun Int.squares() = when (this) {
        0 -> "§7■■■■■"
        1 -> "§a■§7■■■■"
        2 -> "§a■■§7■■■"
        3 -> "§a■■■§7■■"
        4 -> "§a■■■■§7■"
        else -> "§a■■■■■"
    }
}