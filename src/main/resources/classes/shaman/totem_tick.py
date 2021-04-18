from org.bukkit import Particle, Sound
from org.bukkit.attribute import Attribute
from org.bukkit.entity import EntityType, Mob, Player

from com.wynnlab.api import PersistentDataAPI
from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def __init__(self, totem, holo, hit):
        self.totem = totem
        self.holo = holo

        self.hit = hit

    def tick(self):
        if self.t == 0:
            PersistentDataAPI.setInt(PersistentDataAPI.getData(self.player), 'totem_task', self.getTaskId())

        if not self.hit:
            if not self.totem.isOnGround():
                self.particle(self.totem.getLocation().clone().add(0, 1, 0), Particle.END_ROD if self.clone else Particle.VILLAGER_HAPPY, 1, 0, 0, 0, 0)
                self.delay()

            else:
                self.hit = True

                self.totem.setMarker(True)

                self.holo = self.player.getWorld().spawnEntity(self.totem.getLocation().clone().add(0, 2.7, 0), EntityType.ARMOR_STAND)

                self.holo.setMarker(True)
                self.holo.setVisible(False)
                self.holo.setInvulnerable(True)
                self.holo.setCustomName(PySpell.colorText('20s', 'c'))
                self.holo.setCustomNameVisible(True)

                PersistentDataAPI.setInt(PersistentDataAPI.getData(self.player), 'totem_holo', self.holo.getEntityId())

                l = self.totem.getLocation()

                self.particle(l.clone().add(0,-1,0), Particle.EXPLOSION_HUGE, 1, 0, 0, 0, 1)
                self.particle(l, Particle.FIREWORKS_SPARK if self.clone else Particle.CRIT_MAGIC, 5, 2, 2, 2, .8)
                self.particle(l, Particle.SQUID_INK, 5, 2, 2, 2, .5)

                self.sound(l, Sound.ENTITY_EVOKER_PREPARE_SUMMON, .9, 1.1)
                self.sound(l, Sound.ENTITY_IRON_GOLEM_DEATH, 1, .8)
                self.sound(l, Sound.ITEM_TOTEM_USE, .9, 1)
                if self.clone:
                    self.sound(l, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1, .7)
                    self.sound(l, Sound.BLOCK_FIRE_EXTINGUISH, .5, .5)
                    self.sound(l, Sound.ENTITY_BLAZE_DEATH, .3, 1)

                for e in self.nearbyMobs(l, 4, 4, 4):
                    self.damage(e, False, 1, .8, 0, 0, 0, .2, 0)
                    PySpell.knockback(e, VectorUP, .5)

        else:
            if self.totem.isOnGround() and self.t >= 20:
                self.totem.setMarker(True)

            if self.t < 400:
                if self.t % 20 == 0:
                    self.holo.setCustomName(PySpell.colorText('{0}s'.format((400 - self.t) // 20), 'c'))

                    for e in self.player.getWorld().getNearbyEntities(self.totem.getLocation(), 8, 4, 8):
                        if not isinstance(e, Mob):
                            continue

                        if isinstance(e, Player):
                            PySpell.heal(e, self.player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 10)

                        else:
                            self.damage(e, False, .2, .8, 0, 0, 0, 0, .2)

                l = self.totem.getLocation().clone().add(Math.sin(self.t * 10 * DEG2RAD) * 8, .7, Math.cos(self.t * 10 * DEG2RAD) * 8)
                self.particle(l, Particle.FIREWORKS_SPARK if self.clone else Particle.TOTEM, 1, 0, 0, 0, 0)

                self.particle(self.totem.getLocation().clone().add(0, 1, 0), Particle.SPELL_MOB, 2, .5, 1, .5, .5)

                self.holo.teleport(self.totem.getLocation().clone().add(0, 2.7, 0))

            else:
                self.totem.remove()
                self.holo.remove()

                self.player.removeScoreboardTag('totem')