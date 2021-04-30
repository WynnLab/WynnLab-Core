package com.wynnlab.items

import com.wynnlab.api.metaAs
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta

enum class SpecialItems(val itemStack: ItemStack) {
    HealPotion(ItemStack(Material.POTION).metaAs<PotionMeta> {
    })
}