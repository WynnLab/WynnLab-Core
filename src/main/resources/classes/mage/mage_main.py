from org.bukkit import Particle, Sound
from org.bukkit.entity import Mob, Player

from com.wynnlab.spells import PySpell
from com.wynnlab.util import LocationIterator

class Spell(PySpell):
    def __init__(self):
        self.hit = {}

    def tick(self):
        l1 = self.player.getEyeLocation().clone().add(0, -.5, 0)
        l2 = self.player.getEyeLocation().clone().add(self.player.getEyeLocation().getDirection().clone().multiply(7)).add(0, -.5, 0)

        self.player.playSound(self.player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1.5)
        if self.clone:
            self.player.playSound(self.player.getLocation(), Sound.ENTITY_SHULKER_SHOOT, .5, 1.5)

        for l in LocationIterator(l1, l2, self.player.getEyeLocation().getDirection(), .5):
            self.player.spawnParticle(Particle.SQUID_INK if self.clone else Particle.CLOUD, l, 2, 0, 0, 0, .1)
            self.player.spawnParticle(Particle.SPELL_WITCH if self.clone else Particle.CRIT, l, 1, 0, 0, 0, 0)
            self.player.spawnParticle(Particle.CRIT_MAGIC, l, 1, 0, 0, 0, .1)

            for e in l.getNearbyEntities(.5, .5, .5):
                if isinstance(e, Player):
                    continue
                if not isinstance(e, Mob):
                    continue
                if e in self.hit:
                    continue

                self.hit.add(e)
                e.damage(2, self.player)
                e.setNoDamageTicks(0)