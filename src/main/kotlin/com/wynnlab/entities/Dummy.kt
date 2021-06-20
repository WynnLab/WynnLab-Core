package com.wynnlab.entities

import net.minecraft.network.chat.ChatComponentText
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.ai.attributes.GenericAttributes
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat
import net.minecraft.world.entity.monster.EntityVindicator
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.entity.Vindicator

class Dummy(location: Location) : EntityVindicator(EntityTypes.aW, (location.world as CraftWorld).handle), CustomEntity<Vindicator> {
    init {
        setLocation(location.x, location.y, location.z, location.yaw, location.pitch)

        //collides = false

        customName = ChatComponentText("Dummy")
        customNameVisible = true

        getAttributeInstance(GenericAttributes.a)!!.value = 100000.0
        health = 100000f
    }

    override fun initPathfinder() {
        bO.a(0, PathfinderGoalFloat(this))
    }
}