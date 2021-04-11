from org.bukkit import Material, Sound
from org.bukkit.entity import Snowball
from org.bukkit.inventory import ItemStack

from com.wynnlab import Classes
from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        self.sound(Sound.ENTITY_ENDER_PEARL_THROW if self.clone else Sound.ENTITY_SNOWBALL_THROW, .8, 1.3)
        self.sound(Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, .8, 1.3)
        self.sound(Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1.6)

        v = self.player.getEyeLocation().getDirection().clone().multiply(3)
        snowballs = (
            self.player.launchProjectile(Snowball, v),
            self.player.launchProjectile(Snowball, v.rotateAroundY(.4)),
            self.player.launchProjectile(Snowball, v.rotateAroundY(-.8))
        )

        for snowball in snowballs:
            if self.clone:
                snowball.setItem(ItemStack(Material.ENDER_PEARL))
            snowball.addScoreboardTag('smoke_bomb')

def bomb_hit(event):
    event.getEntity().remove()
    #TODO: Classes.getClasses().get('ASSASSIN').getSpells().get(5).cast(event.getEntity().getShooter())

PySpell.registerProjectileHit('bomb_arrow', bomb_hit)