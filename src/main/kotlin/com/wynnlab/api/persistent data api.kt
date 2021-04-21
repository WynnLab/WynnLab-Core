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

fun PersistentDataContainer.getIntArray(key: String) = this[key, PersistentDataType.INTEGER_ARRAY]

fun PersistentDataContainer.getContainer(key: String) = this[key, PersistentDataType.TAG_CONTAINER]

fun PersistentDataContainer.getContainerArray(key: String) = this[key, PersistentDataType.TAG_CONTAINER_ARRAY]

fun PersistentDataContainer.getBoolean(key: String) = this[key, PersistentDataType.BYTE] == 1.toByte()


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

fun PersistentDataContainer.setBoolean(key: String, value: Boolean) = set(key, PersistentDataType.BYTE, if (value) 1.toByte() else 0.toByte())


fun PersistentDataContainer.remove(key: String) = remove(NamespacedKey(plugin, key))