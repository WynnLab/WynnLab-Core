from org.bukkit import Particle, Sound
from org.bukkit.potion import PotionEffect, PotionEffectType

from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def __init__(self):
        self.hit = set()

    def tick(self):
        if self.t == 0:
            if self.player.hasPotionEffect(PotionEffectType.INVISIBILITY):
                self.castSpell('ASSASSIN', 5)

            if self.clone:
                self.sound(Sound.ENTITY_EVOKER_CAST_SPELL, .5, 1.6)
                self.sound(Sound.ENTITY_BLAZE_AMBIENT, .2, 1.5)

        l = self.player.getEyeLocation().clone().add(Math.sin(self.t * -22.5 * DEG2RAD) * 5, 0, Math.cos(self.t * -22.5 * DEG2RAD) * 5)

        for e in self.nearbyMobs(l, 3, 3, 3):
            if e in self.hit:
                continue
            self.hit.add(e)

            self.damage(e, False, 1.5, .7, 0, .3, 0, 0, 0)
            PySpell.knockback(e, VectorUP, .5)
            e.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 100, 1))
            e.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20, 8))

        self.particle(l.clone().subtract(0, .5, 0), Particle.SWEEP_ATTACK, 4, .5, .5, .5, 0)
        self.particle(l, Particle.CLOUD if self.clone else Particle.SQUID_INK, 5, .5, .5, .5, .1)
        self.particle(l, Particle.SPELL_WITCH if self.clone else Particle.CRIT_MAGIC, 10, .5, .5, .5, .2)

        self.sound(l, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1.2)
        self.sound(l, Sound.ITEM_FLINTANDSTEEL_USE, 1, 1.3)
