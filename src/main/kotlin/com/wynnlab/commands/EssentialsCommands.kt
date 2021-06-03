package com.wynnlab.commands

import com.wynnlab.WL_COLOR
import com.wynnlab.api.sendWynnMessage
import com.wynnlab.essentials.Party
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object EssentialsCommands : BaseCommand("msg", "r", "party", "p") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }
        return when (label) {
            "msg" -> msg(sender, args)
            "r" -> r(sender, args)
            "party" -> party(sender, args)
            "p" -> p(sender, args)
            else -> false
        }
    }

    private fun msg(player: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            player.sendWynnMessage("commands.no_receiver")
            return false
        }
        if (args.size < 2) {
            player.sendWynnMessage("commands.no_message")
            return false
        }
        val receivers: List<Player> = player.server.selectEntities(player, args[0]).filterIsInstance<Player>()
        val message = args.slice(1 until args.size).joinToString(" ")
        pm(player, receivers, args[0], message)
        return true
    }

    val conversations = mutableMapOf<Player, Player>()

    private fun r(player: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            player.sendWynnMessage("commands.no_message")
            return false
        }
        val receiver = conversations[player] ?: run {
            player.sendWynnMessage("commands.no_conversation")
            return true
        }
        val message = args.joinToString(" ")
        pm(player, listOf(receiver), player.name, message)
        return true
    }

    private fun pm(sender: Player, receivers: List<Player>, specifiedReceiver: String, text: String) {
        fun getComponent(s: Component, r: Component) = Component.text("[", TextColor.color(0x666666))
            .append(s)
            .append(Component.text(" ➤ ", TextColor.color(0xf7942a)))
            .append(r)
            .append(Component.text("] ", TextColor.color(0x666666)))
            .append(LegacyComponentSerializer.legacy('&').deserialize(text))

        val you = Component.text("You", WL_COLOR)
        val sc = Component.text(sender.name, TextColor.color(0x25d8f7))

        sender.sendMessage(getComponent(you, Component.text(specifiedReceiver, TextColor.color(if (Bukkit.getPlayer(specifiedReceiver) != null) 0xd94ef5 else 0x25d8f7))))
        for (receiver in receivers) {
            receiver.sendMessage(getComponent(sc, you))
            conversations[receiver] = sender
        }
    }

    private fun party(player: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return false
        }
        return when (args[0]) {
            "create" -> partyCreate(player)
            "invite" -> partyInvite(player, args)
            "join" -> partyJoin(player)
            "leave" -> partyLeave(player)
            else -> false
        }
    }

    private fun partyCreate(player: Player): Boolean {
        val party = Party(player)

        player.sendWynnMessage("messages.party.create")

        Party.parties.add(party)

        party.members.add(player)
        Party.members[player] = party

        return true
    }

    private fun partyInvite(player: Player, args: Array<out String>): Boolean {
        if (args.size < 2) {
            player.sendWynnMessage("messages.no_player")
            return false
        }

        val party = player.getParty()
        if (party == null) {
            player.sendWynnMessage("messages.party.no_party")
            return true
        }
        if (party.owner != player) {
            player.sendWynnMessage("messages.party.not_owner")
            return true
        }

        val newMember = Bukkit.getPlayer(args[1])
        if (newMember == null) {
            player.sendWynnMessage("messages.player.not_exist")
            return false
        }

        party.invite(newMember)

        return true
    }

    private fun partyJoin(player: Player): Boolean {
        if (Party.invites.containsKey(player)) {
            if (player.getParty() == Party.invites[player]) {
                player.sendWynnMessage("messages.party.already_joined")
                Party.invites.remove(player)
                return true
            } else if (player.getParty() != null) {
                player.sendWynnMessage("messages.party.already_other")
                player.sendWynnMessage("messages.party.leave")
                return true
            }

            Party.invites[player]!!.addMember(player)
            Party.invites.remove(player)
        } else {
            player.sendWynnMessage("messages.party.no_invite")
        }
        return true
    }

    private fun partyLeave(player: Player): Boolean {
        val party = player.getParty()
        if (party == null) {
            player.sendWynnMessage("messages.party.no_party")
            return true
        }

        party.removeMember(player)

        return true
    }

    private fun p(player: Player, args: Array<out String>): Boolean {
        val name = player.name
        val msg = args.joinToString(" ")

        val party = player.getParty()
        if (party == null) {
            player.sendWynnMessage("messages.party.no_party")
            return true
        }

        party.members.forEach {
            it.sendMessage("§7[§dParty§7] §f$name: $msg")
        }

        return true
    }

    private fun Player.getParty() = Party.members[this]
}