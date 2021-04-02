package com.wynnlab.util

import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.PI

const val RAD2DEG = 180.0 / PI
const val DEG2RAD = PI / 180.0

operator fun Location.plus(l: Location) = clone().add(l)
operator fun Location.plus(l: Vector) = clone().add(l)
fun Location.plus(x: Double, y: Double, z: Double) = clone().add(x, y, z)

fun Location.minus(x: Double, y: Double, z: Double) = clone().subtract(x, y, z)


operator fun Vector.plus(v: Vector) = clone().add(v)
operator fun Vector.plusAssign(v: Vector) { add(v) }

operator fun Vector.minus(v: Vector) = clone().subtract(v)

operator fun Vector.times(x: Double) = clone().multiply(x)
operator fun Vector.times(v: Vector) = clone().multiply(x)