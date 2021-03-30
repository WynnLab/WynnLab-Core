from org.bukkit import Particle, Sound
from org.bukkit.entity import Player, Mob

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
            self.player.spawnParticle(Particle.DRIP_LAVA if self.clone else Particle.FLAME, l.clone().subtract(0, 1, 0) if self.clone else l, 1, 0, 0, 0, 0)

        for l in LocationIterator(self.player.getEyeLocation(), target, self.player.getEyeLocation().getDirection(), 1):
            self.player.spawnParticle(Particle.VILLAGER_ANGRY if self.clone else Particle.LAVA, l, 1, 0, 0, 0, 0)

            for e in l.getNearbyEntities(.5, .5, .5):
                if isinstance(e, Player):
                    continue
                if not isinstance(e, Mob):
                    continue

                e.damage(2, self.player)
                e.setNoDamageTicks(0)

        self.player.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT if self.clone else Sound.ENTITY_SHULKER_TELEPORT, 1, 1)
        self.player.teleport(target)
