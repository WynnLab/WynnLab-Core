package com.wynnlab.items

import com.wynnlab.WynnClass
import com.wynnlab.util.Optional
import com.wynnlab.util.optional
import com.wynnlab.util.optionalAs
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.json.simple.JSONObject

class WynnItem(
    val name: String,
    val displayName: Optional<String>,
    val tier: Tier,
    val set: String?,
    val sockets: Int,
    val category: ItemCategory,
    val type: Type,
    val armorType: Optional<ArmorType>,
    val armorColor: Optional<Triple<Int, Int, Int>>,
    val accessoryType: Optional<AccessoryType>,
    val addedLore: String?,
    val material: Material,
    val itemDamage: Int,
    val damage: OptRange,
    val earthDamage: OptRange,
    val thunderDamage: OptRange,
    val waterDamage: OptRange,
    val fireDamage: OptRange,
    val airDamage: OptRange,
    val attackSpeed: Optional<AttackSpeed>,
    val health: OptInt,
    val earthDefense: OptInt,
    val thunderDefense: OptInt,
    val waterDefense: OptInt,
    val fireDefense: OptInt,
    val airDefense: OptInt,
    val level: Int,
    val classRequirement: WynnClass?,
    val strength: Int,
    val dexterity: Int,
    val intelligence: Int,
    val defense: Int,
    val agility: Int,
) {
    fun toItemStack(): ItemStack {
        return ItemStack(material)
    }

    companion object {
        fun parse(json: JSONObject): WynnItem {
            val materialId = (json["material"] as String).toUpperCase().split(':').let {
                if (it.size > 1) it[0].toInt() to it[1].toInt() else it[0].toInt() to 0
            }
            return WynnItem(
                json["name"] as String,
                json["displayName"].optionalAs(),
                Tier.valueOf((json["tier"] as String).toUpperCase()),
                (json["set"] as String?).ifNullNull(),
                (json["sockets"] as Long).toInt(),
                ItemCategory.valueOf((json["category"] as String).toUpperCase()),
                Type.valueOf((json["type"] as String).toUpperCase()),
                json["armorType"].optionalAs<String>().ifSome { ArmorType.valueOf(it.toUpperCase()) },
                json["armorColor"].optionalAs<String>().ifSome { it.split(',').let { split ->
                    Triple(split[0].toInt(), split[1].toInt(), split[2].toInt())
                } },
                json["accessoryType"].optionalAs<String>().ifSome { AccessoryType.valueOf(it.toUpperCase()) },
                (json["addedLore"] as String?).ifNullNull(),
                idToMaterial[materialId.first] ?: Material.FLINT,
                materialId.second,
                json["damage"].optionalAs<String>().toIntRange(),
                json["earthDamage"].optionalAs<String>().toIntRange(),
                json["thunderDamage"].optionalAs<String>().toIntRange(),
                json["waterDamage"].optionalAs<String>().toIntRange(),
                json["fireDamage"].optionalAs<String>().toIntRange(),
                json["airDamage"].optionalAs<String>().toIntRange(),
                json["attackSpeed"].optionalAs<String>().ifSome { AttackSpeed.valueOf(it.toUpperCase()) },
                json["health"].optionalAs<String>().ifSome { it.toInt() },
                json["earthDefense"].optionalAs<String>().ifSome { it.toInt() },
                json["thunderDefense"].optionalAs<String>().ifSome { it.toInt() },
                json["waterDefense"].optionalAs<String>().ifSome { it.toInt() },
                json["fireDefense"].optionalAs<String>().ifSome { it.toInt() },
                json["airDefense"].optionalAs<String>().ifSome { it.toInt() },
                (json["level"] as Long).toInt(),
                json["classRequirement"]?.let { s -> WynnClass.valueOf((s as String).toUpperCase()) },
                (json["strength"] as Long).toInt(),
                (json["dexterity"] as Long).toInt(),
                (json["intelligence"] as Long).toInt(),
                (json["defense"] as Long).toInt(),
                (json["agility"] as Long).toInt()
            )
        }

        private fun Optional<String>.toIntRange() = ifSome { s -> s.split('-').let { it[0].toInt()..it[1].toInt() } }
    }


    enum class Tier {
        NORMAL, UNIQUE, RARE, SET, LEGENDARY, FABLED, MYTHIC
    }

    enum class Type {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS, SPEAR, BOW, WAND, DAGGER, RELIK
    }

    enum class ArmorType {
        LEATHER, GOLD, CHAIN, IRON, DIAMOND
    }

    enum class AttackSpeed {
        SUPER_SLOW, VERY_SLOW, SLOW, NORMAL, FAST, VERY_FAST, SUPER_FAST
    }

    enum class AccessoryType {
        RING, BRACELET, NECKLACE
    }

    enum class ItemCategory {
        WEAPON, ARMOR, ACCESSORY
    }
}

private val idToMaterial = hashMapOf(
    261 to Material.BOW
)

private fun String?.ifNullNull(): String? = if (this == "null") null else this

typealias OptInt = Optional<Int>
typealias OptRange = Optional<IntRange>