from org.bukkit import Material, Particle, Sound
from org.bukkit.potion import PotionEffect, PotionEffectType

from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def __init__(self):
        self.bash_loc = None

    def tick(self):
        if self.t == 0:
            self.bash_loc = self.player.getLocation().clone().add(self.player.getEyeLocation().getDirection().multiply(2).setY(1))
            self.bash()

            self.sound(self.bash_loc, Sound.ENTITY_GHAST_SHOOT, 1, 1)
            if self.clone:
                self.sound(self.bash_loc, Sound.ENTITY_EVOKER_CAST_SPELL, 1, .4)

        elif self.t == 10:
            self.bash_loc.add(self.player.getEyeLocation().getDirection().multiply(4).setY(0))
            self.bash()

            self.sound(self.bash_loc, Sound.ENTITY_WITHER_BREAK_BLOCK, .4 if self.clone else .8, 1.8 if self.clone else .8)
            if self.clone:
                self.sound(self.bash_loc, Sound.BLOCK_GLASS_BREAK, 1, 1)
                self.sound(self.bash_loc, Sound.ENTITY_BLAZE_AMBIENT, .2, .8)

            for i in range(0, 360, 36):
                block_loc = self.bash_loc.clone().add(Math.sin(i * DEG2RAD) * 3, .5, Math.cos(i * DEG2RAD) * 3)
                block_data = (Material.GLOWSTONE if self.clone else Material.DIRT).createBlockData()

                self.particle(block_loc, Particle.BLOCK_CRACK, 10, .5, .5, .5, .2, block_data)

                block = self.player.getWorld().spawnFallingBlock(block_loc, block_data)
                block.setDropItem(False)
                block.setVelocity(block_loc.subtract(self.bash_loc).toVector().multiply(.05).setY(.4))


    def bash(self):
        if not self.clone:
            self.particle(self.bash_loc, Particle.SMOKE_LARGE, 15, .5, .5, .5, .1)
        self.particle(self.bash_loc, Particle.FIREWORKS_SPARK if self.clone else Particle.SQUID_INK, 10 if self.clone else 5, .4, .4, .4, .4 if self.clone else .2)
        self.particle(self.bash_loc, Particle.CLOUD, 5, .2, .2, .2, .2)
        self.particle(self.bash_loc, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0)
        self.sound(self.bash_loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1)

        hit_count = 0
        for e in self.nearbyMobs(self.bash_loc, 6, 6, 6):
            self.damage(e, False, 1.3, .6, .4, 0, 0, 0, 0)
            self.knockback(e, 2)
            hit_count += 1

        self.player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, hit_count * 20, 1, True, False, True))