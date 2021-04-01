package com.wynnlab.spells

import com.wynnlab.api.isCloneClass
import com.wynnlab.currentClassLoadFolder
import com.wynnlab.python
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.python.core.Py
import org.python.core.PyBoolean
import org.python.core.PyInteger
import org.python.core.PyType
import java.io.File
import java.io.FileReader

/*enum class SpellData(val spellName: String, val cloneSpellName: String, val cost: Int) {
    BASH("Bash", "Holy Blast", 6),
    CHARGE("Charge", "Leap", 4),
    UPPERCUT("Uppercut", "Heaven Jolt", 10),
    WAR_SCREAM("War Scream", "Cry Of The Gods", 6),

    ARROW_STORM("Arrow Storm", "Bolt Blizzard", 6),
    ESCAPE("Escape", "Spider Jump", 3),
    BOMB_ARROW("Bomb Arrow", "Creeper Dart", 8),
    ARROW_SHIELD("Arrow Shield", "Dagger Aura", 10),

    HEAL("Heal", "Remedy", 6),
    TELEPORT("Teleport", ""/*TODO*/, 4),
    METEOR("Meteor", "Death Star", 8),
    ICE_SNAKE("Ice Snake", "" /*TODO*/, 4),

    SPIN_ATTACK("Spin Attack", "Whirlwind", 6),
    VANISH("Vanish", "Shadow Clone", 1),
    MULTIHIT("Multihit", "Shadow Assault", 8),
    SMOKE_BOMB("Smoke Bomb", "Noxious Cloud", 8),

    TOTEM("Totem", "Sky Emblem", 4),
    HAUL("Haul", "Soar", 1),
    AURA("Aura", "Wind Surge", 8),
    UPROOT("Uproot", "" /*TODO*/, 6),
}*/

data class Spell(
    val spellName: String,
    val cloneSpellName: String,
    val cost: Int,
    val maxTick: Int,
    val pythonClass: PyType,
) : ConfigurationSerializable {
    fun cast(player: Player) {
        val instance = pythonClass.__call__()
        instance.__setattr__("player", Py.java2py(player))
        instance.__setattr__("clone", PyBoolean(player.isCloneClass))
        instance.__setattr__("maxTick", PyInteger(maxTick))

        instance("schedule")
    }

    override fun serialize(): MutableMap<String, Any> {
        val out = LinkedHashMap<String, Any>()

        out["spellName"] = spellName
        out["cloneSpellName"] = cloneSpellName
        out["cost"] = cost
        out["maxTick"] = maxTick

        return out
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        fun deserialize(map: Map<String, Any>): Spell {
            val spellName = map["spellName"] as String
            val cloneSpellName = map["cloneSpellName"] as String
            val cost = (map["cost"] as Number).toInt()
            val maxTick = (map["maxTick"] as Number).toInt()

            val scriptFile = File(currentClassLoadFolder, map["script"] as String)
            val script = FileReader(scriptFile).use { reader ->
                python.compile(reader)
            }
            python.exec(script)
            val pythonClass = python.get("Spell") as PyType //TODO: name

            return Spell(spellName, cloneSpellName, cost, maxTick, pythonClass)
        }
    }
}