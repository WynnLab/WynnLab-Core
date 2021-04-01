from org.bukkit import Particle, Sound
from org.bukkit.util import Vector

from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def tick(self):
        if self.t < 10:
            for i in range(3):
                l = self.player.getLocation().clone().add(Math.sin((10 * self.t + 60 * (i - 1)) * RAD2DEG) * (1.5 - .15 * self.t), self.t, Math.cos((10 * self.t + 60 * (i - 1)) * RAD2DEG) * (1.5 - .15 * self.t))
                self.player.spawnParticle(Particle.FIREWORKS_SPARK if self.clone else Particle.CRIT, l, 5, 0, 0, 0, .1 if self.clone else .3)
                self.player.spawnParticle(Particle.SQUID_INK, l, 3, 0, 0, 0, 0)
                self.player.playSound(l, Sound.ENTITY_ARROW_SHOOT, 1, 1 + self.t / 20)
            return

        l = self.player.getLocation().clone().add(0, 10, 0)
        self.player.playSound(l, Sound.ENTITY_ARROW_SHOOT, 1, .8)
        self.player.playSound(l, Sound.ITEM_ARMOR_EQUIP_GOLD if self.clone else Sound.ENTITY_GENERIC_EXPLODE, .8, 1 if self.clone else 1.8)
        if self.clone:
            self.player.playSound(l, Sound.ITEM_TOTEM_USE, .8, .8)

        for i in range(0, 360, 30):
            for j in range(3):
                arrow = self.player.getWorld().spawnArrow(l, Vector(Math.sin(i * RAD2DEG), .5 * (j - 1) - 1, Math.cos(i * RAD2DEG)), 3, 1)
                #TODO: flint

                arrow.setShooter(self.player)
                arrow.addScoreboardTag('rain_arrow')

def delete_arrow(event):
    event.getEntity().remove()

PySpell.registerProjectileHit('rain_arrow', delete_arrow)