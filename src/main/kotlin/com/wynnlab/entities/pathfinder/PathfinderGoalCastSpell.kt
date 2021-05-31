package com.wynnlab.entities.pathfinder

import com.wynnlab.wynnlab
import com.wynnlab.spells.MobSpell
import net.minecraft.server.v1_16_R3.EntityCreature
import net.minecraft.server.v1_16_R3.EntityLiving
import net.minecraft.server.v1_16_R3.EntityPlayer
import net.minecraft.server.v1_16_R3.PathfinderGoal
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.util.*

class PathfinderGoalCastSpell(private val creature: EntityCreature, range: Double, private val spells: List<MobSpell>) : PathfinderGoal() {
    private val range = range * range

    private var cooldown = 20
    private var prepare = 0

    private var target: EntityLiving? = null

    private var spell: MobSpell? = null
    private var runnable: MobSpell.Ticks? = null

    init {
        a(EnumSet.of(Type.LOOK))
    }

    override fun a(): Boolean {
        if (cooldown > 0) {
            --cooldown
            return false
        }

        target = (creature.goalTarget as? EntityPlayer) ?: return false

        if (target == null)
            return false

        if (target!!.h(creature) > range)
            return false

        return true
    }

    override fun c() {
        spell = try { spells.random() } catch (e: NoSuchElementException) { return }

        cooldown = spell!!.cooldown
        prepare = spell!!.prepareTime

        spell!!.spellEffects(creature.bukkitEntity)

        if (spell!!.hasBossBar)
            creature.bukkitEntity.world.getNearbyEntities(creature.bukkitEntity.location, 10.0, 10.0, 10.0) { it is Player }
                .forEach { spell!!.bossBar!!.addPlayer(it as Player) }
    }

    override fun b(): Boolean {
        if (spell == null)
            return false

        if (prepare > 0) {
            --prepare

            if (spell!!.hasBossBar)
                spell!!.bossBar!!.progress = 1.0 - prepare / spell!!.prepareTime.toDouble()

            return true
        } else if (prepare == 0) {
            if (spell!!.hasBossBar) {
                spell!!.bossBar!!.removeAll()
                Bukkit.removeBossBar(NamespacedKey(wynnlab, "prepare_${creature.id}"))
            }

            runnable = spell!!.newInstance(creature.bukkitEntity, target!!.bukkitEntity as Player)
            --prepare
        }

        if (!(runnable!!.tick()))
            return false

        ++runnable!!.t

        return runnable!!.t <= spell!!.maxTick
    }
}