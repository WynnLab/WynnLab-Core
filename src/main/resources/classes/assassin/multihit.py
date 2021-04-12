from org.bukkit import Particle, Sound
from org.bukkit.potion import PotionEffectType

from com.wynnlab.spells import PySpell
from com.wynnlab.util import BukkitUtils

class Spell(PySpell):
    def __init__(self):
        self.l = None
        self.entities = None
        self.shift = False

    def init(self):
        self.shift = self.player.isSneaking()

    def tick(self):
        if self.t % 2 != 0:
            return

        if self.t == 0:
            if self.player.hasPotionEffect(PotionEffectType.INVISIBILITY):
                self.castSpell('ASSASSIN', 5)

            self.sound(Sound.ENTITY_PLAYER_ATTACK_STRONG, .5, 1)
            self.sound(Sound.ENTITY_IRON_GOLEM_HURT, 1, 1.5)
            if self.clone:
                self.sound(Sound.ENTITY_BLAZE_AMBIENT, .3, 1.5)

            v = BukkitUtils.normalizeOnXZ(self.player.getEyeLocation().getDirection().clone())
            self.l = self.player.getLocation().clone().add(v).add(0, .5, 0)

            self.particle(self.l, Particle.SWEEP_ATTACK, 5, .5, .5, .5, .1)

            self.entities = self.nearbyMobs(self.l, 3, 3, 3)

            self.particle(self.l.add(v), Particle.SWEEP_ATTACK, 5, .5, .5, .5, .1)
            self.particle(self.l.add(v), Particle.SWEEP_ATTACK, 5, .5, .5, .5, .1)

        elif self.t <= 20:
            for e in self.entities:
                e.setVelocity(self.player.getEyeLocation().getDirection().clone().multiply(.05 if self.shift else .3).setY(.2).rotateAroundY((.1 * self.t) if self.t % 2 == 0 else (-.1 * self.t)))

                self.particle(e.getLocation(), Particle.SWEEP_ATTACK, 5, .5, .5, .5, .5)
                if self.clone:
                    self.particle(e.getLocation(), Particle.SPELL_WITCH, 7, .5, .5, .5, .2)
                self.particle(e.getLocation(), Particle.SQUID_INK, 6, .5, .5, .5, .1)
                self.particle(e.getLocation(), Particle.CRIT, 7 if self.clone else 10, .5, .5, .5, .1)

                self.sound(e.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1.3)
                self.sound(e.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, .8, 1.6)

                self.damage(e, 1)

        else:
            for e in self.entities:
                if not self.shift:
                    e.setVelocity(self.player.getEyeLocation().getDirection().clone().setY(.5))

                self.sound(e.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1.3)

                self.damage(e, 6)
                if self.clone:
                    self.sound(e.getLocation(), Sound.ENTITY_BLAZE_DEATH, 1, 1.2)
                    self.sound(e.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1, 1.6)
                    self.sound(e.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, .5, 1)
