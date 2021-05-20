package com.wynnlab

import com.wynnlab.api.*
import com.wynnlab.locations.updateLocations
import com.wynnlab.spells.PySpell
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object MainThread : /*Runnable, */Listener {
    /*@Volatile
    var tick = 0

    override fun run() {
        val s1 = tick % 20 == 0
        val s4 = tick % 80 == 0

        for (player in Bukkit.getServer().onlinePlayers) {
            // Send action bar
            player.standardActionBar()

            // Reset exhaustion to not lose mana except in spell casts
            player.exhaustion = 0f

            // Store max health and set attribute
            val maxHealth = (505 + player.getId("health_bonus") + player.getArmorHealth()).coerceIn(1, 1000000).toDouble()
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = maxHealth

            val manaDrain = player.hasPotionEffect(PotionEffectType.INVISIBILITY)

            if (!manaDrain && "invis" in player.scoreboardTags)
                PySpell.castSpell(player, "ASSASSIN", 5)

            if (s1) {
                // Natural mana regen
                player.foodLevel = (player.foodLevel + if (manaDrain) -1 else 1).coerceIn(0, 20)

                // Jump height
                val jumpHeight = player.getId("jump_height")
                if (jumpHeight != 0) player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 21, jumpHeight - 1, true, false, false))

                // Test Inventory
                player.testInventory()

                // Sidebar
                // player.updateSidebar() // TODO: a lot of things
            }

            // Mana regen and health regen
            if (s4) {
                if (!manaDrain) player.foodLevel = (player.foodLevel + player.getId("mana_regen")).coerceIn(0, 20)
                player.health = (player.health + player.getId("health_regen_raw") * (1 + player.getId("health_regen") / 100f)).coerceIn(1.0, maxHealth)
            }

            // Walk speed
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = .1 + player.getId("speed") * .001
        }

        ++tick
    }*/

    fun schedule() {
        //Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L)
        Bukkit.getPluginManager().registerEvents(this, plugin)
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable { Bukkit.getPluginManager().callEvent(EveryTick) }, 1L, 1L)
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable { Bukkit.getPluginManager().callEvent(EverySecond) }, 20L, 20L)
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable { Bukkit.getPluginManager().callEvent(Every4Seconds) }, 80L, 80L)
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable { Bukkit.getPluginManager().callEvent(Every10Seconds) }, 200L, 200L)
    }

    @EventHandler
    fun onTick(e: EveryTick) {
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

    @EventHandler
    fun onSecond(e: EverySecond) {
        for (player in Bukkit.getOnlinePlayers()) {
            val pvp = player.hasScoreboardTag("pvp")

            // Natural mana regen
            player.foodLevel = (player.foodLevel + if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) -1 else if (pvp) 2 else 1).coerceIn(0, 20)

            // Jump height
            val jumpHeight = player.getId("jump_height") - 1
            if (jumpHeight != 0) player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 21,
                if (pvp) jumpHeight.coerceAtMost(3) else jumpHeight,
                true, false, false))

            // Test Inventory
            player.testInventory()

            // Locations
            player.updateLocations()

            // Sidebar
            // player.updateSidebar() // TODO: a lot of things
        }
    }

    @EventHandler
    fun on4Seconds(e: Every4Seconds) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) player.foodLevel = (player.foodLevel + player.getId("mana_regen")).coerceIn(0, 20)

            if (player.hasScoreboardTag("pvp"))
                continue
            healthRegen(player, false)
        }
    }

    @EventHandler
    fun on10Seconds(e: Every10Seconds) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.hasScoreboardTag("pvp"))
                continue
            healthRegen(player, true)
        }
    }

    private fun healthRegen(player: Player, pvp: Boolean) {
        val maxHealth = (505 + player.getId("health_bonus") + player.getArmorHealth()).coerceIn(1, 1000000).toDouble()
        val healthBonus = player.getId("health_regen_raw") * (1 + player.getId("health_regen") / 100f)
        player.health = (player.health + if (healthBonus > 2500) (healthBonus - 2500) * .15f + 2500 else healthBonus).coerceIn(1.0, maxHealth)
    }
}

object EveryTick : Event() {
    override fun getHandlers(): HandlerList = handlerList

    @JvmStatic
    val handlerList = HandlerList()
}

object EverySecond : Event() {
    override fun getHandlers(): HandlerList = handlerList

    @JvmStatic
    val handlerList = HandlerList()
}

object Every4Seconds : Event() {
    override fun getHandlers(): HandlerList = handlerList

    @JvmStatic
    val handlerList = HandlerList()
}

object Every10Seconds : Event() {
    override fun getHandlers(): HandlerList = handlerList

    @JvmStatic
    val handlerList = HandlerList()
}