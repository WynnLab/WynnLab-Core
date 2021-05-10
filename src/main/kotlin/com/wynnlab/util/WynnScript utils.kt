package com.wynnlab.util

import com.wynnlab.spells.SpellUtils
import com.wynnlab.wynnscript.CompiledWynnScript
import com.wynnlab.wynnscript.Invocable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound

fun prepareScript(script: CompiledWynnScript) {
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