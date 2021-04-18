from org.bukkit import Particle, Sound
from org.bukkit.potion import PotionEffect, PotionEffectType

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def __init__(self, l):
        self.l = l

    def tick(self):
        if self.t > 0 and self.player.getScoreboardTags().contains('smoke_bomb'):
            self.cancel()

        if self.t % 4 == 0:
            self.particle(self.l, Particle.CLOUD, 16, 3, 3, 3, 0.1)
            self.particle(self.l, Particle.SPELL_WITCH if self.clone else Particle.SQUID_INK, 16, 3, 3, 3, .2 if self.clone else .1)

        if self.t % 20 == 0:
            self.sound(self.l,Sound.BLOCK_FIRE_EXTINGUISH, .3 , 1)

            for e in self.nearbyMobs(self.l, 3, 3, 3):
                self.damage(e, False, .6, .45, .25, 0, 0, 0, .3)
                PySpell.knockback(e, VectorUP, .5)
                e.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 101, 2))

        if self.t == 0:
            self.player.removeScoreboardTag('smoke_bomb')