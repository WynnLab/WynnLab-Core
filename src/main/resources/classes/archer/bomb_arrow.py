from org.bukkit import Material, Particle, Sound
from org.bukkit.entity import Arrow, EntityType, Mob, Player, Snowball
from org.bukkit.inventory import ItemStack
from org.bukkit.potion import PotionEffect, PotionEffectType

from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def tick(self):
        self.player.playSound(self.player.getLocation(), Sound.ENTITY_ARROW_SHOOT, .9, .7)
        self.player.playSound(self.player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1.3)

        bomb = self.player.launchProjectile(Snowball if self.clone else Arrow, self.player.getEyeLocation().getDirection().clone().multiply(3))
        bomb.addScoreboardTag('bomb_arrow')
        if self.clone:
            bomb.setItem(ItemStack(Material.FLINT))

def bomb_hit(event):
    arrow = event.getEntity()

    player = arrow.getShooter()
    clone = player.getScoreboardTags().contains('clone')

    if not arrow.getScoreboardTags().contains('bomb_hit_2'):
        next = arrow.getWorld().spawnArrow(arrow.getLocation().clone().add(0, .2, 0), arrow.getVelocity().clone().rotateAroundAxis(arrow.getFacing().getDirection(), Math.PI / 2).multiply(3), .6, 0)
        if clone:
            snowball = arrow.getWorld().spawnEntity(next.getLocation(), EntityType.SNOWBALL)
            snowball.setVelocity(next.getVelocity())
            snowball.setItem(ItemStack(Material.FLINT))
            next.remove()
            next = snowball

        next.setShooter(player)
        next.addScoreboardTag('bomb_hit_2' if arrow.getScoreboardTags().contains('bomb_hit_1') else 'bomb_hit_1')

    for e in arrow.getNearbyEntities(arrow.getLocation(), 4, 4, 4):
        if isinstance(e, Player):
            continue
        if not isinstance(e, Mob):
            continue

        e.damage(14, player)
        e.setNoDamageTicks(0)
        e.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 60, 4, True, False))

    player.playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1.3)
    if clone:
        player.playSound(arrow.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, .9, 1.1)
    particle_location = arrow.getLocation().clone().add(0, .5, 0)
    player.spawnParticle(Particle.FIREWORKS_SPARK if clone else Particle.FLAME, particle_location, 20, .3, .3, .3, .1)
    player.spawnParticle(Particle.VILLAGER_HAPPY if clone else Particle.SQUID_INK, particle_location, 10, .3, .3, .3, .1)
    if not clone:
        player.spawnParticle(Particle.CLOUD, particle_location, 5, .2, .2, .2, .1)
    player.spawnParticle(Particle.EXPLOSION_LARGE, particle_location, 1, 0, 0, 0, 0)

    arrow.remove()

PySpell.registerProjectileHit('bomb_arrow', bomb_hit)