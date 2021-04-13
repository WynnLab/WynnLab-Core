from org.bukkit import Particle, Sound

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        self.sound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .8)
        l = self.player.getEyeLocation().clone().add(self.player.getEyeLocation().getDirection())
        self.particle(l, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0)

        for e in self.nearbyMobs(l, 2, 2, 2):
            self.damage(e, 2)

# THIS IS ONLY A PLACEHOLDER