package com.wynnlab.gui

import com.wynnlab.api.*
import com.wynnlab.classes
import com.wynnlab.guilds.Guild
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CompassGUI(player: Player) : GUI(player, "§c200 §4skill points remaining" /*TODO: skill points*/, 3) {
    init {
        registerListener { e ->
            e.isCancelled = true
            when (e.slot) {
                18 -> WynnLabSettings().show()
            }
        }
    }

    override fun update() {
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
                "       §7§lNow                 §6§lNext",
                "       §a0.0%     §a>§2>§a>§2>    §e1.0%",
                "     §70 points               §61 points",
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
        val guildName = player.data.getString("guild")
        val guildLore: List<String>
        val guildBanner: ItemStack
        if (guildName == null) {
            guildLore = listOf("§cYou are not in a Guild yet!")
            guildBanner = ItemStack(Material.WHITE_BANNER)
        } else {
            val guild = Guild(guildName)
            val guildMember = guild.members[player.name]!!
            guildLore = listOf(
                "§3${guild.name} §b[${guild.tag}]",
                " ",
                "§7Rank: §f${guildMember.rank.friendlyName}",
                "§7Joined: ${guildMember.joined}",
                " ",
                "§8Open guild management"
            )
            guildBanner = guild.banner.clone()
        }
        inventory.setItem(9, guildBanner.meta {
            setDisplayName("§b§lView Your Guild")
            lore = guildLore
        })
        // Settings
        inventory.setItem(18, ItemStack(Material.CRAFTING_TABLE).meta {
            setDisplayName("§f§lSettings")
            lore = listOf("§7WynnLab settings")
        })


        // IDs
        inventory.setItem(8, ItemStack(Material.PLAYER_HEAD).meta {
            setDisplayName("§e§l§o${player.name}'s Info")
            val lore = mutableListOf(
                "§7Rank: §f${player.data.getString("rank")?.toLowerCase()?.capitalize() ?: "Player"}",
                " ",
                "§7Combat Lv: §f106",
                "§7Class: §f${classes[player.getWynnClass()]?.let { if (player.isCloneClass) it.cloneName else it.className } ?: "None"}",
                "§7Quests: §f0/0",
                " ",
                "§dID Bonuses:",
            )
            addIdValues(lore)
            this.lore = lore
        })
        // Defence
        inventory.setItem(17, ItemStack(Material.IRON_CHESTPLATE).meta {
            addItemFlags(*ItemFlag.values())
            setDisplayName("§f§lDefence Info")
            lore = listOf(" ",
                "§bBasic Defence:",
                "§b- §4❤ Health: §f${player.health.toInt()}/${player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value.toInt()}",
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
                "§7[${player.getFirstWeaponSlot().let { if (it == -1) null else player.inventory.getItem(it) }?.itemMeta?.displayName}§7]",
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
    }

    inner class WynnLabSettings : GUI(player, "WynnLab Settings", 3) {
        init {
            registerListener { e ->
                e.isCancelled = true
                when (e.slot) {
                    4 -> {
                        setParticles(2)
                        setOtherParticles(2)
                    }
                    10 -> setParticles((player.data.getInt("particles") ?: 2) + if (e.isLeftClick) 1 else if (e.isRightClick) -1 else 0)
                    11 -> setOtherParticles((player.data.getInt("other_particles") ?: 2) + if (e.isLeftClick) 1 else if (e.isRightClick) -1 else 0)
                }
            }
        }

        override fun update() {
            inventory.setItem(4, ItemStack(Material.GOLDEN_SHOVEL).setAppearance(21).meta {
                addItemFlags(*ItemFlag.values())
                setDisplayName("§eReset Settings")
                lore = listOf("§7Back to default")
            })

            val particles = player.data.getInt("particles") ?: 2
            val otherParticles = player.data.getInt("other_particles") ?: 2

            inventory.setItem(10, ItemStack(Material.PLAYER_HEAD).meta {
                setDisplayName("§fYour Particles: ${particleSettingString(particles)}")
                lore = particlesLore
            })
            inventory.setItem(11, ItemStack(Material.PLAYER_HEAD).meta {
                setDisplayName("§fPlayer Particles: ${particleSettingString(otherParticles)}")
                lore = particlesLore
            })
        }

        private fun setParticles(amount: Int) {
            player.data.setInt("particles", amount.coerceIn(0, 2))
        }

        private fun setOtherParticles(amount: Int) {
            player.data.setInt("other_particles", amount.coerceIn(0, 2))
        }
    }

    companion object {

        ///////////////////////////////////////////////////////////////////////////
        // WynnLabSettings
        ///////////////////////////////////////////////////////////////////////////

        fun particleSettingString(int: Int) = when (int) {
            0 -> "§cOFF"
            1 -> "§eMEDIUM"
            2 -> "§aHIGH"
            else -> "§d§kBROKEN"
        }

        val particlesLore = listOf(
            "§fLeft-Click §7to increase",
            "§fRight-Click §7to decrease"
        )
    }

    private fun addIdValues(list: MutableList<String>) {
        val weapon = player.getFirstWeaponSlot().let { if (it == -1) null else player.inventory.getItem(it) }?.itemMeta?.data
        list.addIdValue(weapon, "mana_regen", "Mana Regen", "/4s")
        list.addIdValue(weapon, "mana_steal", "Mana Steal", "/4s")
        list.addIdValue(weapon, "health_bonus", "Health", "")
        list.addIdValue(weapon, "health_regen", "Health Regen")
        list.addIdValue(weapon, "health_regen_raw", "Health Regen", "")
        list.addIdValue(weapon, "life_steal", "Life Steal", "/4s")
        list.addIdValue(weapon, "speed", "Walk Speed")
        list.addIdValue(weapon, "emerald_stealing", "Stealing")
        list.addIdValue(weapon, "attack_speed_bonus", "Attack Speed", " tier")
        list.addIdValue(weapon, "poison", "Poison", "/3s")
        list.addIdValue(weapon, "reflection", "Reflection")
        list.addIdValue(weapon, "thorns", "Thorns")
        list.addIdValue(weapon, "exploding", "Exploding")
        list.addIdValue(weapon, "spell_cost_pct_1", "1st Spell Cost")
        list.addIdValue(weapon, "spell_cost_pct_2", "2nd Spell Cost")
        list.addIdValue(weapon, "spell_cost_pct_3", "3rd Spell Cost")
        list.addIdValue(weapon, "spell_cost_pct_4", "4th Spell Cost")
        list.addIdValue(weapon, "spell_cost_raw_1", "1st Spell Cost", "")
        list.addIdValue(weapon, "spell_cost_raw_2", "2nd Spell Cost", "")
        list.addIdValue(weapon, "spell_cost_raw_3", "3rd Spell Cost", "")
        list.addIdValue(weapon, "spell_cost_raw_4", "4th Spell Cost", "")
        list.addIdValue(weapon, "rainbow_spell_damage_raw", "Rainbow Spell Damage", "")
        list.addIdValue(weapon, "jump_height", "Jump Height", "")
    }

    private fun MutableList<String>.addIdValue(weapon: PersistentDataContainer?, name: String, displayName: String, suffix: String = "%") {
        val value = player.getId(name) + (weapon?.getContainer("ids")?.getInt(name) ?: 0)
        if (value != 0)
            add("§d- §7$displayName: §f${if (value > 0) "+" else ""}$value$suffix")
    }
}