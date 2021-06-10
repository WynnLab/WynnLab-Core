package com.wynnlab.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

val emptyComponent: Component get() = Component.text("")

fun Component.serialize(char: Char = '§') = LegacyComponentSerializer.legacy(char).serialize(this)

fun colorNonItalic(color: Int): Style = Style.style { sb: Style.Builder ->
    sb.decoration(TextDecoration.ITALIC, false)
    sb.color(TextColor.color(color))
}
fun colorNonItalic(color: TextColor): Style = Style.style { sb: Style.Builder ->
    sb.decoration(TextDecoration.ITALIC, false)
    sb.color(color)
}