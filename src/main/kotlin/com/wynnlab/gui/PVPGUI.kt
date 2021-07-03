package com.wynnlab.gui

import com.wynnlab.api.getLocalizedString
import com.wynnlab.api.getLocalizedText
import com.wynnlab.api.getLocalizedTextMultiline
import com.wynnlab.api.meta
import com.wynnlab.java.AddItemFlags
import com.wynnlab.pvp.Duels
import com.wynnlab.pvp.FFA
import com.wynnlab.util.emptyComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class PVPGUI(player: Player) : GUI(player, player.getLocalizedText("gui.pvp.title"), 3) {
    init {
        registerListener { e ->
            e.isCancelled = true
            when (val slot = e.slot) {
                10 -> FFA.join(player)
                else -> {
                    val index = slot - 11
                    val currentDuels = Duels.duels.size
                    if (index == currentDuels) {
                        // Start duel
                        Duels.startDuel(player, 0)
                        e.whoClicked.closeInventory()
                    } else if (index < currentDuels) {
                        // Join duel
                        val duel = Duels.duels[index]
                        duel.join(e.whoClicked as Player)
                    }
                }
            }
        }
    }

    override fun update() {
        decorate()

        inventory.setItem(10, ItemStack(if (FFA.players.size < 100) Material.GREEN_CONCRETE else Material.RED_CONCRETE).meta {
            //(this as Damageable).damage = 20
            //isUnbreakable = true
            //addItemFlags(*ItemFlag.values())

            displayName(player.getLocalizedText("gui.pvp.ffa.title"))
            val l = mutableListOf(emptyComponent)
            l.addAll(player.getLocalizedTextMultiline("gui.pvp.ffa.lore"))
            l.add(emptyComponent)
            l.add(player.getLocalizedText("gui.pvp.ffa.online", FFA.players.size, 100))
            lore(l)
        })

        var i = 10
        for (duel in Duels.duels) {
            ++i

            val full = duel.player1 != null && duel.player2 != null
            inventory.setItem(i, ItemStack(Material.GOLDEN_SHOVEL).meta {
                (this as Damageable).damage = if (full) 18 else 20
                isUnbreakable = true
                AddItemFlags.addAllItemFlags(this)

                displayName(player.getLocalizedText("gui.pvp.duel.title"))
                lore(listOf(
                    player.getLocalizedText("gui.pvp.duel.map", player.getLocalizedString("gui.pvp.duel.maps.${Duels.maps[duel.map]}")),
                    emptyComponent,
                    player.getLocalizedText("gui.pvp.duel.player1", duel.player1?.name ?: "-"),
                    player.getLocalizedText("gui.pvp.duel.player2", duel.player2?.name ?: "-"),
                    emptyComponent,
                    player.getLocalizedText(if (full) "gui.pvp.duel.spectate" else "gui.pvp.duel.join")
                ))
            })
        }

        ++i
        inventory.setItem(i, ItemStack(Material.GOLDEN_SHOVEL).meta {
            (this as Damageable).damage = 22
            isUnbreakable = true
            AddItemFlags.addAllItemFlags(this)

            displayName(player.getLocalizedText("gui.pvp.duel.create.title"))
        })
    }
}