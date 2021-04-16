from org.bukkit import Particle, Sound

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        self.sound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .8)
        l = self.player.getEyeLocation().clone().add(self.player.getEyeLocation().getDirection().multiply(2))
        self.particle(l, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0)

        for e in self.nearbyMobs(l, 3, 3, 3):
            self.damage(e, True, 1)
            self.knockback(e, 1)