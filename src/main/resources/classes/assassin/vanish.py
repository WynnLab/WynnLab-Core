from org.bukkit import Bukkit, Particle, Sound
from org.bukkit.potion import PotionEffect, PotionEffectType
from org.bukkit.util import Vector

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        v = self.player.getEyeLocation().getDirection()
        self.player.setVelocity(v.clone().setY(v.getY() * (.6 if v.getY() >= 0 else .2)).multiply(1.3).add(Vector(0, .5, 0)))

        self.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 100, 4, True, False, True))
        self.player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 4, True, False, True))
        self.player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, True, False, True))

        self.particle(self.player.getEyeLocation(), Particle.SQUID_INK, 30, 1, 2, 1, .2)
        self.particle(self.player.getEyeLocation(), Particle.SPELL_WITCH, 40, 1, 2, 1, .5)

        self.sound(Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1.6)
        self.sound(Sound.ENTITY_GHAST_SHOOT, .6, 1)

        for p in Bukkit.getOnlinePlayers():
            p.hidePlayer(plugin, self.player)
        #TODO: Remove Vanish, Shadow Clone (?)