package com.wynnlab

import com.wynnlab.api.*
import com.wynnlab.essentials.Party
import com.wynnlab.localization.Language
import com.wynnlab.locations.updateLocations
import com.wynnlab.spells.PySpell
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object MainThread : Listener {
    fun schedule() {
        Bukkit.getScheduler().runTaskTimer(wynnlab, { -> onTick() }, 1L, 1L)
        Bukkit.getScheduler().runTaskTimer(wynnlab, { -> onSecond() }, 20L, 20L)
        Bukkit.getScheduler().runTaskTimer(wynnlab, { -> on4Seconds() }, 80L, 80L)
        Bukkit.getScheduler().runTaskTimer(wynnlab, { -> on10Seconds() }, 200L, 200L)
        Bukkit.getScheduler().runTaskTimer(wynnlab, { -> on5Minutes() }, 100L, 6000L)
    }

    private fun onTick() {
        for (player in Bukkit.getOnlinePlayers()) {
            val pvp = player.hasScoreboardTag("pvp")

            // Send action bar
            player.standardActionBar()

            // Reset exhaustion to not lose mana except in spell casts
            player.exhaustion = 0f

            // Store max health and set attribute
            val maxHealth = (505 + player.getId("health_bonus") + player.getArmorHealth()).coerceIn(1, 1000000).toDouble()
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = maxHealth

            if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY) && "invis" in player.scoreboardTags)
                PySpell.castSpell(player, "ASSASSIN", 5)

            // Walk speed
            val speed = player.getId("speed")
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = .1 + (if (pvp) speed.coerceAtMost(100) else speed) * .001
        }
    }

    private fun onSecond() {
        Party.parties.forEach(Party::update)

        for (player in Bukkit.getOnlinePlayers()) {
            val pvp = player.hasScoreboardTag("pvp")

            // Natural mana regen
            player.foodLevel = (player.foodLevel + if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) -1 else if (pvp) 2 else 1).coerceIn(0, 20)

            val jumpHeight = player.getId("jump_height") - 1
            if (jumpHeight != 0) player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 21,
                if (pvp) jumpHeight.coerceAtMost(3) else jumpHeight,
                true, false, false))

            player.testInventory()
            player.updateLocations()
            player.updateSidebar()
        }
    }

    private fun on4Seconds() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) player.foodLevel = (player.foodLevel + player.getId("mana_regen")).coerceIn(0, 20)

            if (player.hasScoreboardTag("pvp"))
                continue
            healthRegen(player, false)
        }
    }

    private fun on10Seconds() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.hasScoreboardTag("pvp"))
                continue
            healthRegen(player, true)
        }
    }

    private fun on5Minutes() {
        for (player in Bukkit.getOnlinePlayers()) {
            val msg = Language[player.locale()].getRandomMessageAsString("tips")
            var c = LegacyComponentSerializer.legacy('&').deserialize(msg)
            if (msg.startsWith("&#5865f2"))
                c = c.append(Component.text("https://discord.gg/7ktHKn2nZG", COLOR_DISCORD)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to join!", NamedTextColor.GRAY)))
                    .clickEvent(ClickEvent.openUrl("https://discord.gg/7ktHKn2nZG")))
            player.sendMessage(c)
        }
    }

    private fun healthRegen(player: Player, pvp: Boolean) {
        val maxHealth = (505 + player.getId("health_bonus") + player.getArmorHealth()).coerceIn(1, 1000000).toDouble()
        val healthBonus = player.getId("health_regen_raw") * (1 + player.getId("health_regen") / 100f)
        player.health = (player.health + if (pvp && healthBonus > 2500) (healthBonus - 2500) * .15f + 2500 else healthBonus).coerceIn(1.0, maxHealth)
    }
}