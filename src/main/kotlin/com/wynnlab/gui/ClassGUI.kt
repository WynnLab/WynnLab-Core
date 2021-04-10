package com.wynnlab.gui

import com.wynnlab.NL_REGEX
import com.wynnlab.api.getLocalizedText
import com.wynnlab.api.sendWynnMessage
import com.wynnlab.api.setWynnClass
import com.wynnlab.classes
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class ClassGUI(player: Player) : GUI(player, player.getLocalizedText("gui.class.title"), 1) {
    private val classCount = classes.size
    private val itemPositions = (5 - (classCount + .5f) / 2f).toInt()..(4 + classCount / 2)

    init {
        registerListener { e ->
            e.isCancelled = true

            val classIndex = itemPositions.indexOf(e.slot)
            if (classIndex == -1) return@registerListener
            val clazz = classes.values.toList()[classIndex]

            if (when (e.click) {
                    ClickType.LEFT, ClickType.SHIFT_LEFT -> false
                    ClickType.RIGHT, ClickType.SHIFT_RIGHT -> true
                    else -> return@registerListener
            }) {
                player.sendWynnMessage("gui.class.select", player.getLocalizedText("classes.${clazz.id}.className"))
                player.addScoreboardTag("clone")
            } else {
                player.sendWynnMessage("gui.class.select", player.getLocalizedText("classes.${clazz.id}.cloneName"))
                player.removeScoreboardTag("clone")
            }

            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

            player.setWynnClass(clazz.id)

            player.closeInventory()
        }
    }

    override fun update() {
        val iterator = itemPositions.iterator()

        for (clazz in classes.values) {
            val item = ItemStack(clazz.item)
            val meta = item.itemMeta

            meta.setDisplayName(player.getLocalizedText("gui.class.item.title", player.getLocalizedText("classes.${clazz.id}.className")))
            val lore = mutableListOf(" ")

            val (damage, defence, range, spells) = clazz.metaStats
            lore.add(player.getLocalizedText("gui.class.item.damage", damage.squares()))
            lore.add(player.getLocalizedText("gui.class.item.defence", defence.squares()))
            lore.add(player.getLocalizedText("gui.class.item.range", range.squares()))
            lore.add(player.getLocalizedText("gui.class.item.spells", spells.squares()))

            lore.add(" ")
            lore.addAll(player.getLocalizedText("classes.${clazz.id}.lore").split(NL_REGEX))

            lore.add(player.getLocalizedText("gui.class.item.clone", player.getLocalizedText("classes.${clazz.id}.cloneName")))

            meta.lore = lore
            item.itemMeta = meta

            inventory.setItem(iterator.nextInt(), item)
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