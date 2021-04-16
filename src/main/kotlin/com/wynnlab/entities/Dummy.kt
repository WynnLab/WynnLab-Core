package com.wynnlab.entities

import net.minecraft.server.v1_16_R3.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.entity.Vindicator

class Dummy(location: Location) : EntityVindicator(EntityTypes.VINDICATOR, (location.world as CraftWorld).handle), CustomEntity<Vindicator> {
    init {
        setLocation(location.x, location.y, location.z, location.yaw, location.pitch)

        collides = false

        customName = ChatComponentText("Dummy")
        customNameVisible = true

        getAttributeInstance(GenericAttributes.MAX_HEALTH)!!.value = 100000.0
        health = 100000f
    }

    override fun initPathfinder() {
        goalSelector.a(0, PathfinderGoalFloat(this))
    }
}