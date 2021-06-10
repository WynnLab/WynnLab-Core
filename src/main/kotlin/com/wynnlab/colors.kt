package com.wynnlab

val COLOR_ORANGE = 0xeb9234.color
val COLOR_DARKER_GREY = 0x666666.color
val COLOR_GOLD = 0xedd953.color

val COLOR_DES_RED = 0xaf6666.color
val COLOR_DES_GREEN = 0x66af66.color
val COLOR_DES_BLUE = 0x6666af.color

val COLOR_HEALTH_VALUE = 0xe82738.color

val COLOR_QUESTS = 0x993ce6.color
val COLOR_QUESTS_COUNT = 0xc13ce6.color
val COLOR_QUESTS_BRACKET = 0x732dad.color
/*
append(Component.text("0/0", TextColor.color(0xc13ce6)))
append(Component.text(" [", TextColor.color(0x732dad)))
append(Component.text("100%", TextColor.color(0x993ce6)))
append(Component.text("]", TextColor.color(0x732dad)))
 */

val COLOR_DISCORD = 0x5865f2.color

inline val Int.color get() = net.kyori.adventure.text.format.TextColor.color(this)