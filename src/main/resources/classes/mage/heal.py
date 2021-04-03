from org.bukkit import Bukkit, Particle, Sound
from org.bukkit.attribute import Attribute
from org.bukkit.entity import Player
from org.bukkit.event.entity import EntityRegainHealthEvent

from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def tick(self):
        if self.t % 20 > 0:
            return

        self.particle(self.player.getLocation().clone().add(0, .5, 0), Particle.PORTAL, 144, 4, 0, 4, .1)
        self.particle(self.player.getLocation().clone().add(0, .3, 0), Particle.CRIT_MAGIC, 144, 4, 0, 4, .1)
        self.particle(self.player.getLocation().clone().add(0, 1, 0), Particle.FIREWORKS_SPARK, 16, .3, 1, .3, .05)
        self.sound(Sound.ENTITY_EVOKER_CAST_SPELL, .5, 1.5)
        self.sound(Sound.BLOCK_LAVA_EXTINGUISH, 1, 1)

        self.player.setHealth(Math.min(self.player.getHealth() + 50, self.player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))

        for e in self.player.getNearbyEntities(4, 4, 4):
            if not isinstance(e, Player):
                continue

            e.setHealth(Math.min(self.player.getHealth() + 50), self.player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
            Bukkit.getPluginManager().callEvent(EntityRegainHealthEvent(self.player, 50, EntityRegainHealthEvent.RegainReason.CUSTOM))

            self.particle(e.getLocation().clone().add(0, 1, 0), Particle.FIREWORKS_SPARK, 16, .3, 1, .3, .05)
