package com.wynnlab.api

import com.wynnlab.plugin
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

inline val PersistentDataHolder.data get() = persistentDataContainer

operator fun <T, Z> PersistentDataContainer.get(key: String, type: PersistentDataType<T, Z>) =
    this[NamespacedKey(plugin, key), type]

fun PersistentDataContainer.getString(key: String) = this[key, PersistentDataType.STRING]

fun PersistentDataContainer.getInt(key: String) = this[key, PersistentDataType.INTEGER]


operator fun <T, Z> PersistentDataContainer.set(key: String, type: PersistentDataType<T, Z>, value: Z) =
    set(NamespacedKey(plugin, key), type, value!!)

fun PersistentDataContainer.setString(key: String, value: String) = set(key, PersistentDataType.STRING, value)

fun PersistentDataContainer.setInt(key: String, value: Int) = set(key, PersistentDataType.INTEGER, value)