package com.wynnlab.essentials

import com.wynnlab.api.data
import com.wynnlab.api.prefix
import com.wynnlab.api.setString
import org.bukkit.ChatColor
import org.bukkit.entity.Player

enum class Rank(
    val donation: Boolean,
    val tag: String,
    val color: ChatColor
) {
    PLAYER(false, "", ChatColor.WHITE),

    VIP(true, "§a[VIP] ", ChatColor.GREEN),
    `VIP+`(true, "§b[§3VIP+§b] ", ChatColor.AQUA),
    HERO(true, "§5[§dHERO§5] ", ChatColor.DARK_PURPLE),
    CHAMPION(true, "§e[§6CHAMPION§e] ", ChatColor.YELLOW),

    CT(false, "§3[CT] ", ChatColor.DARK_AQUA),
    MOD(false, "§6[§eMOD§6] ", ChatColor.GOLD),
    ADMIN(false, "§4[§cADMIN§4] ", ChatColor.DARK_RED);

    /*private val team: Team = registerMainTeam("WynnLab.${name}")

    init {
        team.color = color
        team.prefix = tag
        team.displayName = name
    }*/

    fun apply(player: Player) {
        //team.addEntry(player.name)
        player.prefix = tag+color
        player.data.setString("rank", name)
    }
}