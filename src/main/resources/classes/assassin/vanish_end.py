from org.bukkit import Bukkit, Particle, Sound
from org.bukkit.potion import PotionEffectType

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        self.player.removePotionEffect(PotionEffectType.SPEED)
        self.player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE)
        self.player.removePotionEffect(PotionEffectType.INVISIBILITY)

        self.particle(self.player.getEyeLocation(), Particle.SQUID_INK, 30, 1, 2, 1, .2)
        self.particle(self.player.getEyeLocation(), Particle.SPELL_WITCH, 40, 1, 2, 1, .5)

        self.sound(Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1.6)
        self.sound(Sound.ENTITY_GHAST_SHOOT, .6, 1)

        for p in Bukkit.getOnlinePlayers():
            p.showPlayer(plugin, self.player)