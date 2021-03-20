package com.wynnlab

import com.wynnlab.spells.Spell
import com.wynnlab.spells.archer.*
import com.wynnlab.spells.assassin.*
import com.wynnlab.spells.mage.*
import com.wynnlab.spells.shaman.*
import com.wynnlab.spells.warrior.*
import kotlin.reflect.KClass

enum class WynnClass(val className: String, val cloneName: String, val spells: List<KClass<out Spell>>) {
    WARRIOR("Warrior", "Knight", spells<WarriorMain, Bash, Charge, Uppercut, WarScream>()),
    ARCHER("Archer", "Hunter", spells<ArcherMain, ArrowRain, Escape, BombArrow, ArrowShield>()),
    MAGE("Mage", "Dark Wizard", spells<MageMain, Heal, Teleport, Meteor, IceSnake>()),
    ASSASSIN("Assassin", "Ninja", spells<AssassinMain, SpinAttack, Vanish, Multihit, SmokeBomb>()),
    SHAMAN("Shaman", "Skyseer", spells<ShamanMain, Totem, Haul, Aura, Uproot>());

    fun both() = "$className/$cloneName"
}

inline fun <reified Main : Spell, reified Spell1 : Spell, reified Spell2 : Spell, reified Spell3 : Spell, reified Spell4 : Spell> spells() =
    listOf(Main::class, Spell1::class, Spell2::class, Spell3::class, Spell4::class)