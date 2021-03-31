from org.bukkit import Particle, Sound
from org.bukkit.entity import Mob, Player

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

            for e in ray_loc.getNearbyEntities(7, 7, 7):
                if isinstance(e, Player):
                    continue
                if not isinstance(e, Mob):
                    continue

                if self.target is None or ray_loc.distance(self.target) > e.getLocation().distance(self.target):
                    self.target = e.getLocation()

            if self.target is None:
                self.target = ray_loc
            while self.target.getBlock().isPassable():
                self.target.subtract(0, 1, 0)

            self.origin = self.target.clone().add(random.nextDouble() * 5 - 2.5, 21, random.nextDouble() * 5 - 2.5)
            self.direction = self.origin.clone().subtract(self.target).toVector().normalize()

            for l in LocationIterator(self.target, self.origin, self.direction, .5):
                self.player.spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0)

            self.direction.multiply(-1)

            if self.clone:
                self.player.playSound(self.origin, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, .5)

        particle_count = 0
        if self.t <= 5: particle_count = 4
        elif self.t <= 10: particle_count = 3
        elif self.t <= 15: particle_count = 2
        elif self.t <= 20: particle_count = 1

        if self.t <= 20:
            p_loc = self.origin.clone().add(self.direction.clone().multiply(self.t))

            self.player.spawnParticle(Particle.EXPLOSION_LARGE, p_loc, particle_count, 0, 0, 0, .1)
            self.player.spawnParticle(Particle.SQUID_INK if self.clone else Particle.CLOUD, p_loc, particle_count * 5, 0, 0, 0, .25)
            self.player.spawnParticle(Particle.SPELL_WITCH if self.clone else Particle.LAVA, p_loc, particle_count, 0, 0, 0, .25)

        if self.t == 20:
            self.player.playSound(self.target, Sound.ENTITY_BLAZE_SHOOT, 5, 1)
            self.player.playSound(self.target, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 5, 1)
            self.player.playSound(self.target, Sound.ENTITY_GENERIC_EXPLODE, 5, .5 if self.clone else .75)
            if self.clone:
                self.player.playSound(self.target, Sound.ENTITY_WITHER_DEATH, 1, .175)

            for e in self.target.getNearbyEntities(3, 3, 3):
                if isinstance(e, Player):
                    continue
                if not isinstance(e, Mob):
                    continue

                e.damage(15, self.player)
                e.setNoDamageTicks(0)

        if self.t >= 20 and self.t % 10 == 0:
            self.player.playSound(self.target, Sound.BLOCK_CAMPFIRE_CRACKLE, 2, 1)
            self.player.spawnParticle(Particle.SPELL_WITCH if self.clone else Particle.FLAME, self.target, 98, 7, 0, 7, .2)
            self.player.spawnParticle(Particle.SMOKE_NORMAL, self.target, 98, 7, 0, 7, .2)
