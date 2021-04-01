@file:JvmName("PlayerAPI")

package com.wynnlab.api

import com.wynnlab.WynnClass
import com.wynnlab.classes
import com.wynnlab.events.SpellCastEvent
import com.wynnlab.plugin
import com.wynnlab.util.RefreshRunnable
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

fun Player.setWynnClass(wynnClass: String) {
    data.setString("class", wynnClass)
    updatePrefix()
}

fun Player.getWynnClass() = data.getString("class")

fun Player.hasWeaponInHand(): Boolean? {
    return getWynnClass()?.let { it == (inventory.itemInMainHand.getClassReq() ?: return null) }
}

fun Player.checkWeapon() =
    hasWeaponInHand()?.also { if (!it) sendMessage("§cYou cannot use this weapon!") } == true

val Player.attackSpeed get() = if (hasWeaponInHand() == true) inventory.itemInMainHand.getAttackSpeed() else null

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
    val wynnClass = getWynnClass()?.let { classes[it] } ?: return "§r"
    val classPrefix = if (isCloneClass) wynnClass.cloneName else wynnClass.className
    return "§7[${classPrefix.substring(0..1)}/106] §r"
}

fun Player.updatePrefix() {
    setDisplayName(wynnPrefix()+prefix+name)
}