package com.wynnlab

const val COLOR_HEALTH_VALUE = 0xe82738

const val COLOR_DISCORD = 0x5865f2

inline val Int.color get() = net.kyori.adventure.text.format.TextColor.color(this)