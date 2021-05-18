from org.bukkit import Material, Particle, Sound
from org.bukkit.entity import EntityType, Player
from org.bukkit.inventory import ItemStack
from org.bukkit.util import Vector

from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def tick(self):
        if self.t < 10:
            for i in range(3):
                l = self.player.getLocation().clone().add(Math.sin((10 * self.t + 60 * (i - 1)) * DEG2RAD) * (1.5 - .15 * self.t), self.t, Math.cos((10 * self.t + 60 * (i - 1)) * DEG2RAD) * (1.5 - .15 * self.t))
                self.particle(l, Particle.FIREWORKS_SPARK if self.clone else Particle.CRIT, 5, 0, 0, 0, .1 if self.clone else .3)
                self.particle(l, Particle.SQUID_INK, 3, 0, 0, 0, 0)
                self.sound(l, Sound.ENTITY_ARROW_SHOOT, 1, 1 + self.t / 20)
            return

        l = self.player.getLocation().clone().add(0, 10, 0)
        self.sound(l, Sound.ENTITY_ARROW_SHOOT, 1, .8)
        self.sound(l, Sound.ITEM_ARMOR_EQUIP_GOLD if self.clone else Sound.ENTITY_GENERIC_EXPLODE, .8, 1 if self.clone else 1.8)
        if self.clone:
            self.sound(l, Sound.ITEM_TOTEM_USE, .8, .8)

        for i in range(0, 360, 30):
            for j in range(3):
                arrow = self.player.getWorld().spawnArrow(l, Vector(Math.sin(i * DEG2RAD), .5 * (j - 1) - 1, Math.cos(i * DEG2RAD)), 3, 1)
                if self.clone:
                    snowball = self.player.getWorld().spawnEntity(arrow.getLocation(), EntityType.SNOWBALL)
                    snowball.setVelocity(arrow.getVelocity())
                    snowball.setItem(ItemStack(Material.FLINT))
                    arrow.remove()
                    arrow = snowball

                arrow.setShooter(self.player)
                arrow.addScoreboardTag('rain_arrow')

def delete_arrow(event):
    hit = event.getHitEntity()
    if not hit is None and (isinstance(hit, Mob) or isinstance(hit, Player) and hit.getScoreboardTags().contains('pvp') and event.getEntity().getShooter().getScoreboardTags().contains('pvp')):
        PySpell.damage(event.getEntity().getShooter(), hit, False, 2, .7, 0, 0, 0, 0, .3)
        PySpell.knockbackFromPlayer(hit, event.getEntity().getShooter(), .5)

    event.getEntity().remove()

PySpell.registerProjectileHit('rain_arrow', delete_arrow)