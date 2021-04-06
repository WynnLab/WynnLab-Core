package com.wynnlab.gui

import com.wynnlab.api.*
import com.wynnlab.classes
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

class CompassGUI(player: Player) : GUI(player, "§c200 §4skill points remaining" /*TODO: skill points*/, 3) {
    override fun initialize() {
        // Reset skill points
        inventory.setItem(4, ItemStack(Material.GOLDEN_SHOVEL).setAppearance(21).meta {
            addItemFlags(*ItemFlag.values())
            setDisplayName("§eReset Skill Points")
            lore = listOf("§7Cost: §f0 Soul Points")
        })

        // Skill points
        inventory.setItem( 11, ItemStack(Material.ENCHANTED_BOOK).meta {
            setDisplayName("§dUpgrade your §2✤ Strength§d skill")
            lore = listOf(" ",
                "       §7§lNow              §5§lNext",
                "       §a0.0%     §a>§2>§a>§2>    §e1.0%",
                "     §70 points            §51 points",
                " ",
                "§7Each point in this skill",
                "§7will§d increase §7any damage",
                "§7you deal and increase the §2✤ Earth",
                "§7damage you may inflict.",
                " ",
                "§aThis skill was modified by your equipment"
            )
        })
        inventory.setItem(12, ItemStack(Material.BOOK).meta { setDisplayName("§dUpgrade your §e✦ Dexterity§d skill") })
        inventory.setItem(13, ItemStack(Material.BOOK).meta { setDisplayName("§dUpgrade your §b❉ Intelligence§d skill") })
        inventory.setItem(14, ItemStack(Material.BOOK).meta { setDisplayName("§dUpgrade your §c✹ Defence§d skill") })
        inventory.setItem(15, ItemStack(Material.BOOK).meta { setDisplayName("§dUpgrade your §f❋ Agility§d skill") })

        // Daily rewards
        inventory.setItem(22, ItemStack(Material.CHEST).meta { setDisplayName("§eDaily rewards") })

        // Tomes
        inventory.setItem(0, ItemStack(Material.ENCHANTED_BOOK).meta {
            setDisplayName("§5§lMastery Tomes")
            lore = listOf(
                "§7Tomes are special raid rewards",
                "§7which can buff your character"
            )
        })
        // Guilds
        inventory.setItem(9, ItemStack(Material.MAGENTA_BANNER).meta {
            setDisplayName("§b§lView Your Guild")
            lore = listOf(
                "§3Name §b[TAG]",
                " ",
                "§7Rank: §fRecruit",
                "§7Joined: ${Date()}",
                " ",
                "§8Open guild management"
            )
        })
        // Settings
        inventory.setItem(18, ItemStack(Material.CRAFTING_TABLE).meta {
            setDisplayName("§f§lSettings")
            lore = listOf("§7WynnLab settings")
        })

        // IDs
        inventory.setItem(8, ItemStack(Material.PLAYER_HEAD).meta {
            setDisplayName("§e§l§o${player.name}'s Info")
            lore = listOf(
                "§7Rank: §fNone",
                " ",
                "§7Combat Lv: §f106",
                "§7Class: §f${player.getWynnClass()?.capitalize() ?: "None"}",
                "§7Quests: §f0/0",
                " ",
                "§dID Bonuses:",
                // "§d- §7$name: §f$value
            )
        })
        // Defence
        inventory.setItem(17, ItemStack(Material.IRON_CHESTPLATE).meta {
            addItemFlags(*ItemFlag.values())
            setDisplayName("§f§lDefence Info")
            lore = listOf(" ",
                "§bBasic Defence:",
                "§b- §4❤ Health: §f${player.health}/${player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value}",
                // Ranked
            )
        })
        // Damage
        val invertedControls = player.invertedControls
        val clazz = classes[player.getWynnClass()]
        val clone = player.isCloneClass
        inventory.setItem(26, ItemStack(Material.IRON_SWORD).meta {
            addItemFlags(*ItemFlag.values())
            setDisplayName("§f§lDamage Info")
            lore = listOf(
                "§7[${player.inventory.itemInMainHand.itemMeta.displayName /*TODO: Find weapon in inv*/}§7]",
                " ",
                "§3[${if (invertedControls) 'R' else 'L'}] §bMain Attack:",
                // Damages
                "§b- Total Damage (+Bonus): §f§k0000§r§f-§f§k0000",
                " ",
                "§5[${if (invertedControls) "LRL" else "RLR"}] §d${clazz?.spells?.get(1)?.let { if (clone) it.cloneSpellName else it.spellName}} Spell:" +
                        "§f§k0000§r§f-§f§k0000",
                "§5[${if (invertedControls) "LLL" else "RRR"}] §d${clazz?.spells?.get(2)?.let { if (clone) it.cloneSpellName else it.spellName}} Spell:" +
                        "§f§k0000§r§f-§f§k0000",
                "§5[${if (invertedControls) "LRR" else "RLL"}] §d${clazz?.spells?.get(3)?.let { if (clone) it.cloneSpellName else it.spellName}} Spell:" +
                        "§f§k0000§r§f-§f§k0000",
                "§5[${if (invertedControls) "LLR" else "RRL"}] §d${clazz?.spells?.get(4)?.let { if (clone) it.cloneSpellName else it.spellName}} Spell:" +
                        "§f§k0000§r§f-§f§k0000",
            )
        })

        registerListener { e ->
            e.isCancelled = true
        }
    }
}