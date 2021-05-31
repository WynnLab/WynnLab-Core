package com.wynnlab.items

import com.wynnlab.NL_REGEX
import com.wynnlab.api.*
import com.wynnlab.listeners.PlayerClickListener
import com.wynnlab.spells.PySpell
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta

enum class SpecialItems(val itemStack: (Player) -> ItemStack, private val rightClick: ((Player, ItemStack) -> Unit)?) {
    HealPotion ({ p ->
        ItemStack(Material.POTION).metaAs<PotionMeta> {
        addItemFlags(*ItemFlag.values())

        color = Color.FUCHSIA

        setDisplayName(p.getLocalizedText("items.heal_potion.title", 3))

        val lore = mutableListOf(" ",
            p.getLocalizedText("items.heal_potion.effects"),
            p.getLocalizedText("items.heal_potion.heal", 24),
            p.getLocalizedText("items.heal_potion.duration"),
            " ",
            p.getLocalizedText("items.items.combat_lv_min", "§a✔", 5),
            " "
        )

        lore.addAll(p.getLocalizedText("items.heal_potion.lore").split(NL_REGEX))

        this.lore = lore

        data.setString("type", "HealPotion")
        data.setInt("healing", 24)

    }}, { p, i ->
        i.itemMeta.data.getInt("healing")?.toDouble()?.let { PySpell.heal(p, it) }
    });

    init {
        if (rightClick != null)
            PlayerClickListener.rcEvents[name] = { e -> e.item?.let { rightClick.invoke(e.player, it) } }
    }
}