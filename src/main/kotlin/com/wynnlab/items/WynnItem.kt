package com.wynnlab.items

import com.wynnlab.WynnClass
import com.wynnlab.util.Optional
import com.wynnlab.util.optional
import com.wynnlab.util.optionalAs
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.json.simple.JSONObject

class WynnItem(
    private val name: String,
    private val displayName: Optional<String>,
    private val tier: Tier,
    private val set: String?,
    private val sockets: Int,
    private val category: ItemCategory,
    private val type: Type?,
    private val armorColor: Optional<Triple<Int, Int, Int>>,
    private val addedLore: String?,
    private val material: Material,
    private val itemDamage: Int,
    private val damage: OptRange,
    private val earthDamage: OptRange,
    private val thunderDamage: OptRange,
    private val waterDamage: OptRange,
    private val fireDamage: OptRange,
    private val airDamage: OptRange,
    private val attackSpeed: Optional<AttackSpeed>,
    private val health: OptInt,
    private val earthDefense: OptInt,
    private val thunderDefense: OptInt,
    private val waterDefense: OptInt,
    private val fireDefense: OptInt,
    private val airDefense: OptInt,
    private val level: Int,
    private val classRequirement: WynnClass?,
    private val strength: Int,
    private val dexterity: Int,
    private val intelligence: Int,
    private val defense: Int,
    private val agility: Int,
) {
    fun toItemStack(): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta

        if (meta is Damageable) {
            meta.isUnbreakable = true
            meta.damage = itemDamage
        }
        if (meta is LeatherArmorMeta) {
            armorColor.ifSome { meta.setColor(Color.fromRGB(it.first, it.second, it.third)) }
        }

        val title = tier.colorCode + displayName.or { name }
        meta.setDisplayName(title)

        val lore = mutableListOf<String>()

        attackSpeed.ifSome { lore.add("§7${it.str} Attack Speed") }
        lore.add("")

        damage.ifSome { if (it.last > 0) lore.add("§6✣ Neutral Damage: ${it.dash()}") }
        earthDamage.ifSome { if (it.last > 0) lore.add("§2✤ Earth §7Damage: ${it.dash()}") }
        thunderDamage.ifSome { if (it.last > 0) lore.add("§e✦ Thunder §7Damage: ${it.dash()}") }
        waterDamage.ifSome { if (it.last > 0) lore.add("§b❉ Water §7Damage: ${it.dash()}") }
        fireDamage.ifSome { if (it.last > 0) lore.add("§c✹ Fire §7Damage: ${it.dash()}") }
        airDamage.ifSome { if (it.last > 0) lore.add("§f❋ Air §7Damage: ${it.dash()}") }

        health.ifSome { if (it > 0) lore.add("§4❤ Health: $it") }
        earthDefense.ifSome { if (it > 0) lore.add("§2✤ Earth §7Defense: $it") }
        thunderDefense.ifSome { if (it > 0) lore.add("§e✦ Thunder §7Defense: $it") }
        waterDefense.ifSome { if (it > 0) lore.add("§b❉ Water §7Defense: $it") }
        fireDefense.ifSome { if (it > 0) lore.add("§c✹ Fire §7Defense: $it") }
        airDefense.ifSome { if (it > 0) lore.add("§f❋ Air §7Defense: $it") }

        lore.add("")
        classRequirement?.let { lore.add("§a✔ §7Class Req: ${it.both()}") }
        lore.add("§a✔ §7Combat Lv. Min: $level")
        if (strength > 0) lore.add("§a✔ §7Strength Min: $strength")
        if (dexterity > 0) lore.add("§a✔ §7Dexterity Min: $dexterity")
        if (intelligence > 0) lore.add("§a✔ §7Intelligence Min: $intelligence")
        if (defense > 0) lore.add("§a✔ §7Defense Min: $defense")
        if (agility > 0) lore.add("§a✔ §7Agility Min: $agility")

        // TODO: Identifications

        lore.add("")
        if (sockets > 0) lore.add("§7[0/$sockets] Powder Slots")

        lore.add("${tier.colorCode}${tier.tierName} Item")

        addedLore?.let { l -> l.wrapLines(30).forEach { lore.add("§8$it") } }

        meta.lore = lore
        item.itemMeta = meta
        return item
    }

    companion object {
        fun parse(json: JSONObject): WynnItem {
            val type = (json["type"] as String?)?.let { Type.valueOf(it.toUpperCase()) }

            val materialId = json["material"]?.let { material -> (material as String).toUpperCase().split(':').let {
                if (it.size > 1) it[0].toInt() to it[1].toInt() else it[0].toInt() to 0
            } }
            val armorType = if (materialId == null) (json["armorType"] as String?)?.let {
                ArmorType.valueOf(it.toUpperCase().takeUnless { a -> a == "GOLD" } ?: "GOLDEN")
            } else null
            val accessoryType = if (materialId == null && armorType == null) (json["accessoryType"] as String?)?.let {
                AccessoryType.valueOf(it.toUpperCase())
            } else null

            val material = materialId?.let { idToMaterial[materialId.first] } ?:
                armorType?.let { Material.valueOf("${it.name}_${type!!.name}") } ?:
                accessoryType?.let { Material.FLINT_AND_STEEL } ?:
                Material.BARRIER
            val itemDamage = materialId?.second ?: 0

            return WynnItem(
                json["name"] as String,
                json["displayName"].optionalAs(),
                Tier.valueOf((json["tier"] as String).toUpperCase()),
                (json["set"] as String?).ifNullNull(),
                (json["sockets"] as Long).toInt(),
                ItemCategory.valueOf((json["category"] as String).toUpperCase()),
                type,
                json["armorColor"].optionalAs<String>().ifSome { it.split(',').let { split ->
                    Triple(split[0].toInt(), split[1].toInt(), split[2].toInt())
                } },
                (json["addedLore"] as String?).ifNullNull(),
                material,
                itemDamage,
                json["damage"].optionalAs<String>().toIntRange(),
                json["earthDamage"].optionalAs<String>().toIntRange(),
                json["thunderDamage"].optionalAs<String>().toIntRange(),
                json["waterDamage"].optionalAs<String>().toIntRange(),
                json["fireDamage"].optionalAs<String>().toIntRange(),
                json["airDamage"].optionalAs<String>().toIntRange(),
                json["attackSpeed"].optionalAs<String>().ifSome { AttackSpeed.valueOf(it.toUpperCase()) },
                json["health"].optionalAs<Long>().ifSome { it.toInt() },
                json["earthDefense"].optionalAs<Long>().ifSome { it.toInt() },
                json["thunderDefense"].optionalAs<Long>().ifSome { it.toInt() },
                json["waterDefense"].optionalAs<Long>().ifSome { it.toInt() },
                json["fireDefense"].optionalAs<Long>().ifSome { it.toInt() },
                json["airDefense"].optionalAs<Long>().ifSome { it.toInt() },
                (json["level"] as Long).toInt(),
                (json["classRequirement"] as String?)?.let { s -> WynnClass.valueOf(s.toUpperCase()) },
                (json["strength"] as Long).toInt(),
                (json["dexterity"] as Long).toInt(),
                (json["intelligence"] as Long).toInt(),
                (json["defense"] as Long).toInt(),
                (json["agility"] as Long).toInt()
            )
        }

        private fun Optional<String>.toIntRange() = ifSome { s -> s.split('-').let { it[0].toInt()..it[1].toInt() } }
    }


    enum class Tier(val tierName: String, val colorCode: String) {
        NORMAL("Normal", "§f"),
        UNIQUE("Unique", "§e"),
        RARE("Rare", "§d"),
        SET("Set", "§a"),
        LEGENDARY("Legendary", "§b"),
        FABLED("Fabled", "§c"),
        MYTHIC("Mythic", "§5")
    }

    enum class Type {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS, SPEAR, BOW, WAND, DAGGER, RELIK
    }

    enum class ArmorType {
        LEATHER, GOLDEN, CHAIN, IRON, DIAMOND
    }

    enum class AttackSpeed(val str: String) {
        SUPER_SLOW("Super Slow"),
        VERY_SLOW("Very Slow"),
        SLOW("Slow"),
        NORMAL("Normal"),
        FAST("Fast"),
        VERY_FAST("Very Fast"),
        SUPER_FAST("Super Fast")
    }

    enum class AccessoryType {
        RING, BRACELET, NECKLACE
    }

    enum class ItemCategory {
        WEAPON, ARMOR, ACCESSORY
    }
}

private val idToMaterial = hashMapOf(
    261 to Material.BOW,
    259 to Material.FLINT_AND_STEEL,
    256 to Material.IRON_SHOVEL,
    359 to Material.SHEARS,
    280 to Material.STICK,
    273 to Material.STONE_SHOVEL,
    269 to Material.WOODEN_SHOVEL
)

private fun String?.ifNullNull(): String? = if (this == "null") null else this

private fun String.wrapLines(lineLength: Int): List<String> {
    val list = mutableListOf<String>()

    var lastNL = 0
    while (lastNL < length) {
        val preLastNL = lastNL
        lastNL += lineLength
        if (lastNL < length) {
            while (this[lastNL] != ' ')
                --lastNL
        } else {
            list.add(substring(preLastNL))
            break
        }
        ++lastNL
        list.add(substring(preLastNL, lastNL - 1))
    }

    return list
}

private fun IntRange.dash() = "$first-$last"

typealias OptInt = Optional<Int>
typealias OptRange = Optional<IntRange>