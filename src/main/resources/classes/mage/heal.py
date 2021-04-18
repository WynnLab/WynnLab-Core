from org.bukkit import Bukkit, Particle, Sound
from org.bukkit.attribute import Attribute
from org.bukkit.entity import Player
from org.bukkit.event.entity import EntityRegainHealthEvent

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        if self.t % 20 > 0:
            return

        self.particle(self.player.getLocation().clone().add(0, .5, 0), Particle.PORTAL, 144, 4, 0, 4, .1)
        self.particle(self.player.getLocation().clone().add(0, .3, 0), Particle.CRIT_MAGIC, 144, 4, 0, 4, .1)
        self.particle(self.player.getLocation().clone().add(0, 1, 0), Particle.FIREWORKS_SPARK, 16, .3, 1, .3, .05)
        self.sound(Sound.ENTITY_EVOKER_CAST_SPELL, .5, 1.5)
        self.sound(Sound.BLOCK_LAVA_EXTINGUISH, 1, 1)

        amount = self.player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() // 10
        PySpell.heal(self.player, amount)
        #self.player.sendMessage('{0}{1}{2}'.format(PySpell.colorText('[', '4'), PySpell.colorText('+{a}'.format(a=amount), 'c'), PySpell.colorText(']', '4')))

        for p in self.player.getNearbyEntities(4, 4, 4):
            if not isinstance(p, Player):
                continue

            PySpell.heal(p, amount)
            #p.sendMessage('{0}{1}{2}{3}'.format(PySpell.colorText('[', '4'), PySpell.colorText('+{a}'.format(a=amount), 'c'), PySpell.colorText(']', '4'), PySpell.colorText('({pl})'.format(self.player.getName()), '7')))
            Bukkit.getPluginManager().callEvent(EntityRegainHealthEvent(p, 50, EntityRegainHealthEvent.RegainReason.CUSTOM))

            self.particle(p.getLocation().clone().add(0, 1, 0), Particle.FIREWORKS_SPARK, 16, .3, 1, .3, .05)
