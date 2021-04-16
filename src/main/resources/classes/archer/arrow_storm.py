from org.bukkit import Material, Particle, Sound
from org.bukkit.entity import Arrow, Player, Snowball
from org.bukkit.inventory import ItemStack

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def __init__(self):
        self.arrows = set()

    def tick(self):
        if self.t == 0:
            self.particle(self.player.getLocation().clone().add(0, 1, 0), Particle.VILLAGER_HAPPY if self.clone else Particle.CRIT, 5, .2, 2, .2, .5)

        self.sound(Sound.ENTITY_ARROW_SHOOT, 1, 1)
        if self.clone:
            self.sound(Sound.BLOCK_GLASS_BREAK, .05, .6)

        for arrow in self.arrows:
            self.particle(arrow.getLocation(), Particle.CRIT, 1, 0, 0, 0, .01)
            if self.clone:
                self.particle(arrow.getLocation(), Particle.CRIT_MAGIC, 1, 0, 0, 0, .01)
        
        vi = self.player.getEyeLocation().getDirection().multiply(3)
        shots = (
            self.player.launchProjectile(Snowball if self.clone else Arrow, vi),
            self.player.launchProjectile(Snowball if self.clone else Arrow, vi.rotateAroundY(.4)),
            self.player.launchProjectile(Snowball if self.clone else Arrow, vi.rotateAroundY(-.8))
        )
        
        for arrow in shots:
            arrow.setShooter(self.player)
            arrow.addScoreboardTag('storm_arrow')
            if self.clone:
                arrow.setItem(ItemStack(Material.FLINT))

            self.arrows.add(arrow)

def delete_arrow(event):
    hit = event.getHitEntity()
    if not hit is None and not isinstance(hit, Player):
        PySpell.damage(event.getEntity().getShooter(), hit, False, .1, .6, 0, .25, 0, .15, 0)

    event.getEntity().remove()

PySpell.registerProjectileHit('storm_arrow', delete_arrow)