package com.wynnlab.guilds

import com.wynnlab.api.metaAs
import com.wynnlab.util.getWynncraftAPIResult
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import org.json.simple.JSONArray
import org.json.simple.JSONObject

class Guild(val name: String) {
    private val guildObject = getWynncraftAPIResult("https://api.wynncraft.com/public_api.php?action=guildStats&command=${name.replace(" ", "%20")}")
    val tag = guildObject["prefix"] as String

    val members = (guildObject["members"] as JSONArray).associate { data -> data as JSONObject
        (data["name"] as String) to Member(
            Member.Rank.valueOf(data["rank"] as String),
            data["joinedFriendly"] as String
        )
    }

    val banner: ItemStack = try {
        val bannerData = guildObject["banner"] as JSONObject
        ItemStack(Material.valueOf("${bannerData["base"]}_BANNER")).metaAs<BannerMeta> {
            for (layer in bannerData["layers"] as JSONArray) { layer as JSONObject
                addPattern(Pattern(DyeColor.valueOf(layer["colour"] as String), PatternType.valueOf(layer["pattern"] as String)))
            }
        }
    } catch (e: Exception) {
        ItemStack(Material.WHITE_BANNER)
    }

    data class Member(
        val rank: Rank,
        val joined: String
    ) {
        enum class Rank {
            OWNER, CHIEF, STRATEGIST, CAPTAIN, RECRUITER, RECRUIT;

            val friendlyName = name.toLowerCase().capitalize()
        }
    }
}