package com.wynnlab.entities.pathfinder

import com.wynnlab.base.BaseSpell
import com.wynnlab.mobs.spells.BaseMobSpell
import com.wynnlab.spells.MobSpell
import com.wynnlab.wynnlab
import net.minecraft.server.level.EntityPlayer
import net.minecraft.world.entity.EntityCreature
import net.minecraft.world.entity.EntityLiving
import net.minecraft.world.entity.ai.goal.PathfinderGoal
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

class PathfinderGoalCastSpell(private val creature: EntityCreature, range: Double, private val spells: List<Any>) : PathfinderGoal() {
    private val range = range * range

    private var cooldown = 20
    private var prepare = 0

    private var target: EntityLiving? = null

    private var mobSpell: MobSpell? = null
    private var baseMobSpell: BaseMobSpell? = null
    private var runnable: MobSpell.Ticks? = null

    init {
        a(EnumSet.of(Type.b))
    }

    override fun a(): Boolean {
        if (cooldown > 0) {
            --cooldown
            return false
        }

        target = (creature.goalTarget as? EntityPlayer) ?: return false

        if (target == null)
            return false

        if (target!!.f(creature) > range)
            return false

        return true
    }

    override fun c() {
        val spell = try { spells.random() } catch (e: NoSuchElementException) { return }
        if (spell is MobSpell) mobSpell = spell else baseMobSpell = (spell as (Entity, Player) -> BaseMobSpell)(creature.bukkitEntity, target!!.bukkitEntity as Player)

        cooldown = mobSpell?.cooldown ?: baseMobSpell!!.cooldown
        prepare = mobSpell?.prepareTime ?: 20

        //spell!!.spellEffects(creature.bukkitEntity)
        mobSpell?.spellEffects(creature.bukkitEntity) ?: MobSpell.spellEffects(creature.bukkitEntity)

        if (mobSpell?.hasBossBar == true)
            creature.bukkitEntity.world.getNearbyEntities(creature.bukkitEntity.location, 10.0, 10.0, 10.0) { it is Player }
                .forEach { mobSpell!!.bossBar!!.addPlayer(it as Player) }
    }

    override fun b(): Boolean {
        if (mobSpell == null && baseMobSpell == null)
            return false

        if (prepare > 0) {
            --prepare

            if (mobSpell?.hasBossBar == true)
                mobSpell!!.bossBar!!.progress = 1.0 - prepare / mobSpell!!.prepareTime.toDouble()

            return true
        } else if (prepare == 0) {
            if (mobSpell?.hasBossBar == true) {
                mobSpell!!.bossBar!!.removeAll()
                Bukkit.removeBossBar(NamespacedKey(wynnlab, "prepare_${creature.id}"))
            }

            runnable = mobSpell?.newInstance(creature.bukkitEntity, target!!.bukkitEntity as Player)
            baseMobSpell?.onCast()
            --prepare
        }

        if (!(runnable?.tick() ?: true.also { baseMobSpell!!.onTick() }))
            return false

        //++runnable!!.t
        runnable?.t?.inc() ?: baseMobSpell!!.t.inc()

        val cancel = (runnable?.t ?: baseMobSpell!!.t) > (mobSpell?.maxTick ?: BaseSpell::class.java.getDeclaredField("maxTick").getInt(runnable))

        if (cancel && baseMobSpell != null)
            baseMobSpell!!.onCancel()

        return !cancel
    }
}