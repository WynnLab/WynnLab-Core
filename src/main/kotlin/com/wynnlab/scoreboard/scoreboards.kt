package com.wynnlab.scoreboard

import com.wynnlab.api.hasScoreboardTag
import com.wynnlab.essentials.Party
import com.wynnlab.pvp.Duels
import com.wynnlab.pvp.FFA
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Objective

val InfoSidebar = DynamicScoreboard(
    "info"
) {
    listOf(
        "§0" // TODO
    )
}

val PartyHealthSidebar = HealthSidebar(
    "partyHealth"
) {
    Party.members[it]?.members ?: emptyList()
}

val FFAHealthSidebar = HealthSidebar(
    "ffaHealth"
) {
    if (it.hasScoreboardTag("ffa")) FFA.players else emptyList()
}

val DuelHealthSidebar = HealthSidebar(
    "duelHealth"
) {
    if (it.hasScoreboardTag("duel")) Duels[it]?.players ?: emptyList() else emptyList()
}

val BossDamageSidebar = DamageSidebar(
    "bossDamage"
) {
    emptyList() // TODO
}

val FFADamageSidebar = DamageSidebar(
    "ffaDamage"
) {
    if (it.hasScoreboardTag("ffa")) FFA.players else emptyList()
}


val DuelDamageSidebar = DamageSidebar(
    "ffaDamage",
) {
    if (it.hasScoreboardTag("duel")) Duels[it]?.players ?: emptyList() else emptyList()
}

val FFAKillsSidebar = object : Scoreboard(
    "ffaKills"
) {
    override fun setScores(player: Player, o: Objective) {
        if (!player.hasScoreboardTag("ffa")) return
        FFA.players.forEach { m ->
            val k = m.getStatistic(Statistic.PLAYER_KILLS)
            o.getScore("§${if (k > 0) 'f' else '7'}${m.name}").score = k
        }
    }
}

val scoreboards = hashMapOf(
    "info" to InfoSidebar,
    "partyHealth" to PartyHealthSidebar,
    "ffaHealth" to FFAHealthSidebar,
    "duelHealth" to DuelHealthSidebar,
    "bossDamage" to BossDamageSidebar,
    "ffaDamage" to FFADamageSidebar,
    "duelDamage" to DuelDamageSidebar,
    "ffaKills" to FFAKillsSidebar
)