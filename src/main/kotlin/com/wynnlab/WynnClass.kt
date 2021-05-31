@file:JvmName("Classes")

package com.wynnlab

import com.wynnlab.registry.ClassRegistry
import com.wynnlab.spells.Spell
import com.wynnlab.util.BaseSerializable
import com.wynnlab.util.ConfigurationDeserializable
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Level

data class WynnClass(
    val id: String,
    val item: Material,
    val itemDamage: Int,
    val metaStats: Tuple4<Int>,
    val invertedControls: Boolean,
    val spells: List<Spell>
) : BaseSerializable<WynnClass>() {

    override fun serialize(): Map<String, Any> {
        val out = LinkedHashMap<String, Any>()

        out["id"] = id
        out["item"] = item.name
        if (itemDamage != 0) out["item_damage"] = itemDamage
        out["metaStats"] = mapOf("damage" to metaStats.v1, "defence" to metaStats.v2, "range" to metaStats.v3, "spells" to metaStats.v4)
        out["invertedControls"] = invertedControls
        out["spells"] = spells

        return out
    }

    override val deserializer = Companion

    companion object : ConfigurationDeserializable<WynnClass> {
        @JvmStatic
        @Suppress("unused", "unchecked_cast")
        override fun deserialize(map: Map<String, Any?>): WynnClass {
            val id = map["id"] as String
            val item = Material.valueOf(map["item"] as String)
            val itemDamage = (map["item_damage"] as Number??: 0).toInt()
            val metaStats = map["metaStats"] as Map<String, Number>
            val invertedControls = map["invertedControls"] as Boolean
            val spells = map["spells"] as List<Spell>

            spellOrdinal = 0

            return WynnClass(id, item, itemDamage,
                Tuple4(metaStats["damage"]!!.toInt(), metaStats["defence"]!!.toInt(), metaStats["range"]!!.toInt(), metaStats["spells"]!!.toInt()),
                invertedControls, spells)
        }

        operator fun get(string: String) = classes[string]
    }
}

val classes = linkedMapOf<String, Any>()

internal var spellOrdinal = 0

fun loadClasses() {
    val classFolder = File(wynnlab.dataFolder, "classes")

    if (!classFolder.exists()) {
        wynnlab.logger.log(Level.WARNING, "No classes loaded")
        return
    }

    for (f in classFolder.listFiles { f, _ -> f.isDirectory } ?: return) {
        currentClassLoadFolder = f

        wynnlab.logger.log(Level.INFO, "Loading class ${f.name} ...")

        val configFile = File(f, "${f.name}.yml")
        val config = YamlConfiguration()
        config.load(configFile)
        val wynnClass = config.getSerializable("class", WynnClass::class.java) ?: continue
        classes[wynnClass.id] = wynnClass
    }

    classes.remove("MONK")?.let { classes["MONK"] = it }

    ClassRegistry.entries.forEach {
        classes[it.id] = it
    }

    //plugin.logger.log(Level.INFO, "Classes: $classes")
    //plugin.logger.log(Level.INFO, "Listeners: ${plugin.projectileHitListener.tags}")
}

data class Tuple4<T>(val v1: T, val v2: T, val v3: T, val v4: T)

internal lateinit var currentClassLoadFolder: File