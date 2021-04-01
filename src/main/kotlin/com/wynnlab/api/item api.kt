package com.wynnlab.api

import com.wynnlab.items.WynnItem
import org.bukkit.inventory.ItemStack

fun ItemStack.getWynnType() = itemMeta.data.getString("type")?.let { WynnItem.Type.valueOf(it) }

fun ItemStack.getClassReq() = itemMeta.data.getString("class_req")

fun ItemStack.getAttackSpeed() = itemMeta.data.getString("attack_speed")?.let { WynnItem.AttackSpeed.valueOf(it) }