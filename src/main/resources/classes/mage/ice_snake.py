from org.bukkit import Material, Particle, Sound
from org.bukkit.entity import Mob, Player
from org.bukkit.potion import PotionEffect, PotionEffectType

from com.wynnlab.spells import SpellL
from com.wynnlab.api import Bukkit_utilsKt

class Spell(SpellL):
    def __init__(self):
        self.hit = {}
        self.ice_loc = self.player.getLocation().clone().add(0, 1, 0)

    def tick(self):
        self.ice_loc.add(Bukkit_utilsKt.normalizeOnXZ(self.player.getEyeLocation().getDirection().clone()))

        ice_block = self.player.getWorld().spawnFallingBlock(self.ice_loc, Material.OBSIDIAN.createBlockData() if self.clone else Material.PACKED_ICE.createBlockData())
        ice_block.setDropItem(False)
        ice_block.setHurtEntities(False)

        self.player.spawnParticle(Particle.FIREWORKS_SPARK, self.ice_loc, 1, 1, 1, 1, .1)
        self.player.spawnParticle(Particle.BLOCK_CRACK, self.ice_loc, 9, 1, 1, 1, 1, Material.OBSIDIAN.createBlockData() if self.clone else Material.IRON_SHOVEL.createBlockData())
        self.player.playSound(self.ice_loc, Sound.BLOCK_STONE_PLACE if self.clone else Sound.BLOCK_GLASS_BREAK, 2 if self.clone else 1, .75)
        self.player.playSound(self.ice_loc, Sound.ENTITY_WITHER_BREAK_BLOCK if self.clone else Sound.BLOCK_FIRE_EXTINGUISH, .25 if self.clone else .5, 1.5 if self.clone else 1)

        for e in self.ice_loc.getNearbyEntities(1, 1, 1):
            if isinstance(e, Player):
                continue
            if not isinstance(e, Mob):
                continue

            if e in self.hit:
                continue
            self.hit.add(e)

            e.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 300, 4))
            e.damage(1, self.player)
            e.setNoDamageTicks(0)
