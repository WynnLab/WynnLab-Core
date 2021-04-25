package com.wynnlab.entities

import net.minecraft.server.v1_16_R3.*
import net.minecraft.server.v1_16_R3.EnumDirection.EnumAxis

class PathfinderGoalCastSpell(private val creature: EntityCreature) : PathfinderGoal() {
    override fun a(): Boolean {
        return false
    }

    fun g(): Int {
        return 40
    }

    fun h(): Int {
        return 100
    }

    fun j() {
        val var0 = creature.goalTarget ?: return
        val var1 = var0.locY().coerceAtMost(creature.locY())
        val var3 = var0.locY().coerceAtLeast(creature.locY()) + 1.0
        val var5 =
            MathHelper.d(var0.locZ() - creature.locZ(), var0.locX() - creature.locX()).toFloat()
        var var6: Int
        if (creature.h(var0) < 9.0) {
            var var7x: Float
            var6 = 0
            while (var6 < 5) {
                var7x = var5 + var6.toFloat() * 3.1415927f * 0.4f
                this.a(
                    creature.locX() + MathHelper.cos(var7x).toDouble() * 1.5,
                    creature.locZ() + MathHelper.sin(var7x)
                        .toDouble() * 1.5,
                    var1,
                    var3,
                    var7x,
                    0
                )
                ++var6
            }
            var6 = 0
            while (var6 < 8) {
                var7x = var5 + var6.toFloat() * 3.1415927f * 2.0f / 8.0f + 1.2566371f
                this.a(
                    creature.locX() + MathHelper.cos(var7x).toDouble() * 2.5,
                    creature.locZ() + MathHelper.sin(var7x)
                        .toDouble() * 2.5,
                    var1,
                    var3,
                    var7x,
                    3
                )
                ++var6
            }
        } else {
            var6 = 0
            while (var6 < 16) {
                val var7 = 1.25 * (var6 + 1).toDouble()
                val var9 = 1 * var6
                this.a(
                    creature.locX() + MathHelper.cos(var5).toDouble() * var7,
                    creature.locZ() + MathHelper.sin(var5)
                        .toDouble() * var7,
                    var1,
                    var3,
                    var5,
                    var9
                )
                ++var6
            }
        }
    }

    fun a(var0: Double, var2: Double, var4: Double, var6: Double, var8: Float, var9: Int) {
        var var10 = BlockPosition(var0, var6, var2)
        var var11 = false
        var var12 = 0.0
        do {
            val var14 = var10.down()
            val var15: IBlockData = creature.world.getType(var14)
            if (var15.d(creature.world, var14, EnumDirection.UP)) {
                if (!creature.world.isEmpty(var10)) {
                    val var16: IBlockData = creature.world.getType(var10)
                    val var17 = var16.getCollisionShape(creature.world, var10)
                    if (!var17.isEmpty) {
                        var12 = var17.c(EnumAxis.Y)
                    }
                }
                var11 = true
                break
            }
            var10 = var10.down()
        } while (var10.y >= MathHelper.floor(var4) - 1)
        if (var11) {
            creature.world.addEntity(
                EntityEvokerFangs(
                    creature.world,
                    var0,
                    var10.y.toDouble() + var12,
                    var2,
                    var8,
                    var9,
                    creature
                )
            )
        }
    }

    fun k(): SoundEffect? {
        return SoundEffects.ENTITY_EVOKER_PREPARE_ATTACK
    }

    fun getCastSpell(): EntityIllagerWizard.Spell? {
        return EntityIllagerWizard.Spell.FANGS
    }
}