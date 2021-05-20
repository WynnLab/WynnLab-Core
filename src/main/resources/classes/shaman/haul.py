from org.bukkit import Particle, Sound
from org.bukkit.potion import PotionEffect, PotionEffectType
from org.bukkit.util import Vector

from com.wynnlab.api import PersistentDataAPI, PlayerAPI
from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def __init__(self):
        self.hit = set()

    def tick(self):
        if self.t == 0:
            # Find totem
            totem = None
            totem_id = PersistentDataAPI.getInt(PersistentDataAPI.getData(self.player), 'totem', None)
            for e in self.player.getWorld().getEntities():
                if e.getEntityId() == totem_id:
                    totem = e

            if totem is None:
                PlayerAPI.sendWynnMessage(self.player, 'messages.totem_out')
                self.cancel()
                return

            vector = totem.getLocation().clone().subtract(self.player.getLocation()).add(0, 2, 0).toVector()
            vector = Vector(Math.max(Math.min(vector.getX(), 6), -6), Math.max(Math.min(vector.getY(), 2), -2), Math.max(Math.min(vector.getZ(), 6), -6))
            self.player.setVelocity(vector.clone().multiply(0.5).setY(vector.getY()).multiply(0.5));

            self.particle(self.player.getLocation(), Particle.CLOUD, 6, 1, 1, 1, 0.1)
            self.particle(self.player.getLocation(), Particle.SQUID_INK, 6, 1, 1, 1, 0.1)

            self.sound(Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, .4, .8)
            self.sound(Sound.ENTITY_BLAZE_SHOOT, 1, 1)
            self.sound(Sound.ENTITY_IRON_GOLEM_HURT, .8, .8)
            if self.clone:
                self.sound(Sound.ENTITY_BLAZE_AMBIENT, .1, .9)

        elif self.t > 10:
            if self.player.isOnGround():
                self.particle(self.player.getLocation().clone().add(0, 1, 0), Particle.FIREWORKS_SPARK if self.clone else Particle.TOTEM, 5, 1, 2, 1, .2)
                self.particle(self.player.getLocation(), Particle.CLOUD, 6, 1, .2, 1, .1)
                self.particle(self.player.getLocation(), Particle.SQUID_INK, 6, 1, .2, 1, .1)
                if self.clone:
                    self.particle(self.player.getLocation(), Particle.SPELL_MOB, 0, 1, 1, 1, 1)

                self.sound(Sound.BLOCK_STONE_STEP, 1, .9)
                self.sound(Sound.BLOCK_STONE_FALL, 1, .9)

                self.player.setVelocity(Vector(0, .5, 0))

                self.cancel()

            else:
                for e in self.nearbyMobs(1, 1, 1):
                    if e in self.hit:
                        continue
                    self.hit.add(e)

                    self.damage(e, False, 1, .8, 0, .2, 0, 0, 0)
                    PySpell.knockback(e, VectorUP, 2)
                    e.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 100, 0, True, False))