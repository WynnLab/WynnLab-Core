package com.wynnlab.entities

import org.bukkit.World
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent

interface CustomEntity<BukkitType : org.bukkit.entity.Entity> {
    fun spawn(world: World) {
        (world as CraftWorld).addEntity<BukkitType>(this as net.minecraft.server.v1_16_R3.Entity, CreatureSpawnEvent.SpawnReason.CUSTOM)
    }
}