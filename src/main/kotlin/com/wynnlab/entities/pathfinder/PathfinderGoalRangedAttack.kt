package com.wynnlab.entities.pathfinder

import net.minecraft.server.v1_16_R3.EntityCreature
import net.minecraft.server.v1_16_R3.EntityLiving
import net.minecraft.server.v1_16_R3.PathfinderGoal
import org.bukkit.entity.Projectile
import org.bukkit.projectiles.ProjectileSource
import java.util.*

class PathfinderGoalRangedAttack(private val creature: EntityCreature, range: Double, var cooldown: Int, val projectile: Class<out Projectile>) : PathfinderGoal() {
    private val range = range * range

    private var target: EntityLiving? = null

    init {
        a(EnumSet.of(Type.LOOK))
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
        ((creature.bukkitEntity as? ProjectileSource) ?: return).launchProjectile(projectile, target!!.bukkitEntity.location.toVector().subtract(creature.bukkitEntity.location.toVector()).normalize())
    }
}