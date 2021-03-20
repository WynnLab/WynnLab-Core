package com.wynnlab.api

import com.wynnlab.events.CastEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Player.setWynnClass(wynnClass: String) {
    data.setString("class", wynnClass)
}

fun Player.getWynnClass() = data.getString("class")

fun Player.hasWeaponInHand() = true // TODO

val Player.invertedControls get() = false // TODO

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
        }
    }
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
}