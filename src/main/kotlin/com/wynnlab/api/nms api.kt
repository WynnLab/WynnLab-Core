package com.wynnlab.api

import org.bukkit.World
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld

val World.nms get() = (this as CraftWorld).handle