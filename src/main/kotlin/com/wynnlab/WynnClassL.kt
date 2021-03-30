package com.wynnlab

import com.wynnlab.spells.Spell
import com.wynnlab.spells.SpellL
import com.wynnlab.spells.archer.*
import com.wynnlab.spells.assassin.*
import com.wynnlab.spells.mage.*
import com.wynnlab.spells.shaman.*
import com.wynnlab.spells.warrior.*
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import java.io.File
import java.util.logging.Level
import kotlin.reflect.KClass

enum class WynnClassL(val className: String, val cloneName: String, val spells: List<KClass<out SpellL>>) {
    WARRIOR("Warrior", "Knight", spells<WarriorMain, Bash, Charge, Uppercut, WarScream>()),
    ARCHER("Archer", "Hunter", spells<ArcherMain, ArrowStorm, Escape, BombArrow, ArrowShield>()),
    MAGE("Mage", "Dark Wizard", spells<MageMain, Heal, Teleport, Meteor, IceSnake>()),
    ASSASSIN("Assassin", "Ninja", spells<AssassinMain, SpinAttack, Vanish, Multihit, SmokeBomb>()),
    SHAMAN("Shaman", "Skyseer", spells<ShamanMain, Totem, Haul, Aura, Uproot>());

    fun both() = "$className/$cloneName"
}

inline fun <reified Main : SpellL, reified Spell1 : SpellL, reified Spell2 : SpellL, reified Spell3 : SpellL, reified Spell4 : SpellL> spells() =
    listOf(Main::class, Spell1::class, Spell2::class, Spell3::class, Spell4::class)


data class WynnClass(
    val className: String,
    val cloneName: String,
    val spells: List<Spell>
) : ConfigurationSerializable {

    override fun serialize(): Map<String, Any> {
        val out = LinkedHashMap<String, Any>()

        out["className"] = className
        out["cloneName"] = cloneName
        out["spells"] = spells.map { it.serialize() }

        return out
    }

    companion object {
        @JvmStatic
        @Suppress("unused", "unchecked_cast")
        fun deserialize(map: Map<String, Any>): WynnClass {
            val className = map["className"] as String
            val cloneName = map["cloneName"] as String
            val spells = map["spells"] as List<Spell>

            return WynnClass(className, cloneName, spells)
        }
    }
}

val classes = hashMapOf<String, WynnClass>()

fun loadClasses() {
    val classFolder = File(plugin.dataFolder, "classes")

    if (!classFolder.exists())
        plugin.logger.log(Level.WARNING, "No classes loaded")

    for (f in classFolder.listFiles { f, _ -> f.isDirectory } ?: return) {
        currentClassLoadFolder = f

        plugin.logger.log(Level.INFO, "Loading class ${f.name} ...")

        val configFile = File(f, "${f.name}.yml")
        val config = YamlConfiguration()
        config.load(configFile)
        val wynnClass = config.getSerializable("class", WynnClass::class.java) ?: continue
        classes[wynnClass.className.toUpperCase()] = wynnClass
    }

    plugin.logger.log(Level.INFO, "Classes: $classes")
}

internal lateinit var currentClassLoadFolder: File