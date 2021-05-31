@file:JvmName("PersistentDataAPI")

package com.wynnlab.api

import com.wynnlab.wynnlab
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

inline val PersistentDataHolder.data get() = persistentDataContainer

operator fun <T, Z> PersistentDataContainer.get(key: String, type: PersistentDataType<T, Z>) =
    try { this[NamespacedKey(wynnlab, key), type] } catch (e: IllegalArgumentException) { null }

fun PersistentDataContainer.getString(key: String, default: String? = null) = this[key, PersistentDataType.STRING] ?: default

fun PersistentDataContainer.getInt(key: String, default: Int? = null) = this[key, PersistentDataType.INTEGER] ?: default

fun PersistentDataContainer.getIntArray(key: String, default: IntArray? = null) = this[key, PersistentDataType.INTEGER_ARRAY] ?: default

fun PersistentDataContainer.getContainer(key: String, default: PersistentDataContainer? = null) = this[key, PersistentDataType.TAG_CONTAINER] ?: default

fun PersistentDataContainer.getContainerArray(key: String, default: Array<PersistentDataContainer>? = null): Array<PersistentDataContainer>? = this[key, PersistentDataType.TAG_CONTAINER_ARRAY] ?: default

fun PersistentDataContainer.getBoolean(key: String, default: Boolean = false): Boolean { return (this[key, PersistentDataType.BYTE] ?: return default) != 0.toByte() }

fun PersistentDataContainer.getDouble(key: String, default: Double? = null) = this[key, PersistentDataType.DOUBLE] ?: default


operator fun <T, Z> PersistentDataContainer.set(key: String, type: PersistentDataType<T, Z>, value: Z) =
    set(NamespacedKey(wynnlab, key), type, value!!)

fun PersistentDataContainer.setString(key: String, value: String) = set(key, PersistentDataType.STRING, value)

fun PersistentDataContainer.setInt(key: String, value: Int) = set(key, PersistentDataType.INTEGER, value)

fun PersistentDataContainer.setIntArray(key: String, value: IntArray) = set(key, PersistentDataType.INTEGER_ARRAY, value)

fun PersistentDataContainer.setContainer(key: String, value: PersistentDataContainer.() -> Unit) {
    val container = adapterContext.newPersistentDataContainer()
    container.value()
    set(key, PersistentDataType.TAG_CONTAINER, container)
}

fun PersistentDataContainer.setBoolean(key: String, value: Boolean) = set(key, PersistentDataType.BYTE, if (value) 1.toByte() else 0.toByte())

fun PersistentDataContainer.setDouble(key: String, value: Double) = set(key, PersistentDataType.DOUBLE, value)


fun PersistentDataContainer.remove(key: String) = remove(NamespacedKey(wynnlab, key))