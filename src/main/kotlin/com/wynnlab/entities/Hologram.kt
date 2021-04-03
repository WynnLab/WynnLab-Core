package com.wynnlab.entities

import org.bukkit.Location

class Hologram(location: Location, text: String) {
    private val handle = EntityArmorStand.getConstructor(EntityTypes, World).newInstance(
        EntityTypes.getField("ARMOR_STAND")[null],
        CraftWorld.getMethod("getHandle")(CraftWorld.cast(location.world))
    )

    init {
        EntityArmorStand.getMethod("setInvisible")(handle, true)
        EntityArmorStand.getMethod("setInvulnerable")(handle, true)
        EntityArmorStand.getField("collides")[handle] = false
        EntityArmorStand.getMethod("setNoGravity")(handle, true)
        EntityArmorStand.getMethod("setSmall")(handle, true)

        EntityArmorStand.getMethod("setLocation")(handle, location.x, location.y, location.z, location.yaw, location.pitch)

        EntityArmorStand.getMethod("setCustomName")(handle, ChatComponentText.getConstructor(String::class.java).newInstance(text))
        EntityArmorStand.getMethod("setCustomNameVisible")(handle, true)
    }

    fun remove() {
        EntityArmorStand.getMethod("die")(handle)
    }
}