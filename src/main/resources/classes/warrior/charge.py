from org.bukkit import Particle, Sound
from org.bukkit.potion import PotionEffect, PotionEffectType

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def __init__(self):
        self.ground_start = False

    def tick(self):
        if self.t == 0:
            p_loc = self.player.getLocation().clone().add(0, 1, 0)
            self.particle(p_loc, Particle.VILLAGER_HAPPY if self.clone else Particle.SMOKE_LARGE, 5, .5, 2, .5, 0.1)
            self.particle(p_loc, Particle.CLOUD, 5, .5, 2, .5, .3)
            self.particle(p_loc, Particle.SQUID_INK, 8, .5, 2, .5, .2)

            self.sound(Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK if self.clone else Sound.ENTITY_PLAYER_ATTACK_CRIT, .3, .6)
            self.sound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .8, 1)
            self.sound(Sound.ENTITY_BLAZE_SHOOT, 1, 1.1)

            if self.player.isOnGround():
                self.ground_start = True
            #else:
            #    self.player.playSound(self.player.getLocation(), Sound.ITEM_ELYTRA_FLYING, 1, .9)

            self.player.setVelocity(self.player.getEyeLocation().getDirection().setY(1) if self.ground_start else self.player.getEyeLocation().getDirection().multiply(2).setY(.2))

        if self.t <= 5:
            self.particle(self.player.getLocation(), Particle.CLOUD if self.clone else Particle.SQUID_INK, 3, .2, .2, .2, .6)

        elif self.t < 10:
            if not self.player.isOnGround():
                self.player.setVelocity(self.player.getEyeLocation().getDirection().multiply(2).setY(-.3))
                self.particle(self.player.getLocation(), Particle.FIREWORKS_SPARK if self.clone else Particle.LAVA, 5 if self.clone else 2, .1, .1, .1, .4)
            else:
                self.particle(self.player.getLocation().clone().add(0, 2, 0), Particle.VILLAGER_HAPPY if self.clone else Particle.FLAME, 1, 0, 0, 0, .05)

                self.sound(Sound.ENTITY_GENERIC_EXPLODE, 1, 1.3)
                self.sound(Sound.BLOCK_STONE_STEP, 1, 1.3)
                if self.clone:
                    self.sound(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1.3)
                    self.sound(Sound.ENTITY_BLAZE_AMBIENT, .2, 1.3)

                #self.player.stopSound(Sound.ITEM_ELYTRA_FLYING)

                self.cancel()

        else:
            self.sound(Sound.ENTITY_GENERIC_EXPLODE, 1, 1.3)
            self.sound(Sound.BLOCK_STONE_STEP, 1, 1.3)

            #self.player.stopSound(Sound.ITEM_ELYTRA_FLYING)

        hit_count = 0
        for e in self.nearbyMobs(1.5, 2, 1.5):
            self.damage(e, False, 1.5, .6, 0, 0, 0, .4, 0)
            PySpell.knockback(e, e.getLocation().clone().subtract(self.player.getLocation()).toVector(), 1)
            hit_count += 1

        self.player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, hit_count * 20, 1, True, False, True))
