package com.wynnlab.listeners

import com.wynnlab.spells.PySpell
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageListener : Listener {
    @EventHandler
    fun onEntityDamageEntity(e: EntityDamageByEntityEvent) {
        e.isCancelled = true

        if (e.entity is Player) {
            e.isCancelled = false
            return
        }

        if (e.damager !is Player)
            return

        if (e.entity !is LivingEntity)
            return

        val entity = e.entity as LivingEntity
        val player = e.damager as Player

        entity.damage(e.damage)

        val currentHealth = entity.health
        val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value

        val percentage = currentHealth / maxHealth

        val newName = StringBuilder().append("§4[").append(if (percentage < 0.0833) "§8|" else "§c|")
            .append(if (percentage < 0.25) "§8|" else "|").append(if (percentage < 0.4167) "§8|" else "|").append("§4")
            .append(currentHealth.toInt()).append(if (percentage < 0.5833) "§8|" else "§c|").append(if (percentage < 0.75) "§8|" else "|")
            .append(if (percentage < 9167) "§8|" else "|").append("§4]").toString()

        entity.customName = newName
        entity.isCustomNameVisible = true
    }
}