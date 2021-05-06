package com.wynnlab.listeners

import com.wynnlab.Players
import com.wynnlab.api.*
import com.wynnlab.commands.EssentialsCommands
import com.wynnlab.essentials.Party
import com.wynnlab.spells.PySpell
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta

class PlayerEventsListener : BaseListener() {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player

        Players.preparePlayer(player)
        e.joinMessage = "§7[§a+§7]§r ${player.prefix}${player.name}"
        //e.player.sendMessage("Locale: ${e.player.locale}")
        player.getWynnClass()?.let { c ->
            player.sendWynnMessage("messages.current_class", player.getLocalizedText("classes.$c.${if (player.isCloneClass) "cloneName" else "className"}"))
            player.sendWynnMessage("messages.class_change")
        } ?: run {
            player.sendWynnMessage("messages.no_class")
            player.sendWynnMessage("messages.class_select")
            player.performCommand("wynnlab:class")
        }
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        val player = e.player

        e.quitMessage = "§7[§c-§7]§r ${player.prefix}${player.name}"
        prefixes.remove(player)

        // Remove player from activities
        EssentialsCommands.conversations.remove(player)
        Party.invites.remove(player)
        Party.members[player]?.removeMember(player)
    }

    @EventHandler
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        e.format = "%s: §r%s"
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        e.keepInventory = true
        e.keepLevel = true
        e.setShouldDropExperience(false)
        e.droppedExp = 0

        e.deathMessage = deathMessages.random().replace("$", e.entity.name)
    }

    @EventHandler
    fun onPlayerToggleSneak(e: PlayerToggleSneakEvent) {
        val player = e.player
        if (e.isSneaking) {
            if (player.isGliding) {
                player.boostElytra(fireworks)

                PySpell.particle(player, player.location, Particle.CLOUD, 10, .5, .5, .5, .5)
                PySpell.particle(player, player.location, Particle.SQUID_INK, 10, .5, .5, .5, .5)
                PySpell.particle(player, player.location, Particle.LAVA, 10, .5, .5, .5, .5)

                PySpell.sound(player, Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK , .3f, .6f)
                PySpell.sound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .8f, 1f)
                PySpell.sound(player, Sound.ENTITY_BLAZE_SHOOT, 1f, 1.1f)
            }
        }
    }

    companion object {
        val deathMessages = setOf("$ didn't know about RRR")

        val fireworks = ItemStack(Material.FIREWORK_ROCKET).metaAs<FireworkMeta> {
            power = 2
        }
    }
}