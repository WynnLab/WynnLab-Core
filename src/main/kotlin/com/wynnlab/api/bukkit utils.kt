package com.wynnlab.api

import org.bukkit.util.Vector

fun Vector.normalizeOnXZ() = if (x == 0.0 && y == 0.0) {
    z = 1.0; this
} else normalize()