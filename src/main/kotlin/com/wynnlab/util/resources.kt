package com.wynnlab.util

import com.wynnlab.plugin
import java.util.logging.Level

fun saveAllResources() {
    plugin.logger.log(Level.INFO, "Saving resources...")
    resources.forEach {
        plugin.saveResource(it, true)
    }
    plugin.logger.log(Level.INFO, "... Done")
}

val resources = arrayOf(
    /////////////////////////////////////
    // Classes
    /////////////////////////////////////

    "classes/archer/archer.yml",
    "classes/archer/archer_main.py",
    "classes/archer/arrow_storm.py",
    "classes/archer/escape.py",
    "classes/archer/bomb_arrow.py",
    "classes/archer/arrow_shield.py",
    "classes/archer/arrow_rain.py",

    "classes/assassin/assassin.yml",
    "classes/assassin/assassin_main.py",
    "classes/assassin/spin_attack.py",
    "classes/assassin/vanish.py",
    "classes/assassin/multihit.py",
    "classes/assassin/smoke_bomb.py",
    "classes/assassin/vanish_end.py",
    "classes/assassin/smoke_bomb_tick.py",

    "classes/mage/mage.yml",
    "classes/mage/mage_main.py",
    "classes/mage/heal.py",
    "classes/mage/teleport.py",
    "classes/mage/meteor.py",
    "classes/mage/ice_snake.py",

    "classes/shaman/shaman.yml",
    "classes/shaman/shaman_main.py",
    "classes/shaman/totem.py",
    "classes/shaman/haul.py",
    "classes/shaman/aura.py",
    "classes/shaman/uproot.py",
    "classes/shaman/totem_tick.py",

    "classes/warrior/warrior.yml",
    "classes/warrior/warrior_main.py",
    "classes/warrior/bash.py",
    "classes/warrior/charge.py",
    "classes/warrior/uppercut.py",
    "classes/warrior/war_scream.py",
    "classes/warrior/uppercut_throw.py",

    /////////////////////////////////////
    // Languages
    /////////////////////////////////////

    "lang/en_us.yml",
    "lang/de_de.yml",
    "lang/ar_sa.yml"
)