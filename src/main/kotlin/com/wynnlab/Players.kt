package com.wynnlab

import com.wynnlab.api.*
import com.wynnlab.essentials.Rank
import com.wynnlab.util.getWynncraftAPIResult
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.json.simple.JSONArray
import org.json.simple.JSONObject

object Players {
    val players get() = Bukkit.getOnlinePlayers()

    fun initPlayers() {
        //initTeams()
        for (player in players) {
            preparePlayer(player)
        }
    }

    /*fun initTeams() {
        val mainTeam = try {
            Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("WynnLab.MainTeam")
        } catch (e: IllegalArgumentException) {
            return
        }
        mainTeam.setAllowFriendlyFire(false)
        mainTeam.setCanSeeFriendlyInvisibles(false)
        mainTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
    }*/

    //WARNING: Only for thing that work if done multiple times
    fun preparePlayer(player: Player) {
        //Bukkit.getScoreboardManager().mainScoreboard.getTeam("WynnLab.MainTeam")?.addEntry(player.name)
        player.gameMode = GameMode.ADVENTURE
        player.foodLevel = 20
        player.saturation = 0f
        player.exp = 0f
        player.level = 106

        player.healthScale = 20.0
        player.isHealthScaled = true

        player.sendPlayerListHeaderAndFooter(
            Component.text("play.WYNNLAB.tk", NamedTextColor.LIGHT_PURPLE, TextDecoration.UNDERLINED),
            Component.text("Join our Discord:", NamedTextColor.YELLOW).append(Component.text("https://discord.gg/7ktHKn2nZG", NamedTextColor.AQUA, TextDecoration.ITALIC))
                //.append(Component.newline()).append(Component.text("")))
        )

        player.data.run {
            remove("rr_action_bar")
            remove("rr_cancel_spell")
        }

        player.inventory.run {
            setItem(6, ItemStack(Material.COMPASS).meta {
                setDisplayName("§bCharacter Info")
                lore = listOf("§6View and manage your skills")
            })
            setItem(7, ItemStack(Material.WRITTEN_BOOK).metaAs<BookMeta> {
                title = "§dQuest Book"
                author = "WynnLab"
                generation = BookMeta.Generation.ORIGINAL
            }.meta {
            lore = listOf("", "§5Quests: §d0/0 §5[100%]")
            })
            setItem(8, ItemStack(Material.NETHER_STAR, 15).meta {
                setDisplayName("§e§l§o15§b Soul Points")
                lore = listOf("§7Having less soul points increases", "§7the chance of dropping items upon", "§7death",
                )//" ", "§cShift Right-Click to enable hunted")
            })
            setItem(13, ItemStack(Material.DIAMOND_AXE).setAppearance(93).meta {
                addItemFlags(*ItemFlag.values())
                setDisplayName("§6Magic Pouch")
                lore = listOf(
                    "§fLeft-Click §7to view contents",
                    " "
                )
            }) // TODO: ingredient pouch
            // 94 Empty 95 Half 96 Full
            loadAPIData(player)
        }
    }

    private fun loadAPIData(player: Player) {
        getWynncraftAPIResult("https://api.wynncraft.com/v2/player/${player.name}/stats").task().let { root ->
            val data = (root["data"] as JSONArray)[0] as JSONObject

            var rank = when (data["rank"] as String) {
                "Player" -> when (((data["meta"] as JSONObject)["tag"] as JSONObject)["value"] as String?) {
                    "VIP" -> Rank.VIP
                    "VIP+" -> Rank.`VIP+`
                    "HERO" -> Rank.HERO
                    "CHAMPION" -> Rank.CHAMPION
                    else -> Rank.PLAYER
                }
                "Administrator" -> Rank.ADMIN
                "Moderator" -> Rank.MOD
                else -> Rank.CT
            }
            if (player.name == "TheLastMinecraft") {
                player.sendMessage("WynnLab Admin, Wynncraft $rank")
                rank = Rank.ADMIN
            }
            rank.apply(player)

            val guildData = data["guild"] as JSONObject
            val guildName = guildData["name"] as String?
            //val guildRank = guildData["rank"] as String?

            guildName?.let { gn ->
                getWynncraftAPIResult("https://api.wynncraft.com/public_api.php?action=guildStats&command=${gn.replace(" ", "%20")}").execute { guild ->
                    val guildTag = guild["prefix"] as String?
                    guildName.let { player.data.setString("guild", it) }
                    guildTag?.let { player.data.setString("guild_tag", it) }
                }
            }
        }
    }
}