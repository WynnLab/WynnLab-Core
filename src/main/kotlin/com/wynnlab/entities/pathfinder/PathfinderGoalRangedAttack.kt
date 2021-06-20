package com.wynnlab.entities.pathfinder

import net.minecraft.world.entity.EntityCreature
import net.minecraft.world.entity.EntityLiving
import net.minecraft.world.entity.ai.goal.PathfinderGoal
import org.bukkit.Material
import org.bukkit.entity.Projectile
import org.bukkit.entity.Snowball
import org.bukkit.inventory.ItemStack
import org.bukkit.projectiles.ProjectileSource
import java.util.*

class PathfinderGoalRangedAttack(private val creature: EntityCreature, range: Double, private val cooldown: Int, private val projectile: Class<out Projectile>, private val projectileMaterial: Material?) : PathfinderGoal() {
    private val range = range * range

    private var c = cooldown

    private var target: EntityLiving? = null

    init {
        a(EnumSet.of(Type.b))
    }

    override fun a(): Boolean {
        if (c > 0) {
            --c
            return false
        }

        c = cooldown

        target = creature.goalTarget

        if (target == null)
            return false

        if (target!!.f(creature) > range)
            return false

        return true
    }

    override fun c() {
        val p = ((creature.bukkitEntity as? ProjectileSource) ?: return).launchProjectile(projectile, target!!.bukkitEntity.location.toVector().subtract(creature.bukkitEntity.location.toVector()).normalize().multiply(3))

        if (p is Snowball && projectileMaterial != null)
            p.item = ItemStack(projectileMaterial)

        p.addScoreboardTag("mob_projectile")
    }
}