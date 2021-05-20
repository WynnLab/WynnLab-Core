@file:JvmName("PlayerAPI")

package com.wynnlab.api

import com.wynnlab.PREFIX
import com.wynnlab.WynnClass
import com.wynnlab.events.SpellCastEvent
import com.wynnlab.items.WynnItem
import com.wynnlab.listeners.GUIListener
import com.wynnlab.localization.Language
import com.wynnlab.plugin
import com.wynnlab.random
import com.wynnlab.scoreboard.scoreboards
import com.wynnlab.util.RefreshRunnable
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import kotlin.math.round

fun Entity.hasScoreboardTag(tag: String) = tag in scoreboardTags

fun Player.sendWynnMessage(key: String, vararg format_args: Any?) =
    sendMessage(PREFIX + getLocalizedText(key, *format_args))

fun Player.getLocalizedText(key: String, vararg format_args: Any?) =
    Language[locale.toLowerCase()].getMessage(key, *format_args)

fun Player.sendWynnMessageNonNls(message: String) {
    sendMessage(PREFIX + message)
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
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { removeScoreboardTag("cooldown") }, attackSpeed.cooldown.toLong())
        setCooldown(inventory.itemInMainHand.type, attackSpeed.cooldown)
        false
    } else
        true
}

var Player.isCloneClass
get() = "clone" in scoreboardTags
set(value) { if (value) addScoreboardTag("clone") else removeScoreboardTag("clone") }

val Player.invertedControls get() = getWynnClass()?.let { WynnClass[it] }?.invertedControls ?: false

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
            updateActionBar(if (invertedControls) "§a§nL§r-§a§nL§r-§a§nR" else "§a§nR§r-§a§nR§r-§a§nL")
            castSpell(4)
        }
        "rlx" in scoreboardTags -> {
            scoreboardTags.remove("rlx")
            updateActionBar(if (invertedControls) "§a§nL§r-§a§nR§r-§a§nR" else "§a§nR§r-§a§nL§r-§a§nL")
            castSpell(3)
        }
        "rxx" in scoreboardTags -> {
            scoreboardTags.remove("rxx")
            scoreboardTags.add("rlx")
            updateActionBar(if (invertedControls) "§a§nL§r-§a§nR§r-§n?" else "§a§nR§r-§a§nL§r-§n?")
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
            updateActionBar(if (invertedControls) "§a§nL§r-§a§nL§r-§a§nL" else "§a§nR§r-§a§nR§r-§a§nR")
            castSpell(2)
        }
        "rlx" in scoreboardTags -> {
            scoreboardTags.remove("rlx")
            updateActionBar(if (invertedControls) "§a§nL§r-§a§nR§r-§a§nL" else "§a§nR§r-§a§nL§r-§a§nR")
            castSpell(1)
        }
        "rxx" in scoreboardTags -> {
            scoreboardTags.remove("rxx")
            scoreboardTags.add("rrx")
            updateActionBar(if (invertedControls) "§a§nL§r-§a§nL§r-§n?" else "§a§nR§r-§a§nR§r-§n?")
        }
        else -> {
            scoreboardTags.add("rxx")
            updateActionBar(if (invertedControls) "§a§nL§r-§n?§r-§n?" else "§a§nR§r-§n?§r-§n?")
        }
    }
    scheduleCancelSpellClicks()
    playEffect(location, Effect.CLICK1, null)
}

private fun Player.scheduleCancelSpellClicks() {
    RefreshRunnable(data, "cancel_spell") { cancelSpellClicks() }.schedule(20L)
}

fun Player.cancelSpellClicks() {
    if (removeScoreboardTag("rxx")) return
    if (removeScoreboardTag("rrx")) return
    removeScoreboardTag("rlx")
}

fun Player.updateActionBar(msg: String) {
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
        sendWynnActionBar("")
    }
}

private fun Player.sendWynnActionBar(msg: String) {
    val health = "§4[§c❤ ${health.toInt()}/${getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value?.toInt()}§4]§r"
    val mana = "§3[§b✺ $foodLevel/20§3]§r"
    val mlD2 = (ChatColor.stripColor(msg)!!.length) / 2
    sendActionBar(buildString {
        append(health)
        repeat(20 - health.length + 8 - mlD2) { append(' ') }
        append(msg)
        append("§r")
        repeat(20 - mana.length + 8 - mlD2) { append(' ') }
        append(mana)
    })
}

var Player.prefix: String
get() = prefixes[this] ?: ""
set(value) {
    prefixes[this] = value
    updatePrefix()
}

val prefixes = hashMapOf<Player, String>()

fun Player.wynnPrefix(): String {
    val classId = getWynnClass() ?: return "§r"
    val classPrefix = if (isCloneClass) Language.en_us.getMessage("classes.$classId.cloneName")
        else Language.en_us.getMessage("classes.$classId.className")
    return "§7[106/${classPrefix.substring(0..1)}${data.getString("guild_tag")?.let { "/$it" } ?: ""}] §r"
}

fun Player.updatePrefix() {
    setDisplayName(wynnPrefix()+prefix+name)
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
    val result = noDamage

    val pvp = hasScoreboardTag("pvp")

    // Melee Neutral = (Base Dam) * (1 + (IDs) - (Def)) + (Raw Melee)
    // Melee Elemental = (Base Dam) * (1 + (IDs)) - ((Ele Def) * (1 + (Ele Def %)))
    // Spell Neutral = (Base Dam) * (1 + (IDs) - (Def)) * (Att Speed) * (Spell Base Multiplier) + (Raw Spell) * (Spell Base Multiplier)
    // Spell Elemental = [(Base Dam) * (1 + (IDs)) - ((Ele Def) * (1 + (Ele Def %)))] * (Att Speed) * (Spell Base Multiplier)

    val strength = skillPercentage(getSkill(0)).let { if (pvp) it.coerceAtMost(.6) else it }
    val dexterity = if (random.nextDouble() < skillPercentage(getSkill(1)).let { if (pvp) it.coerceAtMost(.35) else it }) 1.0 else .0

    val damageRanges = if (hasWeaponInHand() ?: return noDamage)
        inventory.itemInMainHand.itemMeta.data.getIntArray("damage") ?: return noDamage
    else return noDamage

    val damages = DoubleArray(6) { i ->
        (damageRanges[i * 2 + 1] - damageRanges[i * 2].let { if (it > 0) random.nextInt(it) else it } + damageRanges[i * 2]).toDouble()
    }

    repeat(6) { i ->
        damages[i] = if (i > 0) damages[0] * conversion[i] + damages[i] else damages[0] * conversion[0]
    }

    val ids = DoubleArray(6) { i ->
        var value = strength + dexterity
        value += if (melee)
            getId("damage_bonus") / 100.0
        else
            getId("spell_damage") / 100.0
        if (i > 0)
            value += getId("bonus_${elementNamesLC[i - 1]}_damage") / 100.0
        value
    }

    if (melee) {
        result[0] = damages[0] * (1 + ids[0] /*- def*/) + getId("damage_bonus_raw")
        repeat(5) { i ->
            result[i + 1] = damages[i + 1] * (1 + ids[i + 1]) /*- def*/
        }
    } else {
        val attackSpeedSpellMultiplier = weaponAttackSpeed!!.spellMultiplier
        result[0] = (damages[0] * (1 + ids[0] /*- def*/) * attackSpeedSpellMultiplier + getId("spell_damage_raw")) * multiplier
        repeat(5) { i ->
            result[i + 1] = (damages[i + 1] * (1 + ids[i + 1]) /*- def*/) * attackSpeedSpellMultiplier * multiplier
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
                setDisplayName("§6Magic Pouch")
                lore = listOf(
                    "§fLeft-Click §7to view contents",
                    //" "
                )
                data.setBoolean("pouch", true)
            })

}

fun Player.showPouch() {
    setMetadata("a", FixedMetadataValue(plugin, Bukkit.createInventory(null, 5)))
}

fun Player.updateSidebar() {
    scoreboards[data.getString("scoreboard")]?.update(this)
}