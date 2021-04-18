from org.bukkit import Particle, Sound

from com.wynnlab.spells import PySpell
from com.wynnlab.util import LocationIterator


class Spell(PySpell):
    def __init__(self):
        self.hit = set()

    def tick(self):
        l1 = self.player.getEyeLocation().clone().add(0, -.5, 0)
        l2 = self.player.getEyeLocation().clone().add(self.player.getEyeLocation().getDirection().multiply(7)).add(0, -.5, 0)

        self.sound(Sound.ITEM_TRIDENT_THROW, 1, 1.5)
        self.sound(Sound.ITEM_TRIDENT_RIPTIDE_3, .2, 1)
        if self.clone:
            self.sound(Sound.ENTITY_SHULKER_SHOOT, .5, 1.5)

        for l in LocationIterator(l1, l2, self.player.getEyeLocation().getDirection(), .5):
            self.particle(l, Particle.SQUID_INK if self.clone else Particle.CLOUD, 2, 0, 0, 0, .1)
            self.particle(l, Particle.SPELL_WITCH if self.clone else Particle.CRIT, 1, 0, 0, 0, 0)
            self.particle(l, Particle.CRIT_MAGIC, 1, 0, 0, 0, .1)

            for e in self.nearbyMobs(l, .5, .5, .5):
                if e in self.hit:
                    continue
                self.hit.add(e)

                self.damage(e, True, 1)
                self.knockback(e, .5)