package com.wynnlab.entities

import com.wynnlab.api.*
import com.wynnlab.entities.pathfinder.PathfinderGoalCastSpell
import com.wynnlab.entities.pathfinder.PathfinderGoalRangedAttack
import com.wynnlab.spells.MobSpell
import com.wynnlab.util.BaseSerializable
import com.wynnlab.util.ConfigurationDeserializable
import net.minecraft.server.v1_16_R3.*
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import net.minecraft.server.v1_16_R3.ItemStack as NMSItemStack

data class WynnMob(
    val name: String,
    val mobType: EntityTypes<out EntityCreature>,
    val ai: AI,
    val level: Int,
    val health: Int,
    val regen: Int,
    val damage: IntRange,
    val attackSpeed: Double,
    val projectile: Class<out Projectile>?,
    val projectileMaterial: Material?,
    val speed: Double,
    val vision: Double,
    //val invisible: Boolean = false,
    //val burning: Boolean = false,
    //val baby: Boolean = false,
    val defense: Double,
    val elementalDamage: Elemental<IntRange>?,
    val elementalDefense: Elemental<Int>?,
    val ambientSound: SoundEffect?,
    val hurtSound: SoundEffect?,
    val deathSound: SoundEffect?,
    val kbResistance: Double,
    val equipment: Equipment,
    val spells: List<MobSpell>
) : BaseSerializable<WynnMob>() {
    // Custom entity inner class
    private inner class C(location: Location) : EntityCreature(mobType, (location.world as CraftWorld).handle) {
        init {
            setLocation(location.x, location.y, location.z, location.yaw, location.pitch)

            customName = ChatComponentText("${this@WynnMob.name} §6[Lv. $level]")
            customNameVisible = true

            getAttributeInstance(GenericAttributes.MAX_HEALTH)!!.value = this@WynnMob.health.toDouble()
            health = this@WynnMob.health.toFloat()

            getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)?.value = this@WynnMob.speed

            equipment.run {
                mainHand?.let { setSlot(EnumItemSlot.MAINHAND, it, true) }
                offHand?.let { setSlot(EnumItemSlot.OFFHAND, it, true) }
                head?.let { setSlot(EnumItemSlot.HEAD, it, true) }
                chest?.let { setSlot(EnumItemSlot.CHEST, it, true) }
                legs?.let { setSlot(EnumItemSlot.LEGS, it, true) }
                feet?.let { setSlot(EnumItemSlot.FEET, it, true) }
            }
        }

        override fun initPathfinder() {
            this@WynnMob.ai.initPathfinder(goalSelector, targetSelector, this, this@WynnMob)
        }

        override fun getSoundAmbient(): SoundEffect? = ambientSound

        override fun getSoundHurt(damagesource: DamageSource?): SoundEffect? = hurtSound

        override fun getSoundDeath(): SoundEffect? = deathSound
    }

    fun spawn(location: Location) {
        val entity = C(location)

        entity.spawnIn((location.world as CraftWorld).handle)
        (location.world as CraftWorld).handle.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM)

        val be = entity.bukkitEntity

        be.data.run {
            setString("old_name", name)

            setDouble("defense", defense)
            setIntArray("elemental_defense", elementalDefense?.let { intArrayOf(it.earth, it.thunder, it.water, it.fire, it.air) } ?:
                intArrayOf(0, 0, 0, 0, 0))
            setInt("damage_min", damage.first)
            setInt("damage_max", damage.last)
            setIntArray("elemental_damage", elementalDamage?.let { intArrayOf(it.earth.first, it.earth.last, it.thunder.first, it.thunder.last, it.water.first, it.water.last, it.fire.first, it.fire.last, it.air.first, it.air.last) } ?:
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override val deserializer = Companion

    companion object : ConfigurationDeserializable<WynnMob> {
        @[JvmStatic Suppress("unused", "unchecked_cast")]
        override fun deserialize(map: Map<String, Any?>): WynnMob {
            val name = ChatColor.translateAlternateColorCodes('&', map["name"] as String)
            val mobType = EntityTypes::class.java.getDeclaredField((map["mob_type"] as String).toUpperCase())[null] as EntityTypes<out EntityCreature>
            val ai = AI.valueOf((map["ai"] as String).toUpperCase())
            val level = (map["level"] as Number).toInt()
            val health = (map["health"] as Number).toInt()
            val regen = (map["regen"] as Number).toInt()
            val damage = (map["damage"] as String).split('-').let { it[0].toInt()..it[1].toInt() }
            val attackSpeed = (map["attack_speed"] as Number).toDouble()
            val projectile = (map["projectile"] as String?)?.let { Class.forName("org.bukkit.entity.$it") as Class<out Projectile> }
            val projectileMaterial = (map["projectile_material"] as String?)?.let { Material.getMaterial(it) }
            val speed = (map["speed"] as Number).toDouble()
            val vision = (map["vision"] as Number).toDouble()
            val defense = (map["defense"] as Number).toDouble()
            val elementalDamage = (map["elemental_damage"] as List<String>?)?.map { s -> s.split('-').let { it[0].toInt()..it[1].toInt() } }?.let {
                Elemental(it[0], it[1], it[2], it[3], it[4]) }
            val elementalDefense = (map["elemental_defense"] as List<Int>?)?.let { Elemental(it[0], it[1], it[2], it[3], it[4]) }
            val ambientSound = (map["ambient_sound"] as String?)?.let { SoundEffects::class.java.getDeclaredField(it.toUpperCase())[null] as SoundEffect }
            val hurtSound = (map["hurt_sound"] as String?)?.let { SoundEffects::class.java.getDeclaredField(it.toUpperCase())[null] as SoundEffect }
            val deathSound = (map["death_sound"] as String?)?.let { SoundEffects::class.java.getDeclaredField(it.toUpperCase())[null] as SoundEffect }
            val kbResistance = (map["kb_resistance"] as Number ??: 0).toDouble()
            val equipment = map["equipment"] as Equipment ??: Equipment(null, null, null, null, null, null)
            val spells = map["spells"] as List<MobSpell> ??: listOf()

            return WynnMob(name, mobType, ai, level, health, regen, damage, attackSpeed, projectile, projectileMaterial, speed, vision, defense, elementalDamage, elementalDefense, ambientSound, hurtSound, deathSound, kbResistance, equipment, spells).also { println(it) }
        }
    }

    data class Equipment(
        val mainHand: NMSItemStack?,
        val offHand: NMSItemStack?,
        val head: NMSItemStack?,
        val chest: NMSItemStack?,
        val legs: NMSItemStack?,
        val feet: NMSItemStack?
    ) : BaseSerializable<Equipment>() {
        override fun serialize(): MutableMap<String, Any> {
            TODO("Not yet implemented")
        }

        override val deserializer = Companion

        companion object : ConfigurationDeserializable<Equipment> {
            @[JvmStatic Suppress("unused", "unchecked_cast")]
            override fun deserialize(map: Map<String, Any?>): Equipment {
                val mainHand = (map["main_hand"] as ItemStack?)?.let { CraftItemStack.asNMSCopy(it) }
                val offHand = (map["offhand"] as ItemStack?)?.let { CraftItemStack.asNMSCopy(it) }
                val head = (map["head"] as ItemStack?)?.let { CraftItemStack.asNMSCopy(it) }
                val chest = (map["chest"] as ItemStack?)?.let { CraftItemStack.asNMSCopy(it) }
                val legs = (map["legs"] as ItemStack?)?.let { CraftItemStack.asNMSCopy(it) }
                val feet = (map["feet"] as ItemStack?)?.let { CraftItemStack.asNMSCopy(it) }

                return Equipment(mainHand, offHand, head, chest, legs, feet).also { println(it) }
            }
        }
    }

    enum class AI(val initPathfinder: (PathfinderGoalSelector, PathfinderGoalSelector, EntityCreature, WynnMob) -> Unit) {
        NONE({ g, _, e, _ ->
            g.a(0, PathfinderGoalFloat(e))
        }),
        NO_ATTACK({ g, _, e, _ ->
            g.a(1, PathfinderGoalRandomLookaround(e))
            g.a(2, PathfinderGoalRandomStroll(e, 1.0))
            g.a(3, PathfinderGoalLookAtPlayer(e, EntityPlayer::class.java, .5f))

            g.a(0, PathfinderGoalFloat(e))
        }),
        MELEE({ g, t, e, m ->
            //t.a(0, PathfinderGoalNearestAttackableTarget(e, EntityHuman::class.java, 10, true, false) { (e.bukkitEntity.lastDamageCause?.entity as org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity).handle == it })
            t.a(0, PathfinderGoalNearestAttackableTarget(e, EntityPlayer::class.java, true))

            g.a(1, PathfinderGoalCastSpell(e, m.vision, m.spells))

            g.a(2, PathfinderGoalMeleeAttack(e, 1.0, true))

            g.a(3, PathfinderGoalRandomLookaround(e))
            g.a(4, PathfinderGoalRandomStroll(e, 1.0))
            g.a(5, PathfinderGoalLookAtPlayer(e, EntityPlayer::class.java, .5f))

            g.a(0, PathfinderGoalFloat(e))
        }),
        RANGED({ g, t, e, m ->
            //t.a(0, PathfinderGoalNearestAttackableTarget(e, EntityHuman::class.java, true))
            t.a(0, PathfinderGoalNearestAttackableTarget(e, EntityPlayer::class.java, true))

            g.a(1, PathfinderGoalCastSpell(e, m.vision, m.spells))

            g.a(2, PathfinderGoalRangedAttack(e, m.vision, (m.attackSpeed * 20).toInt(), m.projectile!!, m.projectileMaterial))

            g.a(3, PathfinderGoalRandomLookaround(e))
            g.a(4, PathfinderGoalRandomStroll(e, 1.0))
            g.a(5, PathfinderGoalLookAtPlayer(e, EntityHuman::class.java, .5f))

            g.a(0, PathfinderGoalFloat(e))
        }),
        SUPPORT({ g, t, e, m ->
            NO_ATTACK.initPathfinder(g, t, e, m)
        })
    }

    data class Elemental<T>(
        val earth: T,
        val thunder: T,
        val water: T,
        val fire: T,
        val air: T,
    )
}

/*fun <T> Class<T>.getProtectedMethod(name: String, vararg parameters: Class<out Any?>): Method {
    val m = try {
        getMethod(name, *parameters)
    } catch (e: NoSuchMethodException) {
        var clazz: Class<in T> = this
        var method: Method? = null
        while (method == null) method = try {
            clazz.getDeclaredMethod(name, *parameters)
        } catch (e: NoSuchMethodException) {
            clazz = clazz.superclass ?: throw NoSuchMethodException(name)
            null
        }
        method
    }
    m.isAccessible = true
    return m
}*/