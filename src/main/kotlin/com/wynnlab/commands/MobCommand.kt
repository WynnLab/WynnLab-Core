package com.wynnlab.commands

import com.wynnlab.api.hasScoreboardTag
import com.wynnlab.entities.WynnMob
import com.wynnlab.mobs.BaseMob
import com.wynnlab.registry.MobRegistry
import com.wynnlab.wynnlab
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.network.chat.ChatComponentText
import net.minecraft.sounds.SoundEffect
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityCreature
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.EnumItemSlot
import net.minecraft.world.entity.ai.attributes.GenericAttributes
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.craftbukkit.v1_17_R1.CraftSound
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File

object MobCommand : BaseCommand("mob") {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be executed by players")
            return true
        }

        if (sender.hasScoreboardTag("pvp")) {
            sender.sendMessage("§cYou can't spawn mobs during pvp")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§cPlease specify a mob name")
            return false
        }

        val mobName = args.joinToString(" ")

        val mob = mobs[mobName] ?: run {
            val apiMob = MobRegistry.entries().find { it.name.content() == mobName }
            if (apiMob != null) return@run apiMob

            val folder = File(wynnlab.dataFolder, "mobs")
            folder.mkdir()

            val file = File(folder, "$mobName.yml")
            if (!file.exists()) {
                sender.sendMessage("§cThis mob doesn't exist")
                return true
            }

            val config = YamlConfiguration()
            config.load(file)

            config.getSerializable("mob", WynnMob::class.java)
        }?.also { mobs[mobName] = it } ?: run {
            sender.sendMessage("§cMalformed config")
            return true
        }

        //mob.spawn(sender.location)
        (mob as? WynnMob)?.spawn(sender.location) ?: (mob as BaseMob).spawn(sender.location)

        return true
    }

    val mobs = hashMapOf<String, Any>()

    private fun BaseMob.spawn(l: Location) {
        fun String.toECName() = replace(Regex("[A-Z]")) { "_${it.value}" }.toUpperCase()

        fun ItemStack.toNMSItem() = CraftItemStack.asNMSCopy(this)

        fun BaseMob.Sound.toSoundEffect() = CraftSound.getSoundEffect(bukkitSound)

        fun BaseMob.AI.initPathfinder(g: PathfinderGoalSelector, t: PathfinderGoalSelector, c: EntityCreature, m: BaseMob) {
            if (flags and (1 shl 2) == 1 shl 2) {
                WynnMob.AI.RANGED.initPathfinder(g, t, c, m)
            }
        }

        class C(location: Location) : EntityCreature(EntityTypes::class.java.getDeclaredField(mobType.entityClass!!.name.toECName())[null] as EntityTypes<out EntityCreature>, (location.world as CraftWorld).handle) {
            init {
                setLocation(location.x, location.y, location.z, location.yaw, location.pitch)

                customName = ChatComponentText("${LegacyComponentSerializer.legacy('§').serialize(this@spawn.name)} §6[Lv. ${stats.level}]")
                customNameVisible = true

                getAttributeInstance(GenericAttributes.a)!!.value = this@spawn.stats.health.toDouble()
                health = this@spawn.stats.health.toFloat()

                getAttributeInstance(GenericAttributes.d)?.value = this@spawn.stats.speed

                equipment.run {
                    mainHand?.let { setSlot(EnumItemSlot.a, it.toNMSItem(), true) }
                    offHand?.let { setSlot(EnumItemSlot.b, it.toNMSItem(), true) }
                    head?.let { setSlot(EnumItemSlot.f, it.toNMSItem(), true) }
                    chest?.let { setSlot(EnumItemSlot.e, it.toNMSItem(), true) }
                    legs?.let { setSlot(EnumItemSlot.d, it.toNMSItem(), true) }
                    feet?.let { setSlot(EnumItemSlot.c, it.toNMSItem(), true) }
                }
            }

            override fun initPathfinder() {
                this@spawn.ai.initPathfinder(bO, bP, this, this@spawn)
            }

            override fun getSoundAmbient(): SoundEffect? = ambientSound?.toSoundEffect()

            override fun getSoundHurt(damagesource: DamageSource?): SoundEffect? = hurtSound?.toSoundEffect()

            override fun getSoundDeath(): SoundEffect? = deathSound?.toSoundEffect()
        }
    }
}