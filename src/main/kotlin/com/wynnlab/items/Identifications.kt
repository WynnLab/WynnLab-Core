package com.wynnlab.items

import com.wynnlab.api.setContainer
import com.wynnlab.api.setInt
import org.bukkit.persistence.PersistentDataContainer
import org.json.simple.JSONObject

// Commented values are not important
class Identifications(
    private val healthRegen: Int,
    private val manaRegen: Int,
    private val spellDamage: Int,
    private val damageBonus: Int,
    private val lifeSteal: Int,
    private val manaSteal: Int,
    //private val xpBonus: Int,
    //private val lootBonus: Int,
    private val reflection: Int,
    private val strengthPoints: Int,
    private val dexterityPoints: Int,
    private val intelligencePoints: Int,
    private val defensePoints: Int,
    private val agilityPoints: Int,
    private val thorns: Int,
    private val exploding: Int,
    private val speed: Int,
    private val attackSpeedBonus: Int,
    private val poison: Int,
    private val healthBonus: Int,
    //private val soulPoints: Int,
    private val emeraldStealing: Int,
    private val healthRegenRaw: Int,
    private val spellDamageRaw: Int,
    private val damageBonusRaw: Int,
    private val bonusEarthDamage: Int,
    private val bonusThunderDamage: Int,
    private val bonusWaterDamage: Int,
    private val bonusFireDamage: Int,
    private val bonusAirDamage: Int,
    private val bonusEarthDefense: Int,
    private val bonusThunderDefense: Int,
    private val bonusWaterDefense: Int,
    private val bonusFireDefense: Int,
    private val bonusAirDefense: Int,
    private val spellCostPct1: Int,
    private val spellCostPct2: Int,
    private val spellCostPct3: Int,
    private val spellCostPct4: Int,
    private val spellCostRaw1: Int,
    private val spellCostRaw2: Int,
    private val spellCostRaw3: Int,
    private val spellCostRaw4: Int,
    private val rainbowSpellDamageRaw: Int,
    //private val sprint: Int,
    //private val sprintRegen: Int,
    private val jumpHeight: Int,
    //private val lootQuality: Int,
) {
    fun lore(): List<String> {
        val list = mutableListOf(" ")
        var c = false

        if (strengthPoints != 0) { list.add(idValue("Strength", strengthPoints, "")); c = true }
        if (dexterityPoints != 0) { list.add(idValue("Dexterity", dexterityPoints, "")); c = true }
        if (intelligencePoints != 0) { list.add(idValue("Intelligence", intelligencePoints, "")); c = true }
        if (defensePoints != 0) { list.add(idValue("Defense", defensePoints, "")); c = true }
        if (agilityPoints != 0) { list.add(idValue("Agility", agilityPoints, "")); c = true }
        if (c) { list.add(" "); c = false }

        if (spellDamageRaw != 0) { list.add(idValue("§6✣ Spell §7Damage", spellDamageRaw, "")); c = true }
        if (spellDamage != 0) { list.add(idValue("Spell Damage", spellDamage)); c = true }
        if (damageBonusRaw != 0) { list.add(idValue("§6✣ Main Attack §7Damage", damageBonusRaw, "")); c = true }
        if (damageBonus != 0) { list.add(idValue("Main Attack Damage", damageBonus)); c = true }
        if (c) { list.add(" "); c = false }

        if (bonusEarthDamage != 0) { list.add(idValue("§2✤ Earth §7Damage", bonusEarthDamage)); c = true }
        if (bonusThunderDamage != 0) { list.add(idValue("§e✦ Thunder §7Damage", bonusThunderDamage)); c = true }
        if (bonusWaterDamage != 0) { list.add(idValue("§b❉ Water §7Damage", bonusWaterDamage)); c = true }
        if (bonusFireDamage != 0) { list.add(idValue("§c✹ Fire §7Damage", bonusFireDamage)); c = true }
        if (bonusAirDamage != 0) { list.add(idValue("§f❋ Air §7Damage", bonusAirDamage)); c = true }
        if (c) { list.add(" "); c = false }

        if (bonusEarthDefense != 0) { list.add(idValue("§2✤ Earth §7Defense", bonusEarthDefense)); c = true }
        if (bonusThunderDefense != 0) { list.add(idValue("§e✦ Thunder §7Defense", bonusThunderDefense)); c = true }
        if (bonusWaterDefense != 0) { list.add(idValue("§b❉ Water §7Defense", bonusWaterDefense)); c = true }
        if (bonusFireDefense != 0) { list.add(idValue("§c✹ Fire §7Defense", bonusFireDefense)); c = true }
        if (bonusAirDefense != 0) { list.add(idValue("§f❋ Air §7Defense", bonusAirDefense)); c = true }
        if (c) { list.add(" "); c = false }

        if (manaRegen != 0) { list.add(idValue("Mana Regen", manaRegen, "/4s")); c = true }
        if (manaSteal != 0) { list.add(idValue("Mana Steal", manaSteal, "/4s")); c = true }
        if (c) { list.add(" "); c = false }
        if (healthBonus != 0) { list.add(idValue("Health", healthBonus, "")); c = true }
        if (healthRegen != 0) { list.add(idValue("Health Regen", healthRegen)); c = true }
        if (healthRegenRaw != 0) { list.add(idValue("Health Regen", healthRegenRaw, "")); c = true }
        if (lifeSteal != 0) { list.add(idValue("Life Steal", lifeSteal, "/4s")); c = true }
        if (c) { list.add(" "); c = false }
        if (speed != 0) { list.add(idValue("Walk Speed", speed)); c = true }
        if (c) { list.add(" "); c = false }
        //if (xpBonus != 0) { list.add(idValue("XP Bonus", xpBonus)); c = true }
        //if (lootBonus != 0) { list.add(idValue("Loot Bonus", lootBonus)); c = true }
        //if (lootQuality != 0) { list.add(idValue("Loot Quality", lootQuality)); c = true }
        if (emeraldStealing != 0) { list.add(idValue("Stealing", emeraldStealing)); c = true }
        if (c) { list.add(" "); c = false }
        //if (soulPoints != 0) { list.add(idValue("Soul Point Regen", soulPoints)); c = true }
        //if (c) { list.add(" "); c = false }
        if (attackSpeedBonus != 0) { list.add(idValue("Attack speed", attackSpeedBonus, " tier")); c = true }
        if (c) { list.add(" "); c = false }
        if (poison != 0) { list.add(idValue("Poison", poison, "/3s")); c = true }
        if (reflection != 0) { list.add(idValue("Reflection", reflection)); c = true }
        if (thorns != 0) { list.add(idValue("Thorns", thorns)); c = true }
        if (exploding != 0) { list.add(idValue("Exploding", exploding)); c = true }
        if (c) { list.add(" "); c = false }

        if (spellCostPct1 != 0) { list.add(idValue("1st Spell Cost", spellCostPct1, invertedColors = true)); c = true }
        if (spellCostPct2 != 0) { list.add(idValue("2nd Spell Cost", spellCostPct2, invertedColors = true)); c = true }
        if (spellCostPct3 != 0) { list.add(idValue("3rd Spell Cost", spellCostPct3, invertedColors = true)); c = true }
        if (spellCostPct4 != 0) { list.add(idValue("4th Spell Cost", spellCostPct4, "", true)); c = true }
        if (spellCostRaw1 != 0) { list.add(idValue("1st Spell Cost", spellCostRaw1, "", true)); c = true }
        if (spellCostRaw2 != 0) { list.add(idValue("2nd Spell Cost", spellCostRaw2, "", true)); c = true }
        if (spellCostRaw3 != 0) { list.add(idValue("3rd Spell Cost", spellCostRaw3, "", true)); c = true }
        if (spellCostRaw4 != 0) { list.add(idValue("4th Spell Cost", spellCostRaw4, "", true)); c = true }
        if (c) { list.add(" "); c = false }

        if (rainbowSpellDamageRaw != 0) { list.add(idValue("Rainbow Spell Damage", rainbowSpellDamageRaw, "")); c = true }
        if (c) { list.add(" "); c = false }
        //if (sprint != 0) { list.add(idValue("Sprint", sprint)); c = true }
        //if (sprintRegen != 0) { list.add(idValue("Sprint Regen", sprintRegen)); c = true }
        if (jumpHeight != 0) { list.add(idValue("Jump Height", jumpHeight, "")); c = true }
        if (c) { list.add(" ")/*; c = false*/ }

        return list
    }

    fun data(data: PersistentDataContainer) {
        data.setContainer("ids") {
            entry(strengthPoints, "strength_points")
            entry(dexterityPoints, "dexterity_points")
            entry(intelligencePoints, "intelligence_points")
            entry(defensePoints, "defense_points")
            entry(agilityPoints, "agility_points")

            entry(spellDamageRaw, "spell_damage_raw")
            entry(spellDamage, "spell_damage")
            entry(damageBonusRaw, "damage_bonus_raw")
            entry(damageBonus, "damage_bonus")

            entry(bonusEarthDamage, "bonus_earth_damage")
            entry(bonusThunderDamage, "bonus_thunder_damage")
            entry(bonusWaterDamage, "bonus_water_damage")
            entry(bonusFireDamage, "bonus_fire_damage")
            entry(bonusAirDamage, "bonus_air_damage")

            entry(bonusEarthDefense, "bonus_earth_defense")
            entry(bonusThunderDefense, "bonus_thunder_defense")
            entry(bonusWaterDefense, "bonus_water_defense")
            entry(bonusFireDefense, "bonus_fire_defense")
            entry(bonusAirDefense, "bonus_air_defense")

            entry(manaRegen, "mana_regen")
            entry(manaSteal, "mana_steal")
            entry(healthBonus, "health_bonus")
            entry(healthRegen, "health_regen")
            entry(healthRegenRaw, "health_regen_raw")
            entry(lifeSteal, "life_steal")
            entry(speed, "speed")
            entry(emeraldStealing, "emerald_stealing")
            entry(attackSpeedBonus, "attack_speed_bonus")
            entry(poison, "poison")
            entry(reflection, "reflection")
            entry(thorns, "thorns")
            entry(exploding, "exploding")

            entry(spellCostPct1, "spell_cost_pct_1")
            entry(spellCostPct2, "spell_cost_pct_2")
            entry(spellCostPct3, "spell_cost_pct_3")
            entry(spellCostPct4, "spell_cost_pct_4")
            entry(spellCostRaw1, "spell_cost_raw_1")
            entry(spellCostRaw2, "spell_cost_raw_2")
            entry(spellCostRaw3, "spell_cost_raw_3")
            entry(spellCostRaw4, "spell_cost_raw_4")

            entry(rainbowSpellDamageRaw, "rainbow_spell_damage_raw")
            entry(jumpHeight, "jump_height")
        }
    }

    private fun PersistentDataContainer.entry(int: Int, name: String) {
        if (int != 0) setInt(name, int)
    }

    companion object {
        operator fun invoke(json: JSONObject): Identifications {
            val identified = json["identified"] != true
            return Identifications(
                (json["healthRegen"] as Number??: 0).bestID(identified),
                (json["manaRegen"] as Number??: 0).bestID(identified),
                (json["spellDamage"] as Number??: 0).bestID(identified),
                (json["damageBonus"] as Number??: 0).bestID(identified),
                (json["lifeSteal"] as Number??: 0).bestID(identified),
                (json["manaSteal"] as Number??: 0).bestID(identified),
                //(json["xpBonus"] as Number??: 0).bestID(identified),
                //(json["lootBonus"] as Number??: 0).bestID(identified),
                (json["reflection"] as Number??: 0).bestID(identified),
                (json["strengthPoints"] as Number??: 0).bestID(identified),
                (json["dexterityPoints"] as Number??: 0).bestID(identified),
                (json["intelligencePoints"] as Number??: 0).bestID(identified),
                (json["defensePoints"] as Number??: 0).bestID(identified),
                (json["agilityPoints"] as Number??: 0).bestID(identified),
                (json["thorns"] as Number??: 0).bestID(identified),
                (json["exploding"] as Number??: 0).bestID(identified),
                (json["speed"] as Number??: 0).bestID(identified),
                (json["attackSpeedBonus"] as Number??: 0).bestID(identified),
                (json["poison"] as Number??: 0).bestID(identified),
                (json["healthBonus"] as Number??: 0).bestID(identified),
                //(json["soulPoints"] as Number??: 0).bestID(identified),
                (json["emeraldStealing"] as Number??: 0).bestID(identified),
                (json["healthRegenRaw"] as Number??: 0).bestID(identified),
                (json["spellDamageRaw"] as Number??: 0).bestID(identified),
                (json["damageBonusRaw"] as Number??: 0).bestID(identified),
                (json["bonusEarthDamage"] as Number??: 0).bestID(identified),
                (json["bonusThunderDamage"] as Number??: 0).bestID(identified),
                (json["bonusWaterDamage"] as Number??: 0).bestID(identified),
                (json["bonusFireDamage"] as Number??: 0).bestID(identified),
                (json["bonusAirDamage:"] as Number??: 0).bestID(identified),
                (json["bonusEarthDefense"] as Number??: 0).bestID(identified),
                (json["bonusThunderDefense"] as Number??: 0).bestID(identified),
                (json["bonusWaterDefense"] as Number??: 0).bestID(identified),
                (json["bonusFireDefense"] as Number??: 0).bestID(identified),
                (json["bonusAirDefense"] as Number??: 0).bestID(identified),
                (json["spellCostPct1"] as Number??: 0).bestID(identified),
                (json["spellCostPct2"] as Number??: 0).bestID(identified),
                (json["spellCostPct3"] as Number??: 0).bestID(identified),
                (json["spellCostPct4"] as Number??: 0).bestID(identified),
                (json["spellCostRaw1"] as Number??: 0).bestID(identified),
                (json["spellCostRaw2"] as Number??: 0).bestID(identified),
                (json["spellCostRaw3"] as Number??: 0).bestID(identified),
                (json["spellCostRaw4"] as Number??: 0).bestID(identified),
                (json["rainbowSpellDamage"] as Number??: 0).bestID(identified),
                //(json["sprint"] as Number??: 0).bestID(identified),
                //(json["sprintRegen"] as Number??: 0).bestID(identified),
                (json["jumpHeight"] as Number??: 0).bestID(identified),
                //(json["lootQuality"] as Number??: 0).bestID(identified),
            )
        }

        fun Number.bestID(identified: Boolean) = toInt().let {
            if (identified)
                when {
                    it < 0 -> (it * 0.7).coerceAtMost(-1.0)
                    it > 0 -> (it * 1.3).coerceAtLeast(1.0)
                    else -> 0
                }
            else it
        }.toInt()
    }
}

fun idValue(name: String, value: Int, suffix: String = "%", invertedColors: Boolean = false) = if (invertedColors)
    "${if (value < 0) "§a" else "§c+"}$value$suffix §7$name"
else
    "${if (value > 0) "§a+" else "§c"}$value$suffix §7$name"