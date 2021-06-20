package com.wynnlab.entities

import com.wynnlab.wynnlab
import net.minecraft.network.chat.ChatComponentText
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.decoration.EntityArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.entity.ArmorStand

class Hologram(location: Location, text: String) : EntityArmorStand(EntityTypes.c, (location.world as CraftWorld).handle), CustomEntity<ArmorStand> {
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

    fun removeAfter(time: Long) = Bukkit.getScheduler().runTaskLater(wynnlab, ::remove, time)

    fun remove() {
        die()
    }
}