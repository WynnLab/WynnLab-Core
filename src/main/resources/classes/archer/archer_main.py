from org.bukkit import Material, Sound
from org.bukkit.entity import Arrow, Snowball
from org.bukkit.inventory import ItemStack

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        self.player.playSound(self.player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1)

        arrow = self.player.launchProjectile(Snowball if self.clone else Arrow, self.player.getEyeLocation().getDirection().clone().multiply(3))
        if self.clone:
            arrow.setItem(ItemStack(Material.FLINT))

        arrow.addScoreboardTag('arrow')
        arrow.setShooter(self.player)

def delete_arrow(event):
    hit = event.getHitEntity()
    if not hit is None:
        hit.damage(4, event.getEntity().getShooter())
        hit.setNoDamageTicks(0)

    event.getEntity().remove()

PySpell.registerProjectileHit('arrow', delete_arrow)