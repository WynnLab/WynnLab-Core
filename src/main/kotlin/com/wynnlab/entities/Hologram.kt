package com.wynnlab.entities

import com.wynnlab.plugin
import net.minecraft.server.v1_16_R3.ChatComponentText
import net.minecraft.server.v1_16_R3.EntityArmorStand
import net.minecraft.server.v1_16_R3.EntityTypes
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.entity.ArmorStand
import org.bukkit.event.entity.CreatureSpawnEvent

class Hologram(location: Location, text: String) : EntityArmorStand(EntityTypes.ARMOR_STAND, (location.world as CraftWorld).handle), CustomEntity<ArmorStand> {
    init {
        isInvisible = true
        isInvulnerable = true
        collides = false
        isNoGravity = true
        isSmall = true

        setLocation(location.x, location.y, location.z, location.yaw, location.pitch)

        customName = ChatComponentText(text)
        customNameVisible = true
    }

    fun removeAfter(time: Long) = Bukkit.getScheduler().runTaskLater(plugin, ::remove, time)

    fun remove() {
        die()
    }
}