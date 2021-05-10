package com.wynnlab.spells

import com.wynnlab.api.isCloneClass
import com.wynnlab.currentClassLoadFolder
import com.wynnlab.spellOrdinal
import com.wynnlab.util.BaseSerializable
import com.wynnlab.util.ConfigurationDeserializable
import com.wynnlab.util.TickRunnable
import com.wynnlab.wynnscript.CompiledWynnScript
import com.wynnlab.wynnscript.NoSuchFunctionException
import com.wynnlab.wynnscript.WynnScript
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

    }

    override fun cast(player: Player, vararg args: Any?) {
        script.resetData()

        val spellPlayer = SpellPlayer(player)
        val clone = player.isCloneClass

        val runnable = object : TickRunnable() {
            override fun init() {
                script.setData("task", this)
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