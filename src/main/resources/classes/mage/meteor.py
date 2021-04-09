from org.bukkit import Particle, Sound

from com.wynnlab.spells import PySpell
from com.wynnlab.util import LocationIterator


class Spell(PySpell):
    def __init__(self):
        self.target = None
        self.origin = None
        self.direction = None

    def tick(self):
        if self.t == 0:
            ray = self.player.rayTraceBlocks(21)
            ray_loc = self.player.getEyeLocation().clone().add(self.player.getEyeLocation().getDirection().clone().multiply(21)) if ray is None or ray.getHitBlock() is None else ray.getHitBlock().getLocation()
            if ray is not None and ray.getHitEntity() is not None:
                ray_loc = ray.getHitEntity().getLocation()

            for e in self.nearbyMobs(ray_loc, 7, 7, 7):
                if self.target is None or ray_loc.distance(self.target) > e.getLocation().distance(self.target):
                    self.target = e.getLocation()

            if self.target is None:
                self.target = ray_loc
            while self.target.getBlock().isPassable():
                self.target.subtract(0, 1, 0)

            self.origin = self.target.clone().add(random.nextDouble() * 5 - 2.5, 21, random.nextDouble() * 5 - 2.5)
            self.direction = self.origin.clone().subtract(self.target).toVector().normalize()

            for l in LocationIterator(self.target, self.origin, self.direction, .5):
                self.particle(l, Particle.FLAME, 1, 0, 0, 0, 0)

            self.direction.multiply(-1)

            if self.clone:
                self.sound(self.origin, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, .5)

        particle_count = 0
        if self.t <= 5: particle_count = 4
        elif self.t <= 10: particle_count = 3
        elif self.t <= 15: particle_count = 2
        elif self.t <= 20: particle_count = 1

        if self.t <= 20:
            p_loc = self.origin.clone().add(self.direction.clone().multiply(self.t))

            self.particle(p_loc, Particle.EXPLOSION_LARGE, particle_count, 0, 0, 0, .1)
            self.particle(p_loc, Particle.SQUID_INK if self.clone else Particle.CLOUD, particle_count * 5, 0, 0, 0, .25)
            self.particle(p_loc, Particle.SPELL_WITCH if self.clone else Particle.LAVA, particle_count, 0, 0, 0, .25)

        if self.t == 20:
            self.sound(self.target, Sound.ENTITY_BLAZE_SHOOT, 5, 1)
            self.sound(self.target, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 5, 1)
            self.sound(self.target, Sound.ENTITY_GENERIC_EXPLODE, 5, .5 if self.clone else .75)
            self.sound(self.target, Sound.ITEM_TRIDENT_THUNDER, 1, .5)

            for e in self.nearbyMobs(self.target, 3, 3, 3):
                self.damage(e, 15)

        if self.t >= 20 and self.t % 10 == 0:
            self.sound(self.target, Sound.BLOCK_CAMPFIRE_CRACKLE, 2, 1)
            self.particle(self.target, Particle.SPELL_WITCH if self.clone else Particle.FLAME, 98, 7, 1, 7, .2)
            self.particle(self.target, Particle.SMOKE_NORMAL, 98, 7, 3, 7, .2)
