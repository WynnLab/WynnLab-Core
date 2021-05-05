package com.wynnlab.spells

import com.wynnlab.api.isCloneClass
import com.wynnlab.currentClassLoadFolder
import com.wynnlab.spellOrdinal
import com.wynnlab.util.TickRunnable
import com.wynnlab.wynnscript.CompiledWynnScript
import com.wynnlab.wynnscript.NoSuchFunctionException
import com.wynnlab.wynnscript.WynnScript
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import java.io.File
import java.io.FileReader

data class PlayerSpell(
    override val cost: Int,
    override val maxTick: Int,
    val script: CompiledWynnScript,
    override val ordinal: Int
) : Spell, ConfigurationSerializable {
    override fun cast(player: Player, vararg args: Any?) {
        script.reset()

        val clone = player.isCloneClass

        object : TickRunnable() {
            override fun init() {
                script.setData("taskId", taskId)
                try {
                    script("init", player, clone, *args)
                } catch (_: NoSuchFunctionException) {}
            }

            override fun tick() {
                script("tick", t, player, clone)
            }
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        val out = LinkedHashMap<String, Any>()

        out["cost"] = cost
        out["maxTick"] = maxTick

        return out
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        fun deserialize(map: Map<String, Any>): PlayerSpell {
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