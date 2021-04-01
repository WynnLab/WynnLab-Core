package com.wynnlab.spells.assassin

/*import com.wynnlab.spells.SpellData
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

class AssassinMain(player: Player) : SpellL(player, 1, SpellData.METEOR) {
    override fun tick() {
        player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, .8f)
        val l = player.eyeLocation + player.eyeLocation.direction * 2.0
        player.spawnParticle(Particle.SWEEP_ATTACK, l, 1, .0, .0, .0, .0)

        for (e in player.world.getNearbyEntities(l, 2.0, 2.0, 2.0)) {
            if (e is Player)
                continue
            if (e !is Mob)
                continue
            e.damage(2.0, player)
            e.noDamageTicks = 0
        }
    }
}*/