package com.wynnlab.essentials

import com.wynnlab.PREFIX
import com.wynnlab.api.sendWynnMessage
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
            it.sendWynnMessage("messages.party.joined", player.name)
        }

        this.members.add(player)
        Party.members[player] = this

        player.sendWynnMessage("messages.party.joined_you")
    }

    fun removeMember(player: Player) {
        player.sendWynnMessage("messages.party.left_you")

        this.members.remove(player)
        Party.members.remove(player)

        this.members.forEach {
            it.sendWynnMessage("messages.party.left", player.name)
        }

        if (members.isEmpty()) {
            parties.remove(this)
            return
        }

        if (player == owner) {
            promote(this.members[0])
        }
    }

    private fun promote(player: Player) {
        player.sendWynnMessage("messages.party.promote")

        owner = player
    }

    @Suppress("unchecked_cast")
    fun invite(player: Player) {
        invites[player] = this

        val language = Language[player.locale()]

        player.sendMessage("$PREFIX${language.getMessage("messages.party.invite.get", owner.name)}")

        player.sendMessage(
            Component.text("$PREFIX${language.getMessage("messages.party.join.left")}")
                .append(language.getMessage("messages.party.join.link")
                    .hoverEvent { HoverEvent.showText(
                        language.getMessage("messages.party.join.hover")
                    ) as HoverEvent<Any> }
                    .clickEvent(ClickEvent.runCommand("/party join")))
                .append(language.getMessage("messages.party.join.right"))
        )

        owner.sendWynnMessage("messages.party.invite.send", player.name)
    }

    companion object {
        val parties = mutableListOf<Party>()

        val members = mutableMapOf<Player, Party>()

        val invites = mutableMapOf<Player, Party>()
    }
}