package com.wynnlab.locations

import com.wynnlab.api.getLocalizedText
import com.wynnlab.util.BaseSerializable
import com.wynnlab.util.ConfigurationDeserializable
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.bukkit.Location as _Location

class Location(
    val name: String,
    private val pos1: _Location,
    private val pos2: _Location,
    private val announce: Boolean,
    private val subtitle: String?
) : BaseSerializable<Location>() {
    override val deserializer = Companion

    fun announce(player: Player): Boolean {
        if (!announce) return false

        player.sendTitle(player.getLocalizedText("titles.location.enter", name),
            subtitle?.let { player.getLocalizedText(it) }, 10, 30, 10)

        return true
    }

    operator fun contains(l: _Location) = bb.contains(l.x, l.y, l.z)

    private val bb: BoundingBox = BoundingBox(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z)

    override fun serialize(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()

        map["name"] = name
        map["pos1"] = pos1
        map["pos2"] = pos2
        map["announce"] = announce
        subtitle?.let { map["subtitle"] = it }

        return map
    }

    companion object : ConfigurationDeserializable<Location> {
        @JvmStatic
        override fun deserialize(map: Map<String, Any?>): Location {
            val name = map["name"] as String
            val pos1 = map["pos1"] as _Location
            val pos2 = map["pos2"] as _Location
            val announce = (map["announce"] ?: false) as Boolean
            val subtitle = map["subtitle"] as String?

            return Location(name, pos1, pos2, announce, subtitle)
        }
    }

    override fun toString(): String {
        return "Location(name='$name', pos1=$pos1, pos2=$pos2, announce=$announce, subtitle=$subtitle)"
    }
}