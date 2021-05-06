package com.wynnlab.util

import org.bukkit.Location

class LocationIterator(
    val start: Location,
    val end: Location,
    val step: Double
) : Iterable<Location> {
    private val d = end.clone().subtract(start).toVector().normalize().multiply(step)

    override operator fun iterator(): Iterator<Location> =
        object : Iterator<Location> {
            private var l = start

            private var iStep = 0.0
            private val dist: Double = end.distance(start)

            override operator fun hasNext(): Boolean {
                return iStep <= dist
            }

            override operator fun next(): Location {
                val r = l
                l.add(d)
                iStep += step
                return r
            }
        }
}