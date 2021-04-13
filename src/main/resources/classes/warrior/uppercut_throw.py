from org.bukkit import Particle, Sound
from org.bukkit.util import Vector

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def __init__(self, e):
        self.e = e

    def tick(self):
        if self.t == 0:
            self.e.setVelocity(Vector(0, 1.75, 0))
            self.damage(self.e, 5)
            self.sound(self.e.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1 if self.clone else .5, 1)

        elif self.t < 8:
            self.particle(self.e.getLocation(), Particle.CLOUD if self.clone else Particle.LAVA, 3 if self.clone else 1, .6, .1, .6, 0)
            if self.clone:
                self.particle(self.e.getLocation(), Particle.FIREWORKS_SPARK, 1, .6, .1, .6, .5)

        elif self.t == 8:
            self.e.setVelocity(Vector(0, 0, 0))
            self.damage(self.e, 2)

            self.explode()

            self.sound(self.e.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, .9)
            if self.clone:
                self.sound(self.e.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, .2, .8)

        elif self.t == 10:
            self.e.setVelocity(Vector(0, -2, 0))

        elif (not self.e.isOnGround()) and self.t < 20:
            self.particle(self.e.getLocation(), Particle.FIREWORKS_SPARK if self.clone else Particle.SMOKE_LARGE, 10 if self.clone else 2, .6, .6, .6, .5 if self.clone else 0)

        else:
            self.damage(self.e, 3)
            self.explode()
            self.cancel()

    def explode(self):
        self.particle(self.e.getLocation(), Particle.LAVA, 10, .5, .5, .5, 0)
        self.particle(self.e.getLocation(), Particle.FIREWORKS_SPARK if self.clone else Particle.SMOKE_LARGE, 10 if self.clone else 2, .6, .6, .6, .5 if self.clone else 0)
        self.particle(self.e.getLocation(), Particle.SQUID_INK, 5 if self.clone else 10, .4, .4, .4, .3)
        self.particle(self.e.getLocation(), Particle.CLOUD, 10, 1, 1, 1, 1)