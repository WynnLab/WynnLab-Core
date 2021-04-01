package com.wynnlab

import com.wynnlab.spells.Spell
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.io.File
import java.util.logging.Level

data class WynnClass(
    val className: String,
    val cloneName: String,
    val invertedControls: Boolean,
    val spells: List<Spell>
) : ConfigurationSerializable {

    override fun serialize(): Map<String, Any> {
        val out = LinkedHashMap<String, Any>()

        out["className"] = className
        out["cloneName"] = cloneName
        out["invertedControls"] = invertedControls
        out["spells"] = spells.map { it.serialize() }

        return out
    }

    companion object {
        @JvmStatic
        @Suppress("unused", "unchecked_cast")
        fun deserialize(map: Map<String, Any>): WynnClass {
            val className = map["className"] as String
            val cloneName = map["cloneName"] as String
            val invertedControls = map["invertedControls"] as Boolean
            val spells = map["spells"] as List<Spell>

            return WynnClass(className, cloneName, invertedControls, spells)
        }

        operator fun get(string: String) = classes[string]
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