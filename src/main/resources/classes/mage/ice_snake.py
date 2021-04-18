from org.bukkit import Material, Particle, Sound
from org.bukkit.potion import PotionEffect, PotionEffectType

from com.wynnlab.spells import PySpell
from com.wynnlab.util import BukkitUtils

class Spell(PySpell):
    def __init__(self):
        self.hit = set()
        self.ice_loc = None

    def init(self):
        self.ice_loc = self.player.getLocation().clone().add(0, 1, 0)

    def tick(self):
        self.ice_loc.add(BukkitUtils.normalizeOnXZ(self.player.getEyeLocation().getDirection()))

        ice_block = self.player.getWorld().spawnFallingBlock(self.ice_loc, Material.OBSIDIAN.createBlockData() if self.clone else Material.PACKED_ICE.createBlockData())
        ice_block.setDropItem(False)
        ice_block.setHurtEntities(False)

        self.particle(self.ice_loc, Particle.FIREWORKS_SPARK, 1, 1, 1, 1, .1)
        self.particle(self.ice_loc, Particle.BLOCK_CRACK, 9, 1, 1, 1, 1, Material.OBSIDIAN.createBlockData() if self.clone else Material.ICE.createBlockData())
        self.sound(self.ice_loc, Sound.BLOCK_STONE_PLACE if self.clone else Sound.BLOCK_GLASS_BREAK, 2 if self.clone else 1, .75)
        self.sound(self.ice_loc, Sound.ENTITY_WITHER_BREAK_BLOCK if self.clone else Sound.BLOCK_FIRE_EXTINGUISH, .25 if self.clone else .5, 1.5 if self.clone else 1)

        for e in self.nearbyMobs(self.ice_loc, 1, 1, 1):
            if e in self.hit:
                continue
            self.hit.add(e)

            e.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 300, 4))
            self.damage(e, False, .7, .5, 0, 0, .5, 0, 0)
            PySpell.knockback(e, VectorUP, .5)
