@file:JvmName("ItemAPI")

package com.wynnlab.api

import com.wynnlab.items.WynnItem
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

inline fun ItemStack.meta(edit: ItemMeta.() -> Unit): ItemStack {
    val meta = itemMeta
    meta.edit()
    itemMeta = meta
    return this
}

//BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER
inline fun <reified T> ItemStack.metaAs(edit: T.() -> Unit): ItemStack {
    val meta = itemMeta as? T ?: return this
    meta.edit()
    itemMeta = meta as ItemMeta
    return this
}

fun ItemStack.setAppearance(damage: Int): ItemStack {
    val meta = itemMeta
    (meta as Damageable).damage = damage
    meta.isUnbreakable = true
    itemMeta = meta
    return this
}

fun ItemStack.getWynnType() = itemMeta?.data?.getString("type")?.let { WynnItem.Type.valueOf(it) }
fun ItemStack.takeIfType(type: WynnItem.Type) = takeIf { it.getWynnType() == type }

fun ItemStack.getClassReq() = itemMeta?.data?.getString("class_req")

fun ItemStack.getAttackSpeed() = itemMeta?.data?.getString("attack_speed")?.let { WynnItem.AttackSpeed.valueOf(it) }