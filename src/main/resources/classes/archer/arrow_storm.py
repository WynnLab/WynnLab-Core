from org.bukkit import Material, Particle, Sound
from org.bukkit.entity import Arrow, Snowball
from org.bukkit.inventory import ItemStack

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def __init__(self):
        self.arrows = set()

    def tick(self):
        if self.t == 0:
            self.player.spawnParticle(Particle.VILLAGER_HAPPY if self.clone else Particle.CRIT, self.player.getLocation(), 5)

        self.player.playSound(self.player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1)
        if self.clone:
            self.player.playSound(self.player.getLocation(), Sound.BLOCK_GLASS_BREAK, .05, .6)

        for arrow in self.arrows:
            self.player.spawnParticle(Particle.CRIT, arrow.getLocation(), 1, 0, 0, 0, .01)
            if self.clone:
                self.player.spawnParticle(Particle.CRIT_MAGIC, arrow.getLocation(), 1, 0, 0, 0, .01)

        vi = self.player.getEyeDirection().clone().multiply(3)
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
    event.getEntity().remove()

PySpell.registerProjectileHit('storm_arrow', delete_arrow)