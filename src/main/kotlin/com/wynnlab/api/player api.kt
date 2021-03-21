package com.wynnlab.api

import com.wynnlab.events.CastEvent
import com.wynnlab.items.WynnItem
import com.wynnlab.plugin
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.entity.Player

fun Player.setWynnClass(wynnClass: String) {
    data.setString("class", wynnClass)
}

fun Player.getWynnClass() = data.getString("class")

fun Player.hasWeaponInHand() = when (getWynnClass()) {
    "WARRIOR" -> inventory.itemInMainHand.getWynnType() == WynnItem.Type.SPEAR
    "ARCHER" -> inventory.itemInMainHand.getWynnType() == WynnItem.Type.BOW
    "MAGE" -> inventory.itemInMainHand.getWynnType() == WynnItem.Type.WAND
    "ASSASSIN" -> inventory.itemInMainHand.getWynnType() == WynnItem.Type.DAGGER
    "SHAMAN" -> inventory.itemInMainHand.getWynnType() == WynnItem.Type.RELIK
    else -> null
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


val Player.isCloneClass get() = "clone" in scoreboardTags

val Player.invertedControls get() = getWynnClass()?.toUpperCase() == "ARCHER"

fun Player.castSpell(id: Int) {
    Bukkit.getPluginManager().callEvent(CastEvent(this, id))
}

fun Player.addLeftClick(invertedControls: Boolean = false) {
    if (this.invertedControls && !invertedControls) {
        addRightClick(true)
        return
    }
    when {
        "rrx" in scoreboardTags -> {
            scoreboardTags.remove("rrx")
            sendActionBar(if (invertedControls) "§a§nL§r-§a§nL§r-§a§nR" else "§a§nR§r-§a§nR§r-§a§nL")
            castSpell(4)
        }
        "rlx" in scoreboardTags -> {
            scoreboardTags.remove("rlx")
            sendActionBar(if (invertedControls) "§a§nL§r-§a§nR§r-§a§nR" else "§a§nR§r-§a§nL§r-§a§nL")
            castSpell(3)
        }
        "rxx" in scoreboardTags -> {
            scoreboardTags.remove("rxx")
            scoreboardTags.add("rlx")
            sendActionBar(if (invertedControls) "§a§nL§r-§a§nR§r-§n?" else "§a§nR§r-§a§nL§r-§n?")
        }
        else -> {
            castSpell(0)
            return
        }
    }
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
            sendActionBar(if (invertedControls) "§a§nL§r-§a§nL§r-§a§nL" else "§a§nR§r-§a§nR§r-§a§nR")
            castSpell(2)
        }
        "rlx" in scoreboardTags -> {
            scoreboardTags.remove("rlx")
            sendActionBar(if (invertedControls) "§a§nL§r-§a§nR§r-§a§nL" else "§a§nR§r-§a§nL§r-§a§nR")
            castSpell(1)
        }
        "rxx" in scoreboardTags -> {
            scoreboardTags.remove("rxx")
            scoreboardTags.add("rrx")
            sendActionBar(if (invertedControls) "§a§nL§r-§a§nL§r-§n?" else "§a§nR§r-§a§nR§r-§n?")
        }
        else -> {
            scoreboardTags.add("rxx")
            sendActionBar(if (invertedControls) "§a§nL§r-§n?§r-§n?" else "§a§nR§r-§n?§r-§n?")
        }
    }
    playEffect(location, Effect.CLICK1, null)
}