from org.bukkit import Material, Sound
from org.bukkit.entity import Arrow, Snowball
from org.bukkit.inventory import ItemStack

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        self.player.playSound(self.player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1)
        if self.clone:
            arrow = self.player.launchProjectile(Snowball, self.player.getExeLocation().getDirection().clone().multiply(3))
            arrow.addScoreboardTag('arrow')
            arrow.setItem(ItemStack(Material.FLINT))
        else:
            arrow = self.player.launchProjectile(Arrow, self.player.getExeLocation().getDirection().clone().multiply(3))
            arrow.addScoreboardTag('arrow')

def delete_arrow(event):
    event.getEntity().remove()

PySpell.registerProjectileHit('arrow', delete_arrow)