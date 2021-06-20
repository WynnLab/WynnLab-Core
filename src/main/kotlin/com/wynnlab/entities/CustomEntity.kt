package com.wynnlab.entities

import net.minecraft.world.entity.Entity
import org.bukkit.World
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent

interface CustomEntity<BukkitType : org.bukkit.entity.Entity> {
    fun spawn(world: World) {
        (world as CraftWorld).addEntity<BukkitType>(this as Entity, CreatureSpawnEvent.SpawnReason.CUSTOM)
    }
}