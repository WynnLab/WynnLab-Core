from org.bukkit import Particle, Sound
from org.bukkit.potion import PotionEffect, PotionEffectType
from org.bukkit.util import Vector

from com.wynnlab.api import PersistentDataAPI, PlayerAPI
from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def __init__(self):
        self.totem = None
        self.hit = set()

    def tick(self):
        if self.t == 0:
            # Find totem
            totem_id = PersistentDataAPI.getInt(PersistentDataAPI.getData(self.player), 'totem')
            for e in self.player.getNearbyEntities(16, 16, 16):
                if e.getEntityId() == totem_id:
                    self.totem = e

            if self.totem is None:
                PlayerAPI.sendWynnMessage(self.player, 'messages.totem_out')
                self.cancel()
                return

            self.sound(self.totem.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, .4, 1)
            self.sound(self.totem.getLocation(), Sound.ENTITY_SHULKER_SHOOT, .9, .7)
            self.sound(self.totem.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1.3)
            self.sound(self.totem.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, .7, .5)

            if self.clone:
                self.sound(self.totem.getLocation(), Sound.ENTITY_BLAZE_SHOOT, .7, .7)
                self.sound(self.totem.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .4, .6)

        elif self.t <= 10:
            i = 0
            while i < 360:
                l = self.totem.getLocation().clone().add(Math.sin(i * DEG2RAD) * (self.t + 1), .4, Math.cos(i * DEG2RAD) * (self.t + 1))

                self.particle(l, Particle.SPELL_MOB if self.clone else Particle.CRIT_MAGIC, 0 if self.clone else 1, self.clone, self.clone, self.clone, self.clone)
                self.damageAndPull(l)

                i += 15 - self.t

        elif 15 < self.t <= 25:
            i = 0
            while i < 360:
                l = self.totem.getLocation().clone().add(Math.sin(i * DEG2RAD) * (26 - self.t), .4, Math.cos(i * DEG2RAD) * (26 - self.t))

                self.particle(l, Particle.SPELL_MOB if self.clone else Particle.CRIT_MAGIC, 0 if self.clone else 1, self.clone, self.clone, self.clone, self.clone)
                self.damageAndPull(l)

                i += self.t - 14

        if self.t % 10 == 0:
            for i in range(0, 360, 20):
                self.particle(self.totem.getLocation().clone().add(Math.sin(i * DEG2RAD) * 2, 1.6, Math.cos(i * DEG2RAD) * 2), Particle.FIREWORKS_SPARK, 1, 0, 0, 0, 0)

            for e in self.nearbyMobs(self.totem.getLocation(), 1, 1, 1):
                e.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20, 9, True, False))

        if self.t == 10 or self.t == 25:
            self.hit = set()

    def damageAndPull(self, l):
        for e in self.nearbyMobs(l, .5, 2, .5):
            pull_dir = self.totem.getLocation().clone().subtract(e.getLocation()).toVector()
            pull_dir = Vector(Math.max(Math.min(pull_dir.getX() / 5, 1), -1), 0.2, Math.max(Math.min(pull_dir.getZ() / 5, 1), -1))

            e.setVelocity(pull_dir)

            if e in self.hit:
                continue
            self.hit.add(e)

            self.damage(e, False, 2, .7, 0, 0, .3, 0, 0)