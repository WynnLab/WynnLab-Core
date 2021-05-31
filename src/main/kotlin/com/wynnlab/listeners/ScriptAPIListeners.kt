package com.wynnlab.listeners

import com.wynnlab.events.DamageEvent
import com.wynnlab.events.HealEvent
import com.wynnlab.spells.damage
import com.wynnlab.spells.heal
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

class ScriptAPIListeners : BaseListener() {
    @EventHandler
    fun onDamage(e: DamageEvent) {
        if (e.attacker !is Player)
            return

        damage(e.attacker as Player, e.target, e.melee, e.multiplier, *e.conversion)
    }

    @EventHandler
    fun onHeal(e: HealEvent) {
        heal(e.target, e.amount)
    }
}