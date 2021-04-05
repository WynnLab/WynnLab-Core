package com.wynnlab.gui

import com.wynnlab.api.meta
import com.wynnlab.api.setAppearance
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CompassGUI(player: Player) : GUI(player, "§c200 §4skill points remaining" /*TODO: skill points*/, 3) {
    override fun initialize() {
        // Reset skill points
        inventory.setItem(4, ItemStack(Material.GOLDEN_SHOVEL).setAppearance(1).meta { setDisplayName("§eReset skill points") })

        // Skill points
        inventory.setItem( 11, ItemStack(Material.BOOK).meta { setDisplayName("§dUpgrade your §2✤ Strength§d skill") })
        inventory.setItem(12, ItemStack(Material.BOOK).meta { setDisplayName("§dUpgrade your §e✦ Dexterity§d skill") })
        inventory.setItem(13, ItemStack(Material.BOOK).meta { setDisplayName("§dUpgrade your §b❉ Intelligence§d skill") })
        inventory.setItem(14, ItemStack(Material.BOOK).meta { setDisplayName("§dUpgrade your §c✹ Defence§d skill") })
        inventory.setItem(15, ItemStack(Material.BOOK).meta { setDisplayName("§dUpgrade your §f❋ Agility§d skill") })

        // Daily rewards
        inventory.setItem(22, ItemStack(Material.CHEST).meta { setDisplayName("§eDaily rewards") })

        // Tomes
        inventory.setItem(0, ItemStack(Material.ENCHANTED_BOOK).meta { setDisplayName("§5§lMastery Tomes") })
        // Guilds
        inventory.setItem(9, ItemStack(Material.MAGENTA_BANNER).meta { setDisplayName("§b§lView Your Guild") })
        // Settings
        inventory.setItem(18, ItemStack(Material.CRAFTING_TABLE).meta { setDisplayName("§f§lSettings") })

        // IDs
        inventory.setItem(8, ItemStack(Material.PLAYER_HEAD).meta { setDisplayName("§e§l§o${player.name}'s Info") })
        // Defence
        inventory.setItem(17, ItemStack(Material.IRON_CHESTPLATE).meta { setDisplayName("§f§lDefence Info") })
        // Damage
        inventory.setItem(26, ItemStack(Material.IRON_SWORD).meta { setDisplayName("§f§lDamage Info") })

        registerListener { e ->
            e.isCancelled = true
        }
    }
}