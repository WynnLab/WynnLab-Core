@file:JvmName("PlayerAPI")

package com.wynnlab.api

import com.wynnlab.*
import com.wynnlab.events.SpellCastEvent
import com.wynnlab.extensions.data
import com.wynnlab.items.WynnItem
import com.wynnlab.listeners.GUIListener
import com.wynnlab.localization.Language
import com.wynnlab.scoreboard.InfoSidebar
import com.wynnlab.scoreboard.scoreboards
import com.wynnlab.util.RefreshRunnable
import com.wynnlab.util.colorNonItalic
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.persistence.PersistentDataType
import kotlin.math.round

fun Entity.hasScoreboardTag(tag: String) = tag in scoreboardTags

fun Player.sendWynnMessage(key: String, vararg format_args: Any?) =
    sendMessage(PREFIX.append(getLocalizedText(key, *format_args)))

fun Player.getLocalizedString(key: String, vararg formatArgs: Any?) =
    Language[locale()].getMessageAsString(key, *formatArgs)

fun Player.getLocalizedText(key: String, vararg format_args: Any?) =
    Language[locale()].getMessage(key, *format_args)

fun Player.getLocalizedTextMultiline(key: String, vararg format_args: Any?) =
    Language[locale()].getMessageMultiline(key, *format_args)

fun Player.sendWynnMessageNonNls(message: TextComponent) {
    sendMessage(PREFIX.append(message))
}

fun Player.setWynnClass(wynnClass: String) {
    if (wynnClass == "MONK" && !isOp) return //TODO
    data.setString("class", wynnClass)
    updatePrefix()
}

fun Player.getWynnClass() = data.getString("class")

fun Player.togglePVP() {
    if (addScoreboardTag("pvp")) {
        sendWynnMessage("messages.pvp.on")
    } else {
        removeScoreboardTag("pvp")
        sendWynnMessage("messages.pvp.off")
    }
}

fun Player.hasWeaponInHand(): Boolean? {
    return getWynnClass()?.let { it == (inventory.itemInMainHand.getClassReq() ?: return null) }
}

fun Player.checkWeapon() =
    hasWeaponInHand()?.also { if (!it) sendMessage("§cYou cannot use this weapon!") } == true

val Player.weaponAttackSpeed get() = if (hasWeaponInHand() == true) inventory.itemInMainHand.getAttackSpeed() else null

val Player.attackSpeed get() = weaponAttackSpeed?.let { WynnItem.AttackSpeed.values()[(it.ordinal + getId("attack_speed_bonus")).coerceIn(0, 6)] }

fun Player.cooldown(): Boolean {
    val attackSpeed = attackSpeed ?: return false
    return if ("cooldown" !in scoreboardTags) {
        addScoreboardTag("cooldown")
        Bukkit.getScheduler().runTaskLater(wynnlab, Runnable { removeScoreboardTag("cooldown") }, attackSpeed.cooldown.toLong())
        setCooldown(inventory.itemInMainHand.type, attackSpeed.cooldown)
        false
    } else
        true
}

var Player.isCloneClass
get() = "clone" in scoreboardTags
set(value) { if (value) addScoreboardTag("clone") else removeScoreboardTag("clone") }

val Player.invertedControls get() = (getWynnClass()?.let { WynnClass[it] } as? WynnClass)?.invertedControls ?: false

fun Player.castSpell(id: Int) {
    Bukkit.getPluginManager().callEvent(SpellCastEvent(this, id))
}

fun Player.addLeftClick(invertedControls: Boolean = false) {
    if (this.invertedControls && !invertedControls) {
        addRightClick(true)
        return
    }
    when {
        "rrx" in scoreboardTags -> {
            scoreboardTags.remove("rrx")
            updateActionBar(if (invertedControls) abComponent('L', 'L', 'R') else abComponent('R', 'R', 'L'))
            castSpell(4)
        }
        "rlx" in scoreboardTags -> {
            scoreboardTags.remove("rlx")
            updateActionBar(if (invertedControls) abComponent('L', 'R', 'R') else abComponent('R', 'L', 'L'))
            castSpell(3)
        }
        "rxx" in scoreboardTags -> {
            scoreboardTags.remove("rxx")
            scoreboardTags.add("rlx")
            updateActionBar(if (invertedControls) abComponent('L', 'R', '?') else abComponent('R', 'L', '?'))
        }
        else -> {
            castSpell(0)
            return
        }
    }
    scheduleCancelSpellClicks()
    playEffect(location, Effect.CLICK1, null)
}

fun Player.addRightClick(invertedControls: Boolean = false) {
    if (this.invertedControls && !invertedControls) {
        addLeftClick(true)
        return
    }
    when {
        "rrx" in scoreboardTags -> {
            scoreboardTags.remove("rrx")
            updateActionBar(if (invertedControls) abComponent('L', 'L', 'L') else abComponent('R', 'R', 'R'))
            castSpell(2)
        }
        "rlx" in scoreboardTags -> {
            scoreboardTags.remove("rlx")
            updateActionBar(if (invertedControls) abComponent('L', 'R', 'L') else abComponent('R', 'L', 'R'))
            castSpell(1)
        }
        "rxx" in scoreboardTags -> {
            scoreboardTags.remove("rxx")
            scoreboardTags.add("rrx")
            updateActionBar(if (invertedControls) abComponent('L', 'L', '?') else abComponent('R', 'R', '?'))
        }
        else -> {
            scoreboardTags.add("rxx")
            updateActionBar(if (invertedControls) abComponent('L', '?', '?') else abComponent('R', '?', '?'))
        }
    }
    scheduleCancelSpellClicks()
    playEffect(location, Effect.CLICK1, null)
}

private fun abComponent(lr1: Char, lr2: Char, lr3: Char): TextComponent {
    fun charComponent(c: Char) = if (c == '?') Component.text(c, NamedTextColor.WHITE).style { it.decoration(TextDecoration.UNDERLINED, false) }
        else Component.text(c, WL_COLOR, TextDecoration.UNDERLINED)
    //updateActionBar(if (invertedControls) "§a§nL§r-§a§nR§r-§n?" else "§a§nR§r-§a§nL§r-§n?")
    val dash = Component.text("-", TextColor.color(0xdddddd)).style { it.decoration(TextDecoration.UNDERLINED, false) }
    return charComponent(lr1).append(dash).append(charComponent(lr2)).append(dash).append(charComponent(lr3))
}

private fun Player.scheduleCancelSpellClicks() {
    RefreshRunnable(data, "cancel_spell") { cancelSpellClicks() }.schedule(20L)
}

fun Player.cancelSpellClicks() {
    if (removeScoreboardTag("rxx")) return
    if (removeScoreboardTag("rrx")) return
    removeScoreboardTag("rlx")
}

fun Player.updateActionBar(msg: TextComponent) {
    sendWynnActionBar(msg)
    if ("action_bar" !in scoreboardTags)
        addScoreboardTag("action_bar")
    RefreshRunnable(data, "action_bar") {
        removeScoreboardTag("action_bar")
        standardActionBar()
    }.schedule(20L)
}

fun Player.standardActionBar() {
    if ("action_bar" !in scoreboardTags) {
        sendWynnActionBar(Component.text(""))
    }
}

private fun Player.sendWynnActionBar(msg: TextComponent) {
    //val health = "§4[§c❤ ${health.toInt()}/${getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value?.toInt()}§4]§r"
    //val mana = "§3[§b✺ $foodLevel/20§3]§r"
    //val mlD2 = (ChatColor.stripColor(msg)!!.length) / 2
    /*sendActionBar(buildString {
        append(health)
        repeat(20 - health.length + 8 - mlD2) { append(' ') }
        append(msg)
        append("§r")
        repeat(20 - mana.length + 8 - mlD2) { append(' ') }
        append(mana)
    })*/
    val health = Component.text("[", TextColor.color(0xb0232f))
        .append(Component.text("❤ ", TextColor.color(0xd92b3a)))
        .append(Component.text(health.toInt(), TextColor.color(0xe82738)))
        .append(Component.text("/", TextColor.color(0xd92b3a)))
        .append(Component.text(getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value.toInt(), TextColor.color(0xe82738)))
        .append(Component.text("]", TextColor.color(0xb0232f)))

    val mana = Component.text("[", TextColor.color(0x23abb0))
        .append(Component.text("✺ ", TextColor.color(0x2bd3d9)))
        .append(Component.text(foodLevel, TextColor.color(0x23e1e8)))
        .append(Component.text("/", TextColor.color(0x2bd3d9)))
        .append(Component.text("20", TextColor.color(0x23e1e8)))
        .append(Component.text("]", TextColor.color(0x23abb0)))

    val mlD2 = msg.content().length / 2
    sendActionBar(health
        .append(Component.text((StringBuffer().apply { repeat((20 - health.content().length - mlD2) / 2) { append(' ') } }).toString()))
        .append(msg)
        .append(Component.text((StringBuffer().apply { repeat((20 - mana.content().length - mlD2) / 2) { append(' ') } }).toString()))
        .append(mana)
    )
}

var Player.prefix: String
get() = prefixes[this] ?: ""
set(value) {
    prefixes[this] = value
    updatePrefix()
}

val prefixes = hashMapOf<Player, String>()

fun Player.wynnPrefix(): TextComponent {
    val classId = getWynnClass() ?: return Component.text("§r")
    val classPrefix = if (isCloneClass) Language.en_us.getMessage("classes.$classId.cloneName")
        else Language.en_us.getMessage("classes.$classId.className")
    //return "§7[106/${classPrefix.substring(0..1)}${data.getString("guild_tag")?.let { "/$it" } ?: ""}] §r"
    return Component.text("[", TextColor.color(0x888888))
        .append(Component.text(106, TextColor.color(0x99cc99)))
        .append(Component.text("/", TextColor.color(0x888888)))
        .append(Component.text(classPrefix.content().substring(0, 2), TextColor.color(0xcc9999)))
        .append(Component.text(if (data.has(NamespacedKey(wynnlab, "guild_tag"), PersistentDataType.STRING)) "/" else "", TextColor.color(0x888888)))
        .append(Component.text(data.getString("guild_tag") ?: "", TextColor.color(0x99b0cc)))
        .append(Component.text("]", TextColor.color(0x888888)))
        .append(Component.text(" §r"))
}

fun Player.updatePrefix() {
    //setDisplayName(wynnPrefix()+prefix+name)
    displayName(wynnPrefix().append(Component.text(prefix+name)))
    //setPlayerListName(prefix+name)
    playerListName(Component.text(prefix+name))
}

val Player.wynnEquipment get() = inventory.let { inv -> arrayOf(
    if (hasWeaponInHand() == true) inv.itemInMainHand else null,
    inv.helmet?.takeIfType(WynnItem.Type.HELMET),
    inv.chestplate?.takeIfType(WynnItem.Type.CHESTPLATE),
    inv.leggings?.takeIfType(WynnItem.Type.LEGGINGS),
    inv.boots?.takeIfType(WynnItem.Type.BOOTS),
    inv.getItem(9)?.takeIfType(WynnItem.Type.RING),
    inv.getItem(10)?.takeIfType(WynnItem.Type.RING),
    inv.getItem(11)?.takeIfType(WynnItem.Type.BRACELET),
    inv.getItem(12)?.takeIfType(WynnItem.Type.NECKLACE)
) }

fun Player.getFirstWeaponSlot() = inventory.let {
    for (i in 0..5) {
        if (((it.getItem(i) ?: continue).itemMeta ?: continue).data.getString("class_req") == getWynnClass())
            return i
    }
    return@let -1
}

fun Player.getId(key: String): Int {
    var sum = 0
    for (item in wynnEquipment) {
        if (item == null) continue
        sum += ((item.itemMeta ?: continue).data.getContainer("ids") ?: continue).getInt(key) ?: continue
    }
    return sum
}

fun Player.getSkills() = data.getIntArray("skill_points") ?: run {
    data.setIntArray("skill_points", intArrayOf(0, 0, 0, 0, 0))
    return intArrayOf(0, 0, 0, 0, 0)
}

fun Player.getSkill(index: Int): Int =
    (data.getIntArray("skill_points") ?: run {
        data.setIntArray("skill_points", intArrayOf(0, 0, 0, 0, 0))
        return 0
    })[index]

/*
if(p>=150) return 80.8f;
        else if(p<=0) return 0;
        else return Math.round(10*(-0.0000000166f*p*p*p*p+0.0000122614f*p*p*p-0.0044972984f*p*p+0.9931907398f*p+0.0093811967f))/10f;
 */
fun skillPercentage(p: Int): Double = when {
    p >= 150 -> .808
    p <= 0 -> .0
    else -> round(10.0 * (-0.0000000166 * p*p*p*p + 0.0000122614 * p*p*p - 0.0044972984 * p*p + 0.9931907398 * p + 0.0093811967)) / 1000.0
}

// Damage Type: 0 -> Melee Neutral, 1 -> Melee Elemental, 2 -> Spell Neutral, 3 -> Spell Elemental
val standardConversion = doubleArrayOf(1.0, .0, .0, .0, .0, .0)
private val noDamage = doubleArrayOf(.0, .0, .0, .0, .0, .0)

fun Player.getDamage(melee: Boolean, multiplier: Double = 1.0, conversion: DoubleArray = standardConversion): DoubleArray {
    val pvp = hasScoreboardTag("pvp")

    // Melee Neutral = (Base Dam) * (1 + (IDs) - (Def)) + (Raw Melee)
    // Melee Elemental = (Base Dam) * (1 + (IDs)) - ((Ele Def) * (1 + (Ele Def %)))
    // Spell Neutral = (Base Dam) * (1 + (IDs) - (Def)) * (Att Speed) * (Spell Base Multiplier) + (Raw Spell) * (Spell Base Multiplier)
    // Spell Elemental = [(Base Dam) * (1 + (IDs)) - ((Ele Def) * (1 + (Ele Def %)))] * (Att Speed) * (Spell Base Multiplier)

    val baseDamage = getBaseDamage(conversion)
    val modifiers = getDamageModifiers(melee, pvp)

    return getModifiedDamage(baseDamage, modifiers, multiplier, melee)
}

private fun Player.getBaseDamage(conversion: DoubleArray): DoubleArray {
    val damageRanges = if (hasWeaponInHand() ?: return noDamage)
        inventory.itemInMainHand.itemMeta.data.getIntArray("damage") ?: return noDamage
    else return noDamage

    val damages = DoubleArray(6) { i ->
        (damageRanges[i * 2 + 1] - damageRanges[i * 2].let { if (it > 0) random.nextInt(it) else it } + damageRanges[i * 2]).toDouble()
    }

    repeat(6) { i ->
        damages[i] = if (i > 0) damages[0] * conversion[i] + damages[i] else damages[0] * conversion[0]
    }

    return damages
}

private fun Player.getDamageModifiers(melee: Boolean, pvp: Boolean): DoubleArray {
    val strength = skillPercentage(getSkill(0)).let { if (pvp) it.coerceAtMost(.6) else it }
    val dexterity = if (random.nextDouble() < skillPercentage(getSkill(1)).let { if (pvp) it.coerceAtMost(.35) else it }) 1.0 else .0

    return DoubleArray(6) { i ->
        var value = strength + dexterity
        value += if (melee)
            getId("damage_bonus") / 100.0
        else
            getId("spell_damage") / 100.0
        if (i > 0)
            value += getId("bonus_${elementNamesLC[i - 1]}_damage") / 100.0
        value
    }
}

private fun Player.getModifiedDamage(baseDamage: DoubleArray, modifiers: DoubleArray, multiplier: Double, melee: Boolean): DoubleArray {
    val result = noDamage

    if (melee) {
        result[0] = baseDamage[0] * (1 + modifiers[0] /*- def*/) + getId("damage_bonus_raw")
        repeat(5) { i ->
            result[i + 1] = baseDamage[i + 1] * (1 + modifiers[i + 1]) /*- def*/
        }
    } else {
        val attackSpeedSpellMultiplier = weaponAttackSpeed!!.spellMultiplier
        result[0] = (baseDamage[0] * (1 + modifiers[0] /*- def*/) * attackSpeedSpellMultiplier + getId("spell_damage_raw")) * multiplier
        repeat(5) { i ->
            result[i + 1] = (baseDamage[i + 1] * (1 + modifiers[i + 1]) /*- def*/) * attackSpeedSpellMultiplier * multiplier
        }
    }

    return result
}

private val elementNamesLC = arrayOf("earth", "thunder", "water", "fire", "air")

fun Player.getArmorHealth(): Int {
    var sum = 0
    for (item in wynnEquipment) {
        if (item == null) continue
        sum += (item.itemMeta ?: continue).data.getInt("health") ?: continue
    }
    return sum
}

fun Player.testInventory() {
    wynnEquipment.forEachIndexed { i, item ->
        if (i != 0 && item == null) {
            if (i <= 4) {
                inventory.getItem(104 - i)?.let { realItem ->
                    sendWynnMessage("messages.wrong_item")
                    inventory.setItem(104 - i, null)
                    inventory.addItem(realItem)
                }
            } else {
                inventory.getItem(4 + i)?.let { realItem ->
                    if (realItem.type != Material.SNOW) {
                        sendWynnMessage("messages.wrong_item")

                        inventory.setItem(4 + i, GUIListener.snowForSlot(4 + i))

                        inventory.addItem(realItem)
                    }
                }
            }
        }
    }
}

fun Player.updatePouch(add: ItemStack? = null) {
    inventory.setItem(13, ItemStack(Material.DIAMOND_AXE).setAppearance(93).meta {
                addItemFlags(*ItemFlag.values())
                //setDisplayName("§6Magic Pouch")
                /*lore = listOf(
                    "§fLeft-Click §7to view contents",
                    //" "
                )*/
        displayName(Component.text("Magic Pouch", colorNonItalic(0xedd953)))
        lore(listOf(
            Component.text("Left-Click", colorNonItalic(NamedTextColor.WHITE))
                .append(Component.text(" to view contents", colorNonItalic(NamedTextColor.GRAY))),
            Component.text("Shift-Right-Click", colorNonItalic(NamedTextColor.WHITE))
                .append(Component.text(" to clear", colorNonItalic(NamedTextColor.GRAY)))
        ))

                data.setBoolean("pouch", true)
            })

}

fun Player.showPouch() {
    setMetadata("a", FixedMetadataValue(wynnlab, Bukkit.createInventory(null, 5)))
}

fun Player.updateSidebar() {
    val sb = scoreboards[data.getString("scoreboard")]
    if (sb == null)
        InfoSidebar.update(this)
    else
        sb.update(this)
}