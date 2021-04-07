@file:JvmName("PersistentDataAPI")

package com.wynnlab.api

import com.wynnlab.plugin
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

inline val PersistentDataHolder.data get() = persistentDataContainer

operator fun <T, Z> PersistentDataContainer.get(key: String, type: PersistentDataType<T, Z>) =
    try { this[NamespacedKey(plugin, key), type] } catch (e: IllegalArgumentException) { null }

fun PersistentDataContainer.getString(key: String) = this[key, PersistentDataType.STRING]

fun PersistentDataContainer.getInt(key: String) = this[key, PersistentDataType.INTEGER]


operator fun <T, Z> PersistentDataContainer.set(key: String, type: PersistentDataType<T, Z>, value: Z) =
    set(NamespacedKey(plugin, key), type, value!!)

fun PersistentDataContainer.setString(key: String, value: String) = set(key, PersistentDataType.STRING, value)

fun PersistentDataContainer.setInt(key: String, value: Int) = set(key, PersistentDataType.INTEGER, value)

fun PersistentDataContainer.setIntArray(key: String, value: IntArray) = set(key, PersistentDataType.INTEGER_ARRAY, value)

fun PersistentDataContainer.setContainer(key: String, value: PersistentDataContainer.() -> Unit) {
    val container = adapterContext.newPersistentDataContainer()
    container.value()
    set(key, PersistentDataType.TAG_CONTAINER, container)
}

fun persistentDataTypeFromString(string: String) = when (string) {
    "byte" -> PersistentDataType.BYTE
    "short" -> PersistentDataType.SHORT
    "int" -> PersistentDataType.INTEGER
    "long" -> PersistentDataType.LONG
    "float" -> PersistentDataType.FLOAT
    "double" -> PersistentDataType.DOUBLE
    "byte[]" -> PersistentDataType.BYTE_ARRAY
    "int[]" -> PersistentDataType.INTEGER_ARRAY
    "long[]" -> PersistentDataType.LONG_ARRAY
    "String" -> PersistentDataType.STRING
    "{}" -> PersistentDataType.TAG_CONTAINER
    "{}[]" -> PersistentDataType.TAG_CONTAINER_ARRAY
    else -> null
}