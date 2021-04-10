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

    "classes/mage/mage.yml",
    "classes/mage/mage_main.py",
    "classes/mage/heal.py",
    "classes/mage/teleport.py",
    "classes/mage/meteor.py",
    "classes/mage/ice_snake.py",

    /////////////////////////////////////
    // Languages
    /////////////////////////////////////

    "lang/en_us.yml",
    "lang/de_de.yml",
    "lang/ar_sa.yml"
)