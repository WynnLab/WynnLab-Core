package com.wynnlab.entities

import com.wynnlab.spells.MobSpell
import net.minecraft.server.v1_16_R3.*

class PathfinderGoalCastSpell(private val creature: EntityCreature, range: Double, private val spells: List<MobSpell>) : PathfinderGoal() {
    private val range = range * range

    private var cooldown = 20

    private var target: EntityLiving? = null

    private var spell: MobSpell? = null
    private var ticks: MobSpell.Ticks? = null

    init {
        //a(EnumSet.of(PathfinderGoal.Type.))
    }

    override fun a(): Boolean {
        if (cooldown > 0) {
            --cooldown
            return false
        }

        target = creature.goalTarget

        if (target == null)
            return false

        if (target!!.h(creature) > range)
            return false

        return true
    }

    override fun c() {
        spell = spells.random()
        cooldown = spell!!.cooldown
        ticks = spell!!.newInstance(creature.bukkitEntity, target!!.bukkitEntity)
    }

    override fun b(): Boolean {
        ticks!!.tick()
        ++ticks!!.t
        return ticks!!.t <= spell!!.maxTick
    }
}