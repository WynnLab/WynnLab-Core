from org.bukkit import Particle, Sound
from org.bukkit.entity import Player
from org.bukkit.potion import PotionEffect, PotionEffectType

from com.wynnlab.spells import PySpell
from com.wynnlab.util import BukkitUtils

from java.lang import Math

class Spell(PySpell):
    def __init__(self):
        self.scream_loc = None
        self.scream_dir = None

    def init(self):
        self.scream_loc = self.player.getEyeLocation().clone()
        self.scream_dir = BukkitUtils.normalizeOnXZ(self.player.getEyeLocation().getDirection())

    def tick(self):
        if self.t == 0:
            self.sound(Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, .5, .5)
            self.sound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1)
            self.sound(Sound.ENTITY_ENDER_DRAGON_GROWL, .4, 1)

            for p in self.player.getWorld().getNearbyEntities(self.player.getLocation(), 8, 8, 8):
                if not isinstance(p, Player):
                    continue

                p.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 4800, 2, True, False, True))
                p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 4800, 1, True, False, True))

        if self.t <= 8:
            for i in range(0, 360, 45):
                l = self.player.getLocation().clone().add(Math.sin(i * DEG2RAD) * (self.t + 1), .2, Math.cos(i * DEG2RAD) * (self.t + 1))
                self.particle(l, Particle.CLOUD if self.clone else Particle.SQUID_INK, 3, .1, 0, .1, .1)
                self.particle(l, Particle.CRIT, 3, .1, 0, .1, .1)
                self.particle(l, Particle.SPELL_MOB if self.clone else Particle.LAVA, 5 if self.clone else 1, .2, .2, .2, .5)

        self.scream_loc.add(self.scream_dir)

        if self.t >= 2:
            self.particle(self.scream_loc, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0)

            for e in self.nearbyMobs(self.scream_loc, 3, 3, 3):
                self.damage(e, 1)