package com.wynnlab.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class SpellCastEvent(val player: Player, val spellId: Int) : Event() {
    override fun getHandlers(): HandlerList = SpellCastEvent.handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}