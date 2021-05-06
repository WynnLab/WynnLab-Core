package com.wynnlab.util

import com.wynnlab.WynnClass
import com.wynnlab.entities.WynnMob
import com.wynnlab.spells.MobSpell
import com.wynnlab.spells.PlayerSpell
import com.wynnlab.spells.PythonSpell
import com.wynnlab.spells.Spell
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization

abstract class BaseSerializable<S : BaseSerializable<S>> : ConfigurationSerializable {

    abstract val deserializer: ConfigurationDeserializable<S>

    abstract override fun serialize(): Map<String, Any?>
}

interface ConfigurationDeserializable<T : ConfigurationSerializable> {
    fun deserialize(map: Map<String, Any?>): T
}

fun registerSerializers() {
    registerSerializer<WynnMob.Equipment>()
    registerSerializer<MobSpell>()
    registerSerializer<PlayerSpell>()
    registerSerializer<PythonSpell>()
    registerSerializer<Spell>()
    registerSerializer<WynnClass>()
    registerSerializer<WynnMob>()
}

private inline fun <reified T : ConfigurationSerializable> registerSerializer() =
    ConfigurationSerialization.registerClass(T::class.java)