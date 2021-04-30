package com.wynnlab.listeners

import com.wynnlab.api.data
import com.wynnlab.api.getString
import com.wynnlab.api.setString
import com.wynnlab.plugin
import com.wynnlab.util.RefreshRunnable
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

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

        //if (entity.data.getString("old_name") != null)
        //    return

        val oldName = entity.data.getString("old_name") ?: entity.data.getString("name") ?:
            entity.customName?.takeIf { entity.isCustomNameVisible } ?:
            entity.name

        entity.data.setString("old_name", oldName)

        entity.customName = newName
        entity.isCustomNameVisible = true

        // BossBar

        if (entity.health < e.damage)
            return

       /*val tag = "boss_bar_${entity.entityId}"
        if (tag in player.scoreboardTags)
            Bukkit.bo
        player.addScoreboardTag(tag)*/
        val bbKey = NamespacedKey(plugin, "damage_${entity.entityId}")
        val currentBB = Bukkit.getBossBar(bbKey)
        currentBB?.apply {
            setTitle("$oldName§r§f - §4${currentHealth.toInt()}§c❤")

            if (entity.isDead) {
                removeAll()
                Bukkit.removeBossBar(bbKey)
                return
            }
        }

        val bossBar = currentBB ?: Bukkit.createBossBar(
            bbKey,
            "$oldName§r§f - §4${currentHealth.toInt()}§c❤",
            BarColor.RED, BarStyle.SEGMENTED_10
        )

        bossBar.progress = percentage

        bossBar.addPlayer(player)

        /*val bossBar = BossBar.bossBar(
            Component.text(oldName)
                .append(Component.text(" - ", NamedTextColor.WHITE))
                .append(Component.text(currentHealth.toInt(), NamedTextColor.DARK_RED))
                .append(Component.text("❤", NamedTextColor.RED)),
                //.append(), TODO: Damage & Defense
            percentage.toFloat(),
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS,
            setOf()
        )*/

        RefreshRunnable(entity.data, "damage_${entity.entityId}") {
            bossBar.removePlayer(player)
            entity.customName = oldName
        }.schedule(100)
    }

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        if (e.entity is Player)
            return

        val damageKey = NamespacedKey(plugin, "damage_${e.entity.entityId}")
        Bukkit.getBossBar(damageKey)?.removeAll()
        Bukkit.removeBossBar(damageKey)

        val prepareKey = NamespacedKey(plugin, "prepare_${e.entity.entityId}")
        (Bukkit.getBossBar(prepareKey) ?: return).removeAll()
        Bukkit.removeBossBar(prepareKey)
    }
}