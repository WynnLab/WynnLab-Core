from org.bukkit import Particle, Sound

from com.wynnlab.spells import PySpell
from com.wynnlab.util import LocationIterator

class Spell(PySpell):
    def tick(self):
        ray = self.player.rayTraceBlocks(14)
        target = self.player.getLocation().clone().add(self.player.getEyeLocation().getDirection().clone().multiply(14)) if ray is None or ray.getHitBlock() is None else ray.getHitPosition().toLocation(self.player.getWorld())
        while not target.getBlock().isPassable():
            target.add(self.player.getEyeLocation().getDirection().clone().multiply(-1))
        target.setDirection(self.player.getEyeLocation().getDirection())

        for l in LocationIterator(self.player.getEyeLocation(), target, self.player.getEyeLocation().getDirection(), .5):
            self.particle(l.clone().subtract(0, 1, 0) if self.clone else l, Particle.DRIP_LAVA if self.clone else Particle.FLAME, 1, 0, 0, 0, 0)

        for l in LocationIterator(self.player.getEyeLocation(), target, self.player.getEyeLocation().getDirection(), 1):
            self.particle(l, Particle.VILLAGER_ANGRY if self.clone else Particle.LAVA, 1, 0, 0, 0, 0)

            for e in self.nearbyMobs(l, .5, 2, .5):
                self.damage(e, 2)

        self.sound(target, Sound.ENTITY_ENDERMAN_TELEPORT if self.clone else Sound.ENTITY_SHULKER_TELEPORT, 1, 1)
        self.player.teleport(target)
