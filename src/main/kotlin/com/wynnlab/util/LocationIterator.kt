package com.wynnlab.util

import org.bukkit.Location
import org.bukkit.util.Vector

class LocationIterator(
    val start: Location,
    val end: Location,
    val direction: Vector,
    val step: Double
) : Iterable<Location> {
    override operator fun iterator(): Iterator<Location> =
        object : Iterator<Location> {
            private var iStep = 0.0
            private val dist: Double = end.distance(start)

            override operator fun hasNext(): Boolean {
                return iStep < dist
            }

            override operator fun next(): Location {
                val add: Location = start.clone().add(direction.clone().multiply(iStep))
                iStep += step
                return add
            }
        }
}