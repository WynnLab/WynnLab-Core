package com.wynnlab.gui

import com.wynnlab.NL_REGEX
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

class CompassGUI(player: Player, private val skills: IntArray) : GUI(player, player.getLocalizedText("gui.compass.title", 200 - skills.sum()), 3) {

    constructor(player: Player) : this(player, player.getSkills())

    init {
        registerListener { e ->
            e.isCancelled = true
            when (e.slot) {
                4 -> { player.data.setIntArray("skill_points", intArrayOf(0, 0, 0, 0, 0)); reopen() }

                11 -> { player.data.setIntArray("skill_points", skills.apply { set(0, get(0) + 1) }); reopen() }

                18 -> WynnLabSettings().show()
            }
        }
    }

    private fun reopen() { // TODO: efficiency
        CompassGUI(player).show()
    }

    override fun update() {
        // Reset skill points
        inventory.setItem(4, ItemStack(Material.GOLDEN_SHOVEL).setAppearance(21).meta {
            addItemFlags(*ItemFlag.values())
            setDisplayName(language.getMessage("gui.compass.reset_skills"))
            lore = listOf(language.getMessage("gui.compass.reset_skills_cost"))
        })

        // Skill points
        inventory.setItem( 11, ItemStack(Material.BOOK).meta {
            setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.strength")))
            val lore = mutableListOf(" ")
            lore.addAll(language.getMessage("gui.compass.upgrade_skill_numbers",
                skillPercentage(skills[0]) * 100.0, skillPercentage(skills[0] + 1) * 100.0, skills[0], skills[0] + 1).split(NL_REGEX))
            lore.add(" ")
            lore.addAll(language.getMessage("gui.compass.upgrade_skill_strength").split(NL_REGEX))
            //lore.add(" ")
            //lore.add(language.getMessage("gui.compass.upgrade_skill_modified"))
            this.lore = lore
        })
        inventory.setItem(12, ItemStack(Material.BOOK).meta { setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.dexterity"))) })
        inventory.setItem(13, ItemStack(Material.BOOK).meta { setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.intelligence"))) })
        inventory.setItem(14, ItemStack(Material.BOOK).meta { setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.defense"))) })
        inventory.setItem(15, ItemStack(Material.BOOK).meta { setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.agility"))) })

        // Daily rewards
        inventory.setItem(22, ItemStack(Material.CHEST).meta { setDisplayName(language.getMessage("gui.compass.daily_rewards")) })

        // Tomes
        inventory.setItem(0, ItemStack(Material.ENCHANTED_BOOK).meta {
            setDisplayName(language.getMessage("gui.compass.daily_rewards"))
            //lore = language.getMessage("gui.compass.").split(NL_REGEX)
        })
        // Guilds
        val guildName = player.data.getString("guild")
        val guildLore: List<String>
        val guildBanner: ItemStack
        if (guildName == null) {
            guildLore = listOf(language.getMessage("gui.compass.no_guild"))
            guildBanner = ItemStack(Material.WHITE_BANNER)
        } else {
            val guild = Guild(guildName)
            val guildMember = guild.members[player.name]!!
            guildLore = listOf(
                "§3${guild.name} §b[${guild.tag}]",
                " ",
                language.getMessage("gui.compass.guild_rank", guildMember.rank.friendlyName),
                language.getMessage("gui.compass.guild_joined", guildMember.joined),
                " ",
                language.getMessage("gui.compass.guild_open")
            )
            guildBanner = guild.banner.clone()
        }
        inventory.setItem(9, guildBanner.meta {
            setDisplayName(language.getMessage("gui.compass.guild_view"))
            lore = guildLore
        })
        // Settings
        inventory.setItem(18, ItemStack(Material.CRAFTING_TABLE).meta {
            setDisplayName(language.getMessage("gui.compass.settings"))
            lore = listOf(language.getMessage("gui.compass.settings_lore"))
        })


        // IDs
        inventory.setItem(8, ItemStack(Material.PLAYER_HEAD).meta {
            setDisplayName(language.getMessage("gui.compass.player_info", player.name))
            val lore = mutableListOf(
                language.getMessage("gui.compass.player_rank", player.data.getString("rank")?.toLowerCase()?.capitalize() ?: "Player"),
                " ",
                language.getMessage("gui.compass.player_level", 106),
                language.getMessage("gui.compass.player_class", classes[player.getWynnClass()]?.let {
                    if (player.isCloneClass) player.getLocalizedText("classes.${it.id}.cloneName") 
                    else player.getLocalizedText("classes.${it.id}.className") } ?: "None"),
                language.getMessage("gui.compass.player_quests", 0, 0),
                " ",
                language.getMessage("gui.compass.player_ids"),
            )
            addIdValues(lore)
            this.lore = lore
        })
        // Defence
        inventory.setItem(17, ItemStack(Material.IRON_CHESTPLATE).meta {
            addItemFlags(*ItemFlag.values())
            setDisplayName(language.getMessage("gui.compass.defense"))
            lore = listOf(" ",
                language.getMessage("gui.compass.defense_basic"),
                language.getMessage("gui.compass.defense_health", player.health.toInt(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value.toInt()),
                // Ranked
            )
        })
        // Damage
        val invertedControls = player.invertedControls
        val clazz = classes[player.getWynnClass()]
        val clone = player.isCloneClass
        inventory.setItem(26, ItemStack(Material.IRON_SWORD).meta {
            addItemFlags(*ItemFlag.values())
            setDisplayName(language.getMessage("gui.compass.damage"))
            lore = listOf(
                "§7[${player.getFirstWeaponSlot().let { if (it == -1) null else player.inventory.getItem(it) }?.itemMeta?.displayName}§7]",
                " ",
                language.getMessage("gui.compass.main_attack_damage", if (invertedControls) 'R' else 'L'),
                // Damages
                language.getMessage("gui.compass.total_damage", 0, 0),
                " ",
                language.getMessage("gui.compass.spell_damage", if (invertedControls) "LRL" else "RLR", language.getMessage("classes.${player.getWynnClass()}.spells.${if (clone) "1c" else "1"}"), 0, 0),
                language.getMessage("gui.compass.spell_damage", if (invertedControls) "LLL" else "RRR", language.getMessage("classes.${player.getWynnClass()}.spells.${if (clone) "2c" else "2"}"), 0, 0),
                language.getMessage("gui.compass.spell_damage", if (invertedControls) "LRR" else "RLL", language.getMessage("classes.${player.getWynnClass()}.spells.${if (clone) "3c" else "3"}"), 0, 0),
                language.getMessage("gui.compass.spell_damage", if (invertedControls) "LLR" else "RRL", language.getMessage("classes.${player.getWynnClass()}.spells.${if (clone) "4c" else "4"}"), 0, 0),
            )
        })
    }

    inner class WynnLabSettings : GUI(player, (this@CompassGUI).language.getMessage("gui.compass.wl_settings.title"), 3) {
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
                setDisplayName(language.getMessage("gui.compass.wl_settings.reset"))
                lore = listOf(language.getMessage("gui.compass.wl_settings.reset_default"))
            })

            val particles = player.data.getInt("particles") ?: 2
            val otherParticles = player.data.getInt("other_particles") ?: 2

            inventory.setItem(10, ItemStack(Material.PLAYER_HEAD).meta {
                setDisplayName(language.getMessage("gui.compass.wl_settings.your_particles", particleSettingString(particles)))
                lore = particlesLore
            })
            inventory.setItem(11, ItemStack(Material.PLAYER_HEAD).meta {
                setDisplayName(language.getMessage("gui.compass.wl_settings.other_particles", particleSettingString(otherParticles)))
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