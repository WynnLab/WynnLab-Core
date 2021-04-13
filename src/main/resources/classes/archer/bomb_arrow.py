from org.bukkit import Material, Particle, Sound
from org.bukkit.entity import Arrow, EntityType, Snowball
from org.bukkit.inventory import ItemStack
from org.bukkit.potion import PotionEffect, PotionEffectType

from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def tick(self):
        self.sound(Sound.ENTITY_ARROW_SHOOT, .9, .7)
        self.sound(Sound.ENTITY_TNT_PRIMED, 1, 1.3)

        bomb = self.player.launchProjectile(Snowball if self.clone else Arrow, self.player.getEyeLocation().getDirection().multiply(3))
        bomb.addScoreboardTag('bomb_arrow')
        if self.clone:
            bomb.setItem(ItemStack(Material.FLINT))

def bomb_hit(event):
    arrow = event.getEntity()

    player = arrow.getShooter()
    clone = player.getScoreboardTags().contains('clone')

    if not arrow.getScoreboardTags().contains('bomb_hit_2'):
        next = arrow.getWorld().spawnArrow(arrow.getLocation().clone().add(0, .2, 0), arrow.getVelocity().clone().rotateAroundAxis(arrow.getFacing().getDirection(), Math.PI / 2).multiply(3).setY(1), .6, 0)
        if clone:
            snowball = arrow.getWorld().spawnEntity(next.getLocation(), EntityType.SNOWBALL)
            snowball.setVelocity(next.getVelocity())
            snowball.setItem(ItemStack(Material.FLINT))
            next.remove()
            next = snowball

        next.setShooter(player)
        next.addScoreboardTag('bomb_arrow')
        next.addScoreboardTag('bomb_hit_2' if arrow.getScoreboardTags().contains('bomb_hit_1') else 'bomb_hit_1')

    for e in PySpell.nearbyMobs(arrow.getWorld(), arrow.getLocation(), 4, 4, 4):
        PySpell.damage(player, e, 14)
        e.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 60, 4, True, False, True))

    PySpell.sound(player, arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1.3)
    if clone:
        PySpell.sound(player, arrow.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, .9, 1.1)
    particle_location = arrow.getLocation().clone().add(0, .5, 0)
    PySpell.particle(player, particle_location, Particle.FIREWORKS_SPARK if clone else Particle.FLAME, 20, .3, .3, .3, .1)
    PySpell.particle(player, particle_location, Particle.VILLAGER_HAPPY if clone else Particle.SQUID_INK, 10, .3, .3, .3, .1)
    if not clone:
        PySpell.particle(player, particle_location, Particle.CLOUD, 5, .2, .2, .2, .1)
    PySpell.particle(player, particle_location, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0)

    arrow.remove()

PySpell.registerProjectileHit('bomb_arrow', bomb_hit)