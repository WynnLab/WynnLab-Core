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
import java.util.*

open class CompassGUI(player: Player, private val skills: IntArray) : GUI(player, player.getLocalizedText("gui.compass.title", 200 - skills.sum()), 3) {
    constructor(player: Player) : this(player, player.getSkills())

    val spLeft = 200 - skills.sum()

    init {
        registerListener { e ->
            e.isCancelled = true
            val skillIncrease = if (e.isRightClick) 5 else if (e.isLeftClick) 1 else 0
            when (e.slot) {
                4 -> { player.data.setIntArray("skill_points", intArrayOf(0, 0, 0, 0, 0)); reopen(-1) }

                11 -> { player.data.setIntArray("skill_points", skills.apply { set(0, get(0) + skillIncrease) }); reopen(0) }
                12 -> { player.data.setIntArray("skill_points", skills.apply { set(1, get(1) + skillIncrease) }); reopen(1) }
                13 -> { player.data.setIntArray("skill_points", skills.apply { set(2, get(2) + skillIncrease) }); reopen(2) }
                14 -> { player.data.setIntArray("skill_points", skills.apply { set(3, get(3) + skillIncrease) }); reopen(3) }
                15 -> { player.data.setIntArray("skill_points", skills.apply { set(4, get(4) + skillIncrease) }); reopen(4) }

                18 -> WynnLabSettings().show()
            }
        }
    }

    private fun reopen(slot: Int) {
        (if (slot == -1) ReopenedCompassGUI(null, null, null, null, null, null, null)
        else ReopenedCompassGUI(
            currentDefenseStats,//.takeUnless { slot == 3 },
            currentDamageStats,//.takeUnless { slot == 0 || slot == 1 },
            currentStrengthBook.takeUnless { slot == 0 },
            currentDexterityBook.takeUnless { slot == 1 },
            currentIntelligenceBook.takeUnless { slot == 2 },
            currentDefenseBook.takeUnless { slot == 3 },
            currentAgilityBook.takeUnless { slot == 4 },
        )).show()
    }

    private inner class ReopenedCompassGUI(
        val defenseStats: ItemStack?,
        val damageStats: ItemStack?,
        val strength: ItemStack?,
        val dexterity: ItemStack?,
        val intelligence: ItemStack?,
        val defense: ItemStack?,
        val agility: ItemStack?
    ) : CompassGUI(player, player.getSkills()) {
        override fun update() {
            inventory.setItem(4, resetSkillPoints)

            inventory.setItem(11, strength ?: strengthBook())
            inventory.setItem(12, dexterity ?: dexterityBook())
            inventory.setItem(13, intelligence ?: intelligenceBook())
            inventory.setItem(14, defense ?: defenseBook())
            inventory.setItem(15, agility ?: agilityBook())

            inventory.setItem(22, jukebox)

            inventory.setItem(0, tomes)
            inventory.setItem(9, guildBanner)
            inventory.setItem(18, settings)

            inventory.setItem(8, idStats)
            inventory.setItem(17, defenseStats ?: defenseStats())
            inventory.setItem(26, damageStats ?: damageStats())
        }
    }

    override fun update() {
        // Reset skill points
        inventory.setItem(4, resetSkillPoints)

        // Skill points
        inventory.setItem( 11, currentStrengthBook)
        inventory.setItem(12, currentDexterityBook)
        inventory.setItem(13, currentIntelligenceBook)
        inventory.setItem(14, currentDefenseBook)
        inventory.setItem(15, currentAgilityBook)

        // Jukebox
        inventory.setItem(22, jukebox)
        // Tomes
        inventory.setItem(0, tomes)
        // Guilds

        inventory.setItem(9, guildBanner)
        // Settings
        inventory.setItem(18, settings)
        // IDs
        inventory.setItem(8, idStats)
        // Defence
        inventory.setItem(17, currentDefenseStats)
        // Damage
        inventory.setItem(26, currentDamageStats)
    }

    private fun strengthBook() = ItemStack(Material.BOOK).meta {
        setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.strength")))
        val lore = mutableListOf(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_numbers",
            skillPercentage(skills[0]) * 100.0, skillPercentage(skills[0] + 1) * 100.0, skills[0], skills[0] + 1).split(NL_REGEX))
        lore.add(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_strength").split(NL_REGEX))
        //lore.add(" ")
        //lore.add(language.getMessage("gui.compass.upgrade_skill_modified"))
        this.lore = lore
    }
    private var currentStrengthBook = strengthBook()

    private fun dexterityBook() = ItemStack(Material.BOOK).meta {
        setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.dexterity")))
        val lore = mutableListOf(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_numbers",
            skillPercentage(skills[1]) * 100.0, skillPercentage(skills[1] + 1) * 100.0, skills[1], skills[1] + 1).split(NL_REGEX))
        lore.add(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_dexterity").split(NL_REGEX))
        //lore.add(" ")
        //lore.add(language.getMessage("gui.compass.upgrade_skill_modified"))
        this.lore = lore
    }
    private var currentDexterityBook = dexterityBook()

    private fun intelligenceBook() = ItemStack(Material.BOOK).meta {
        setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.intelligence")))
        val lore = mutableListOf(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_numbers",
            skillPercentage(skills[2]) * 100.0, skillPercentage(skills[2] + 1) * 100.0, skills[2], skills[2] + 1).split(NL_REGEX))
        lore.add(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_intelligence").split(NL_REGEX))
        //lore.add(" ")
        //lore.add(language.getMessage("gui.compass.upgrade_skill_modified"))
        this.lore = lore
    }
    private var currentIntelligenceBook = intelligenceBook()

    private fun defenseBook() = ItemStack(Material.BOOK).meta {
        setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.defense")))
        val lore = mutableListOf(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_numbers",
            skillPercentage(skills[3]) * 100.0, skillPercentage(skills[3] + 1) * 100.0, skills[3], skills[3] + 1).split(NL_REGEX))
        lore.add(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_defense").split(NL_REGEX))
        //lore.add(" ")
        //lore.add(language.getMessage("gui.compass.upgrade_skill_modified"))
        this.lore = lore
    }
    private var currentDefenseBook = defenseBook()

    private fun agilityBook() = ItemStack(Material.BOOK).meta {
        setDisplayName(language.getMessage("gui.compass.upgrade_skill", language.getMessage("elements.agility")))
        val lore = mutableListOf(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_numbers",
            skillPercentage(skills[4]) * 100.0, skillPercentage(skills[4] + 1) * 100.0, skills[4], skills[4] + 1).split(NL_REGEX))
        lore.add(" ")
        lore.addAll(language.getMessage("gui.compass.upgrade_skill_agility").split(NL_REGEX))
        //lore.add(" ")
        //lore.add(language.getMessage("gui.compass.upgrade_skill_modified"))
        this.lore = lore
    }
    private var currentAgilityBook = agilityBook()

    private val resetSkillPoints = ItemStack(Material.GOLDEN_SHOVEL).setAppearance(21).meta {
        addItemFlags(*ItemFlag.values())
        setDisplayName(language.getMessage("gui.compass.reset_skills"))
        lore = listOf(language.getMessage("gui.compass.reset_skills_cost"))
    }

    private val jukebox = ItemStack(Material.JUKEBOX).meta { setDisplayName(language.getMessage("gui.compass.jukebox")) }

    private val tomes = ItemStack(Material.ENCHANTED_BOOK).meta {
        setDisplayName(language.getMessage("gui.compass.tomes"))
        //lore = language.getMessage("gui.compass.").split(NL_REGEX)
    }

    private val guildBanner: ItemStack
    init {
        val guildName = player.data.getString("guild")
        val guildLore: List<String>
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
        guildBanner.meta {
            setDisplayName(language.getMessage("gui.compass.guild_view"))
            lore = guildLore
        }
    }

    private val settings = ItemStack(Material.CRAFTING_TABLE).meta {
        setDisplayName(language.getMessage("gui.compass.settings"))
        lore = listOf(language.getMessage("gui.compass.settings_lore"))
    }

    private val idStats = ItemStack(Material.PLAYER_HEAD).meta {
        setDisplayName(language.getMessage("gui.compass.player_info", player.name))
        val lore = mutableListOf(
            language.getMessage(
                "gui.compass.player_rank",
                player.data.getString("rank")?.toLowerCase()?.capitalize() ?: "Player"
            ),
            " ",
            language.getMessage("gui.compass.player_level", 106),
            language.getMessage("gui.compass.player_class", classes[player.getWynnClass()]?.let {
                if (player.isCloneClass) player.getLocalizedText("classes.${it.id}.cloneName")
                else player.getLocalizedText("classes.${it.id}.className")
            } ?: "None"),
            language.getMessage("gui.compass.player_quests", 0, 0),
            " ",
            language.getMessage("gui.compass.player_ids"),
        )
        addIdValues(lore)
        this.lore = lore
    }

    private fun defenseStats() = ItemStack(Material.IRON_CHESTPLATE).meta {
        addItemFlags(*ItemFlag.values())
        setDisplayName(language.getMessage("gui.compass.defense"))
        lore = listOf(" ",
            language.getMessage("gui.compass.defense_basic"),
            language.getMessage("gui.compass.defense_health", player.health.toInt(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value.toInt()),
            // Ranked
        )
    }
    private var currentDefenseStats = defenseStats()

    private val invertedControls = player.invertedControls
    private val clazz = classes[player.getWynnClass()]
    private val clone = player.isCloneClass

    private fun damageStats(): ItemStack = ItemStack(Material.IRON_SWORD).meta {
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
    }
    private var currentDamageStats = damageStats()

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