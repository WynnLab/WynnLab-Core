package com.wynnlab.gui

import com.wynnlab.NL_REGEX
import com.wynnlab.api.getLocalizedText
import com.wynnlab.api.meta
import com.wynnlab.commands.PVPCommands
import com.wynnlab.pvp.FFA
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class PVPGUI(player: Player) : GUI(player, player.getLocalizedText("gui.pvp.title"), 3) {
    init {
        registerListener { e ->
            e.isCancelled = true
            when (e.slot) {
                10 -> FFA.join(player)
                16 -> PVPCommands.world(player)
            }
        }
    }

    override fun update() {
        decorate()

        inventory.setItem(10, ItemStack(Material.GOLDEN_SHOVEL).meta {
            (this as Damageable).damage = 20
            isUnbreakable = true
            addItemFlags(*ItemFlag.values())

            setDisplayName(player.getLocalizedText("gui.pvp.ffa.title"))
            val l = mutableListOf("")
            player.getLocalizedText("gui.pvp.ffa.lore").split(NL_REGEX).forEach { l.add(it) }
            l.add("")
            l.add(player.getLocalizedText("gui.pvp.ffa.online", FFA.players.size, 100))
            lore = l
        })

        inventory.setItem(16, ItemStack(Material.STONE))
    }
}