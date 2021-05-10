package com.wynnlab.spells

import com.wynnlab.api.isCloneClass
import com.wynnlab.currentClassLoadFolder
import com.wynnlab.spellOrdinal
import com.wynnlab.util.BaseSerializable
import com.wynnlab.util.ConfigurationDeserializable
import com.wynnlab.util.LocationIterator
import com.wynnlab.util.TickRunnable
import com.wynnlab.wynnscript.CompiledWynnScript
import com.wynnlab.wynnscript.Invocable
import com.wynnlab.wynnscript.NoSuchFunctionException
import com.wynnlab.wynnscript.WynnScript
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.io.File
import java.io.FileReader

data class PlayerSpell(
    override val cost: Int,
    override val maxTick: Int,
    val script: CompiledWynnScript,
    override val ordinal: Int
) : Spell, BaseSerializable<PlayerSpell>() {
    init {
        script["print"] = Invocable { _, args -> if (args.size != 1) throw IllegalArgumentException() else
            println(args[0])
        }

        script["array"] = Invocable { _, args -> args }
        script["list"] = Invocable { _, args -> mutableListOf(*args) }
        script["set"] = Invocable { _, args -> mutableSetOf(*args) }
        script["map"] = Invocable { _, args -> if (args.isNotEmpty()) throw IllegalArgumentException() else
            mutableMapOf<Any?, Any?>()
        }

        script["SpellUtils"] = SpellUtils

        script["locations"] = Invocable { _, args -> if (args.size != 3) throw IllegalArgumentException() else
            LocationIterator(args[0] as Location, args[1] as Location, (args[2] as Number).toDouble())
        }

        script["Material"] = Material::class.java
        script["Particle"] = Particle::class.java
        script["Sound"] = Sound::class.java
    }

    override fun cast(player: Player, vararg args: Any?) {
        script.resetData()

        val spellPlayer = SpellPlayer(player)
        val clone = player.isCloneClass

        val runnable = object : TickRunnable() {
            override fun init() {
                script.setData("taskId", taskId)
                try {
                    script("init", spellPlayer, clone, *args)
                } catch (_: NoSuchFunctionException) {
                } catch (e: Throwable) {
                    reportError(e, "§cError at initializing script", player)
                }
            }

            override fun tick() {
                try {
                    script("tick", t, spellPlayer, clone)
                } catch (e: Throwable) {
                    reportError(e, "§cError at executing script (tick $t)", player)
                }
            }
        }

        runnable.schedule()
    }

    override fun serialize(): MutableMap<String, Any> {
        val out = LinkedHashMap<String, Any>()

        out["cost"] = cost
        out["maxTick"] = maxTick

        return out
    }

    override val deserializer = Companion

    companion object : ConfigurationDeserializable<PlayerSpell> {
        @JvmStatic
        @Suppress("unused")
        override fun deserialize(map: Map<String, Any?>): PlayerSpell {
            val cost = (map["cost"] as Number? ?: 0).toInt()
            val maxTick = (map["maxTick"] as Number).toInt()

            val scriptFile = File(currentClassLoadFolder, map["script"] as String)

            val script = FileReader(scriptFile).use { reader ->
                WynnScript(reader)
            }.compile()

            return PlayerSpell(cost, maxTick, script, spellOrdinal++)
        }
    }
}