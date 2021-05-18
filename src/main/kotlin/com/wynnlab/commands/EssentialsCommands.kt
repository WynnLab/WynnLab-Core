package com.wynnlab.commands

import com.wynnlab.api.sendWynnMessage
import com.wynnlab.essentials.Party
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
        player.sendMessage("§7[§r${player.name} §6➤ §r${args[0]}§7] §r$message")
        for (receiver in receivers) {
            receiver.sendMessage("§7[§r${player.name} §6➤ §r${receiver.name}§7] §r$message")
            conversations[receiver] = player
        }
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
        player.sendMessage("§7[§r${player.name} §6➤ §r${receiver.name}§7] §r$message")
        receiver.sendMessage("§7[§r${player.name} §6➤ §r${receiver.name}§7] §r$message")
        conversations[receiver] = player
        return true
    }

    private fun party(player: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return false
        }
        return when (args[0]) {
            "create" -> {
                val party = Party(player)

                player.sendWynnMessage("messages.party.create")

                Party.parties.add(party)

                party.members.add(player)
                Party.members[player] = party

                true
            }
            "invite" -> {
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

                true
            }
            "join" -> {
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
                true
            }
            "leave" -> {
                val party = player.getParty()
                if (party == null) {
                    player.sendWynnMessage("messages.party.no_party")
                    return true
                }

                party.removeMember(player)

                true
            }
            else -> false
        }
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