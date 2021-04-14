from org.bukkit import Material, Particle, Sound
from org.bukkit.util import Vector

from com.wynnlab.spells import PySpell
from com.wynnlab.util import BukkitUtils

class Spell(PySpell):
    def __init__(self):
        self.ripple_loc = None
        self.ripple_dir = None
        self.hit = False
        self.shift = False

    def init(self):
        self.ripple_loc = self.player.getLocation().clone().add(0, .1, 0)
        self.ripple_dir = BukkitUtils.normalizeOnXZ(self.player.getEyeLocation().getDirection())
        self.shift = self.player.isSneaking()

    def tick(self):
        if self.t == 0 and self.clone:
            self.sound(Sound.ENTITY_EVOKER_CAST_SPELL, .7, .7)

        if self.t <= 5:
            self.ripple_loc.add(self.ripple_dir)

            self.sound(self.ripple_loc, Sound.BLOCK_GLASS_BREAK if self.clone else Sound.ENTITY_WITHER_BREAK_BLOCK, 1 if self.clone else .2, 1)
            if self.clone:
                self.sound(self.ripple_loc, Sound.BLOCK_LAVA_EXTINGUISH, .6, 1.2)

            self.particle(self.ripple_loc, Particle.BLOCK_CRACK, 10, .5, .5, .5, .5, (Material.GLOWSTONE if self.clone else Material.DIRT).createBlockData())

            for e in self.nearbyMobs(self.ripple_loc, 3, 3, 3):
                self.castSpell('WARRIOR', 5, e)
                self.hit = True

        if self.t == 5 and self.hit and self.shift:
            self.player.setVelocity(Vector(0, 1.5, 0))