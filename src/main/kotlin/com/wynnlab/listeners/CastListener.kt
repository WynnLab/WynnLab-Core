package com.wynnlab.listeners

import com.wynnlab.WynnClass
import com.wynnlab.api.*
import com.wynnlab.classes
import com.wynnlab.classes.BaseClass
import com.wynnlab.classes.BasePlayerSpell
import com.wynnlab.events.SpellCastEvent
import com.wynnlab.spells.Spell
import com.wynnlab.spells.lifeSteal
import com.wynnlab.spells.manaSteal
import com.wynnlab.util.RefreshRunnable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import kotlin.math.ceil
import kotlin.math.floor

class CastListener : BaseListener() {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onSpellCast(e: SpellCastEvent) {
        val player = e.player
        val spellClass = player.getWynnClass()?.let { classes[it] } ?: return
        val spellId = e.spellId
        val spell = ((spellClass as? WynnClass)?.spells?.get(spellId) ?: (spellClass as BaseClass).spells[spellId](player))

        val cost = cost(player, spellId, (spell as? Spell)?.cost ?: (spell as BasePlayerSpell).cost)

        if (spellId > 0) {
            player.data.setInt("spell_cost_extra", if (player.data.getInt("last_spell") == spellId) (player.data.getInt("spell_cost_extra") ?: 0) + 1 else 0)
            player.data.setInt("last_spell", spellId)

            RefreshRunnable(player.data, "spell_cost_extra") {
                player.data.setInt("spell_cost_extra", 0)
                player.data.setInt("last_spell", 0)
            }.schedule(100L)
        }

        if (spellId > 0) {
            if (player.foodLevel > cost) {
                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, .5f)
                player.updateActionBar(
                    player.getLocalizedText("classes.${player.getWynnClass()}.spells.${if (player.isCloneClass) "${spellId}c" else spellId.toString()}")
                        .color(TextColor.color(0x75ebf0))
                    .append(Component.text(" Cast ", TextColor.color(0x9feaed)))
                    .append(Component.text("[",TextColor.color(0x23abb0)))
                    .append(Component.text(-cost, TextColor.color(0x23e1e8)))
                    .append(Component.text("✺ ", TextColor.color(0x2bd3d9)))
                    .append(Component.text("]",TextColor.color(0x23abb0)))
                    //"${} Cast " +
                    //        "§3[§b-${cost}✺§3]"
                )
                player.foodLevel -= cost
            } else {
                player.playSound(player.location, Sound.BLOCK_ANVIL_PLACE, 1f, 1f)
                player.updateActionBar(Component.text("Not enough mana!", NamedTextColor.DARK_RED))
                return
            }
        } else {
            if (player.cooldown()) return
        }

        if (player.removeScoreboardTag("life_steal"))
            lifeSteal(player.getId("life_steal"), player)
        if (player.removeScoreboardTag("mana_steal"))
            manaSteal(player.getId("mana_steal"), player)
        player.removeScoreboardTag("no_life_steal")
        player.removeScoreboardTag("no_mana_steal")
        player.removeScoreboardTag("no_exploding")

        (spell as? Spell)?.cast(player) ?: (spell as BasePlayerSpell).schedule()
    }

    private fun cost(player: Player, spellIndex: Int, cost: Int) =
        floor(ceil(cost * (1.0 - skillPercentage(player.getSkill(2)).let { if (player.hasScoreboardTag("pvp")) it.coerceAtMost(.55) else it }) + player.getId("spell_cost_raw_$spellIndex")) *
                (1.0 + player.getId("spell_cost_pct_$spellIndex") / 100.0) + (player.data.getInt("spell_cost_extra") ?: 0)).coerceAtLeast(1.0).toInt()
}