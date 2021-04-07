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
    private val sprint: Int,
    private val sprintRegen: Int,
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

        if (bonusEarthDefense != 0) { list.add(idValue("§2✤ Earth §7Defense", bonusEarthDamage)); c = true }
        if (bonusThunderDefense != 0) { list.add(idValue("§e✦ Thunder §7Defense", bonusThunderDamage)); c = true }
        if (bonusWaterDefense != 0) { list.add(idValue("§b❉ Water §7Defense", bonusWaterDamage)); c = true }
        if (bonusFireDefense != 0) { list.add(idValue("§c✹ Fire §7Defense", bonusFireDamage)); c = true }
        if (bonusAirDefense != 0) { list.add(idValue("§f❋ Air §7Defense", bonusAirDamage)); c = true }
        if (c) { list.add(" "); c = false }

        if (manaRegen != 0) { list.add(idValue("Mana Regen", manaRegen, "/5s")); c = true }
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

        if (rainbowSpellDamageRaw != 0) { list.add(idValue("Rainbow Spell Damage", rainbowSpellDamageRaw)); c = true }
        if (c) { list.add(" "); c = false }
        if (sprint != 0) { list.add(idValue("Sprint", sprint)); c = true }
        if (sprintRegen != 0) { list.add(idValue("Sprint Regen", sprintRegen)); c = true }
        if (jumpHeight != 0) { list.add(idValue("Jump Height", jumpHeight)); c = true }
        if (c) { list.add(" ")/*; c = false*/ }

        return list
    }

    fun data(data: PersistentDataContainer) {
        data.setContainer("ids") {
            if (strengthPoints != 0) setInt("strength_points", strengthPoints)
            if (dexterityPoints != 0) setInt("dexterity_points", dexterityPoints)
            if (intelligencePoints != 0) setInt("intelligence_points", intelligencePoints)
            if (defensePoints != 0) setInt("defense_points", defensePoints)
            if (agilityPoints != 0) setInt("agility_points", agilityPoints)
        }
    }

    constructor(json: JSONObject) : this(
        (json["healthRegen"] as Number??: 0).toInt(),
        (json["manaRegen"] as Number??: 0).toInt(),
        (json["spellDamage"] as Number??: 0).toInt(),
        (json["damageBonus"] as Number??: 0).toInt(),
        (json["lifeSteal"] as Number??: 0).toInt(),
        (json["manaSteal"] as Number??: 0).toInt(),
        //(json["xpBonus"] as Number??: 0).toInt(),
        //(json["lootBonus"] as Number??: 0).toInt(),
        (json["reflection"] as Number??: 0).toInt(),
        (json["strengthPoints"] as Number??: 0).toInt(),
        (json["dexterityPoints"] as Number??: 0).toInt(),
        (json["intelligencePoints"] as Number??: 0).toInt(),
        (json["defensePoints"] as Number??: 0).toInt(),
        (json["agilityPoints"] as Number??: 0).toInt(),
        (json["thorns"] as Number??: 0).toInt(),
        (json["exploding"] as Number??: 0).toInt(),
        (json["speed"] as Number??: 0).toInt(),
        (json["attackSpeedBonus"] as Number??: 0).toInt(),
        (json["poison"] as Number??: 0).toInt(),
        (json["healthBonus"] as Number??: 0).toInt(),
        //(json["soulPoints"] as Number??: 0).toInt(),
        (json["emeraldStealing"] as Number??: 0).toInt(),
        (json["healthRegenRaw"] as Number??: 0).toInt(),
        (json["spellDamageRaw"] as Number??: 0).toInt(),
        (json["damageBonusRaw"] as Number??: 0).toInt(),
        (json["bonusEarthDamage"] as Number??: 0).toInt(),
        (json["bonusThunderDamage"] as Number??: 0).toInt(),
        (json["bonusWaterDamage"] as Number??: 0).toInt(),
        (json["bonusFireDamage"] as Number??: 0).toInt(),
        (json["bonusAirDamage:"] as Number??: 0).toInt(),
        (json["bonusEarthDefense"] as Number??: 0).toInt(),
        (json["bonusThunderDefense"] as Number??: 0).toInt(),
        (json["bonusWaterDefense"] as Number??: 0).toInt(),
        (json["bonusFireDefense"] as Number??: 0).toInt(),
        (json["bonusAirDefense"] as Number??: 0).toInt(),
        (json["spellCostPct1"] as Number??: 0).toInt(),
        (json["spellCostPct2"] as Number??: 0).toInt(),
        (json["spellCostPct3"] as Number??: 0).toInt(),
        (json["spellCostPct4"] as Number??: 0).toInt(),
        (json["spellCostRaw1"] as Number??: 0).toInt(),
        (json["spellCostRaw2"] as Number??: 0).toInt(),
        (json["spellCostRaw3"] as Number??: 0).toInt(),
        (json["spellCostRaw4"] as Number??: 0).toInt(),
        (json["rainbowSpellDamage"] as Number??: 0).toInt(),
        (json["sprint"] as Number??: 0).toInt(),
        (json["sprintRegen"] as Number??: 0).toInt(),
        (json["jumpHeight"] as Number??: 0).toInt(),
        //(json["lootQuality"] as Number??: 0).toInt(),
    )
}

fun idValue(name: String, value: Int, suffix: String = "%", invertedColors: Boolean = false) = if (invertedColors)
    "${if (value < 0) "§a" else "§c+"}$value$suffix §7$name"
else
    "${if (value > 0) "§a+" else "§c"}$value$suffix §7$name"