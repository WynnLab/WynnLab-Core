package com.wynnlab.gui

import com.wynnlab.WynnClass
import com.wynnlab.api.*
import com.wynnlab.classes
import com.wynnlab.guilds.Guild
import com.wynnlab.util.emptyComponent
import com.wynnlab.wynnlab
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.network.chat.ChatMessage
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow
import net.minecraft.world.inventory.Containers
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer

class CompassGUI(player: Player, private val skills: IntArray) : GUI(player, player.getLocalizedText("gui.compass.title", 200 - skills.sum()), 3) {
    constructor(player: Player) : this(player, player.getSkills())

    private val spLeft = 200 - skills.sum()

    init {
        registerListener { e ->
            e.isCancelled = true
            var skillIncrease = if (e.isRightClick) 5 else if (e.isLeftClick) 1 else 0
            if (spLeft < skillIncrease)
                skillIncrease = 0
            player.playSound(player.location, if (skillIncrease > 0) Sound.ENTITY_EXPERIENCE_ORB_PICKUP else Sound.BLOCK_ANVIL_PLACE, 1f, 1f)
            val skillPointsKey = "skill_points"
            when (e.slot) {
                4 -> { player.data.setIntArray(skillPointsKey, intArrayOf(0, 0, 0, 0, 0)); reopen(-1) }

                11 -> { player.data.setIntArray(skillPointsKey, skills.apply { set(0, get(0) + skillIncrease) }); reopen(0) }
                12 -> { player.data.setIntArray(skillPointsKey, skills.apply { set(1, get(1) + skillIncrease) }); reopen(1) }
                13 -> { player.data.setIntArray(skillPointsKey, skills.apply { set(2, get(2) + skillIncrease) }); reopen(2) }
                14 -> { player.data.setIntArray(skillPointsKey, skills.apply { set(3, get(3) + skillIncrease) }); reopen(3) }
                15 -> { player.data.setIntArray(skillPointsKey, skills.apply { set(4, get(4) + skillIncrease) }); reopen(4) }

                18 -> WynnLabSettings().show()
            }
        }
    }

    private fun reopen(slot: Int) {
        val nmsPlayer = (player as CraftPlayer).handle
        val packet = PacketPlayOutOpenWindow(nmsPlayer.bV.j, Containers.c,
            ChatMessage(LegacyComponentSerializer.legacy('§').serialize(player.getLocalizedText("gui.compass.title", 200 - skills.sum()))))
        nmsPlayer.b.sendPacket(packet)

        Bukkit.getScheduler().runTaskLater(wynnlab, Runnable {
            if (slot == -1 || slot == 0)
                inventory.setItem(11, strengthBook())
            if (slot == -1 || slot == 1)
                inventory.setItem(12, dexterityBook())
            if (slot == -1 || slot == 2)
                inventory.setItem(13, intelligenceBook())
            if (slot == -1 || slot == 3)
                inventory.setItem(14, defenseBook())
            if (slot == -1 || slot == 4)
                inventory.setItem(15, agilityBook())
        }, 1L) //TODO
    }

    override fun update() {
        // Reset skill points
        inventory.setItem(4, resetSkillPoints)

        // Skill points
        inventory.setItem(11, currentStrengthBook)
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

    private fun abilityBook(index: Int, name: String) = ItemStack(Material.BOOK).meta {
        displayName(language.getMessage("gui.compass.upgrade_skill", language.getMessageAsString("elements.$name")))
        val lore = mutableListOf(emptyComponent)
        lore.addAll(language.getMessageMultiline("gui.compass.upgrade_skill_numbers",
            skillPercentage(skills[index]) * 100.0, skillPercentage(skills[index] + 1) * 100.0, skills[index], skills[index] + 1))
        lore.add(emptyComponent)
        lore.addAll(language.getMessageMultiline("gui.compass.upgrade_skill_$name"))
        //lore.add(" ")
        //lore.add(language.getMessage("gui.compass.upgrade_skill_modified"))
        lore(lore)
    }

    private val strengthBook = { abilityBook(0, "strength") }
    private var currentStrengthBook = strengthBook()

    private val dexterityBook = { abilityBook(1, "dexterity") }
    private var currentDexterityBook = dexterityBook()

    private val intelligenceBook = { abilityBook(2, "intelligence") }
    private var currentIntelligenceBook = intelligenceBook()

    private val defenseBook = { abilityBook(3, "defense") }
    private var currentDefenseBook = defenseBook()

    private val agilityBook = { abilityBook(4, "agility") }
    private var currentAgilityBook = agilityBook()

    private val resetSkillPoints = ItemStack(Material.GOLDEN_SHOVEL).setAppearance(21).meta {
        addItemFlags(*ItemFlag.values())
        displayName(language.getMessage("gui.compass.reset_skills"))
        lore(listOf(language.getMessage("gui.compass.reset_skills_cost")))
    }

    private val jukebox = ItemStack(Material.JUKEBOX).meta { displayName(language.getMessage("gui.compass.jukebox")) }

    private val tomes = ItemStack(Material.ENCHANTED_BOOK).meta {
        displayName(language.getMessage("gui.compass.tomes"))
        //lore = language.getMessage("gui.compass.").split(NL_REGEX)
    }

    private val guildBanner: ItemStack
    init {
        val guildName = player.data.getString("guild")
        val guildLore: List<Component>
        if (guildName == null) {
            guildLore = listOf(language.getMessage("gui.compass.no_guild"))
            guildBanner = ItemStack(Material.WHITE_BANNER)
        } else {
            val guild = Guild(guildName)
            val guildMember = guild.members[player.name]!!
            guildLore = listOf(
                /*"§3${guild.name} §b[${guild.tag}]",
                " ",
                language.getMessage("gui.compass.guild_rank", guildMember.rank.friendlyName),
                language.getMessage("gui.compass.guild_joined", guildMember.joined),
                " ",
                language.getMessage("gui.compass.guild_open")*/
                Component.text(guild.name, TextColor.color(0x8f30c9))
                    .append(Component.text(" [", TextColor.color(0xa42dc2)))
                    .append(Component.text(guild.tag, TextColor.color(0xbf45de)))
                    .append(Component.text("]", TextColor.color(0xa42dc2)))
                    .style { it.decoration(TextDecoration.ITALIC, false) },
                emptyComponent,
                language.getMessage("gui.compass.guild_rank", guildMember.rank.friendlyName),
                language.getMessage("gui.compass.guild_joined", guildMember.joined),
                emptyComponent,
                language.getMessage("gui.compass.guild_open")
            )
            guildBanner = guild.banner.clone()
        }
        guildBanner.meta {
            displayName(language.getMessage("gui.compass.guild_view"))
            lore(guildLore)
        }
    }

    private val settings = ItemStack(Material.CRAFTING_TABLE).meta {
        displayName(language.getMessage("gui.compass.settings"))
        lore(listOf(language.getMessage("gui.compass.settings_lore")))
    }

    private val idStats = ItemStack(Material.PLAYER_HEAD).meta {
        displayName(language.getMessage("gui.compass.player_info", player.name))
        val lore = mutableListOf(
            language.getMessage(
                "gui.compass.player_rank",
                player.data.getString("rank")?.toLowerCase()?.capitalize() ?: "Player"
            ),
            emptyComponent,
            language.getMessage("gui.compass.player_level", 106),
            language.getMessage("gui.compass.player_class", classes[player.getWynnClass()]?.let {
                if (player.isCloneClass) player.getLocalizedString("classes.${it.id}.cloneName")
                else player.getLocalizedString("classes.${it.id}.className")
            } ?: "None"),
            language.getMessage("gui.compass.player_quests", 0, 0),
            emptyComponent,
            language.getMessage("gui.compass.player_ids"),
        )
        addIdValues(lore)
        lore(lore)
    }

    //TODO: remove
    val Any.id get() = if (this is WynnClass) this.id else "CLASS"

    private fun defenseStats() = ItemStack(Material.IRON_CHESTPLATE).meta {
        addItemFlags(*ItemFlag.values())
        displayName(language.getMessage("gui.compass.defense"))
        lore(listOf(
            emptyComponent,
            language.getMessage("gui.compass.defense_basic"),
            language.getMessage("gui.compass.defense_health", player.health.toInt(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value.toInt()),
            // Ranked
        ))
    }
    private var currentDefenseStats = defenseStats()

    private val invertedControls = player.invertedControls
    //private val clazz = classes[player.getWynnClass()]
    private val clone = player.isCloneClass

    private fun damageStats(): ItemStack = ItemStack(Material.IRON_SWORD).meta {
        addItemFlags(*ItemFlag.values())
        displayName(language.getMessage("gui.compass.damage"))
        val spellDamageKey = "gui.compass.spell_damage"
        lore(listOf(
            //"§7[${player.getFirstWeaponSlot().let { if (it == -1) null else player.inventory.getItem(it) }?.itemMeta?.displayName}§7]",
            Component.text("[", TextColor.color(0xa42dc2))
                .append(player.getFirstWeaponSlot().let { if (it == -1) null else player.inventory.getItem(it) }?.itemMeta?.displayName() ?: Component.text("null"))
                .append(Component.text("]", TextColor.color(0xa42dc2)))
                .style { it.decoration(TextDecoration.ITALIC, false) },
            emptyComponent,
            language.getMessage("gui.compass.main_attack_damage", if (invertedControls) 'R' else 'L'),
            // Damages
            language.getMessage("gui.compass.total_damage", 0, 0),
            emptyComponent,
            language.getMessage(spellDamageKey, if (invertedControls) "LRL" else "RLR", language.getMessageAsString("classes.${player.getWynnClass()}.spells.${if (clone) "1c" else "1"}"), 0, 0),
            language.getMessage(spellDamageKey, if (invertedControls) "LLL" else "RRR", language.getMessageAsString("classes.${player.getWynnClass()}.spells.${if (clone) "2c" else "2"}"), 0, 0),
            language.getMessage(spellDamageKey, if (invertedControls) "LRR" else "RLL", language.getMessageAsString("classes.${player.getWynnClass()}.spells.${if (clone) "3c" else "3"}"), 0, 0),
            language.getMessage(spellDamageKey, if (invertedControls) "LLR" else "RRL", language.getMessageAsString("classes.${player.getWynnClass()}.spells.${if (clone) "4c" else "4"}"), 0, 0),
        ))
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
            decorate()

            inventory.setItem(4, ItemStack(Material.GOLDEN_SHOVEL).setAppearance(21).meta {
                addItemFlags(*ItemFlag.values())
                displayName(language.getMessage("gui.compass.wl_settings.reset"))
                lore(listOf(language.getMessage("gui.compass.wl_settings.reset_default")))
            })

            val particles = player.data.getInt("particles") ?: 2
            val otherParticles = player.data.getInt("other_particles") ?: 2

            inventory.setItem(10, ItemStack(Material.PLAYER_HEAD).meta {
                displayName(language.getMessage("gui.compass.wl_settings.your_particles", particleSettingString(particles)))
                lore(particlesLore)
            })
            inventory.setItem(11, ItemStack(Material.PLAYER_HEAD).meta {
                displayName(language.getMessage("gui.compass.wl_settings.other_particles", particleSettingString(otherParticles)))
                lore(particlesLore)
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
            //"§fLeft-Click §7to increase",
            //"§fRight-Click §7to decrease"
            Component.text()
                .append(Component.keybind("key.dig").color(TextColor.color(0xaaccaa)))
                .append(Component.text(" to increase", NamedTextColor.GRAY))
                .style { it.decoration(TextDecoration.ITALIC, false) }
                .build(),
            Component.text()
                .append(Component.keybind("key.use").color(TextColor.color(0xaaccaa)))
                .append(Component.text(" to decrease", NamedTextColor.GRAY))
                .style { it.decoration(TextDecoration.ITALIC, false) }
                .build()
        )
    }

    private fun addIdValues(list: MutableList<Component>) {
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

    private fun MutableList<Component>.addIdValue(weapon: PersistentDataContainer?, name: String, displayName: String, suffix: String = "%") {
        val value = player.getId(name) + (weapon?.getContainer("ids")?.getInt(name) ?: 0)
        if (value != 0)
            //add("§d- §7$displayName: §f${if (value > 0) "+" else ""}$value$suffix")
            add(Component.text("- ", TextColor.color(0x9749e6))
                .append(Component.text("$displayName: ", NamedTextColor.GRAY))
                .append(Component.text("${if (value > 0) "+" else ""}$value$suffix", NamedTextColor.WHITE))
                .style { it.decoration(TextDecoration.ITALIC, false) })
    }
}