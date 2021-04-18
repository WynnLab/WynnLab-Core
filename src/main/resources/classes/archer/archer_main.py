from org.bukkit import Material, Sound
from org.bukkit.entity import Arrow, Player, Snowball
from org.bukkit.inventory import ItemStack

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        self.sound(Sound.ENTITY_ARROW_SHOOT, 1, 1)

        arrow = self.player.launchProjectile(Snowball if self.clone else Arrow, self.player.getEyeLocation().getDirection().multiply(3))
        if self.clone:
            arrow.setItem(ItemStack(Material.FLINT))

        arrow.addScoreboardTag('arrow')
        arrow.setShooter(self.player)

def delete_arrow(event):
    hit = event.getHitEntity()
    if not hit is None and not isinstance(hit, Player):
        PySpell.damage(event.getEntity().getShooter(), hit, True, 1)
        PySpell.knockbackFromPlayer(hit, event.getEntity().getShooter(), .5)

    event.getEntity().remove()

PySpell.registerProjectileHit('arrow', delete_arrow)