#function tick() {
#    this.sound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .8);
#    var l = this.player.getEyeLocation().clone().add(this.player.getEyeLocation().getDirection());
#    this.particle(l, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
#
#    this.nearbyMobs(l, 2, 2, 2).forEach(function (e) {
#        this.damage(e, 2);
#    });
#}
from org.bukkit import Particle, Sound
from org.bukkit.potion import PotionEffectType

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        if self.player.hasPotionEffect(PotionEffectType.INVISIBILITY):
            self.castSpell('ASSASSIN', 5)

        self.sound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .8)
        l = self.player.getEyeLocation().clone().add(self.player.getEyeLocation().getDirection())
        self.particle(l, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0)

        for e in self.nearbyMobs(l, 2, 2, 2):
            self.damage(e, 2)