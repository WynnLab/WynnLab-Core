package com.wynnlab.essentials

import com.wynnlab.PREFIX
import com.wynnlab.api.sendWynnMessage
import com.wynnlab.api.updateSidebar
import com.wynnlab.localization.Language
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.entity.Player

data class Party(
    var owner: Player,
    val members: MutableList<Player> = mutableListOf()
) {

    fun addMember(player: Player) {
        this.members.forEach {
            it.sendWynnMessage("messages.party_joined", player.name)
        }

        this.members.add(player)
        Party.members[player] = this

        player.sendWynnMessage("messages.party_joined_you")
    }

    fun removeMember(player: Player) {
        player.sendWynnMessage("messages.party_left_you")

        this.members.remove(player)
        Party.members.remove(player)

        this.members.forEach {
            it.sendWynnMessage("messages.party_left", player.name)
        }

        if (members.isEmpty()) {
            parties.remove(this)
            return
        }

        if (player == owner) {
            promote(this.members[0])
        }
    }

    fun promote(player: Player) {
        player.sendWynnMessage("messages.party_promote")

        owner = player
    }

    @Suppress("unchecked_cast")
    fun invite(player: Player) {
        invites[player] = this

        val language = Language[player.locale]

        player.sendMessage("$PREFIX${language.getMessage("messages.party_invite_get", owner.name)}")

        player.sendMessage(
            Component.text("$PREFIX${language.getMessage("messages.party_join_left")}")
                .append(Component.text(language.getMessage("messages.party_join_link"))
                    .hoverEvent { HoverEvent.showText(
                        Component.text(language.getMessage("messages.party_join_hover"))
                    ) as HoverEvent<Any> }
                    .clickEvent(ClickEvent.runCommand("/party join")))
                .append(Component.text(language.getMessage("messages.party_join_right")))
        )

        owner.sendWynnMessage("messages.party_invite_send", player.name)
    }

    companion object {
        val parties = mutableListOf<Party>()

        val members = mutableMapOf<Player, Party>()

        val invites = mutableMapOf<Player, Party>()
    }
}