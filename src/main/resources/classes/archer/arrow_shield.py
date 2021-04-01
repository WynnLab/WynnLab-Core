from org.bukkit import Material, Particle, Sound
from org.bukkit.entity import ArmorStand, Mob, Player
from org.bukkit.inventory import ItemStack
from org.bukkit.util import EulerAngle, Vector

from com.wynnlab import Classes
from com.wynnlab.api import PersistentDataAPI
from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def __init__(self):
        self.stands = None
        self.remaining = 3
        self.cooldown = 0

    def tick(self):
        if self.t == 0:
            if not self.player.getScoreboardTags().contains('arrow_shield'):
                self.player.addScoreboardTag('arrow_shield')
                PersistentDataAPI.setInt(PersistentDataAPI.getData(self.player), 'arrow_shield', 3)
            else:
                arrows = PersistentDataAPI.getInt(PersistentDataAPI.getData(self.player), 'arrow_shield')
                PersistentDataAPI.setInt(PersistentDataAPI.getData(self.player), 'arrow_shield', 3)
                if arrows <= 0:
                    self.player.removeScoreboardTag('arrow_shield')
                return #TODO

            self.player.playSound(self.player.location, Sound.ITEM_ARMOR_EQUIP_NETHERITE if self.clone else Sound.ENTITY_EVOKER_PREPARE_SUMMON, .5, .9)
            self.player.playSound(self.player.location, Sound.ENTITY_ARROW_SHOOT, 1, .8)
            self.player.playSound(self.player.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1)

            self.stands = (
                self.player.getWorld().spawn(self.player.getLocation().clone(1.3, .2 if self.clone else .7, .75), ArmorStand),
                self.player.getWorld().spawn(self.player.getLocation().clone(-1.3, .2 if self.clone else .7, .75), ArmorStand),
                self.player.getWorld().spawn(self.player.getLocation().clone(0, .2 if self.clone else .7, -1.5), ArmorStand)
            )

            for stand in self.stands:
                stand.setVisible(False)
                stand.setSmall(True)
                stand.setInvulnerable(True)
                stand.setMarker(True)
                stand.addScoreboardTag('shield_arrow')
                PersistentDataAPI.setString(PersistentDataAPI.getData(self.player), 'owner', self.player.getName())
                stand.getEquipment().setHelmet(ItemStack(Material.SHEARS if self.clone else Material.ARROW))
                stand.setHeadPose(EulerAngle(0, 0, Math.PI * -.5 if self.clone else -.75))

        elif self.t < 600:
            self.stands[0].teleport(self.player.getLocation().clone().add(Math.sin((10 * self.t + 60) * -RAD2DEG) * 1.5, .2 if self.clone else .7, Math.cos((10 * self.t + 60) * RAD2DEG) * 1.5))
            self.stands[0].teleport(self.player.getLocation().clone().add(Math.sin((10 * self.t - 60) * -RAD2DEG) * 1.5, .2 if self.clone else .7, Math.cos((10 * self.t - 60) * RAD2DEG) * 1.5))
            self.stands[0].teleport(self.player.getLocation().clone().add(Math.sin((10 * self.t + 180) * -RAD2DEG) * 1.5, .2 if self.clone else .7, Math.cos((10 * self.t + 180) * RAD2DEG) * 1.5))

            self.stands[0].setRotation(-(10 * self.t + 60), 0)
            self.stands[1].setRotation(-(10 * self.t - 60), 0)
            self.stands[2].setRotation(-(10 * self.t + 180), 0)

            for stand in self.stands:
                self.player.spawnParticle(Particle.VILLAGER_HAPPY if self.clone else Particle.CRIT, self.player.getLocation().clone().add(0, .5 if self.clone else .2, 0), 1, 0, 0, 0, 0)
                self.player.spawnParticle(Particle.FIREWORKS_SPARK if self.clone else Particle.CRIT_MAGIC, self.player.getLocation().clone().add(0, .5, 0) if self.clone else self.player.getLocation(), 1, 0, 0, 0, 0)

        else:
            self.player.playSound(self.player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, .1, 1.9)
            self.player.playSound(self.player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1, 1)

            self.deactivate()


        if self.cooldown <= 0:
            for e in self.player.getNearbyEntities(3, 3, 3):
                if isinstance(e, Player):
                    continue
                if not isinstance(e, Mob):
                    continue

                self.activate(e)

                if self.remaining > 1:
                    self.cooldown = 10
                    self.remaining -= 1
                else:
                    Classes.getClasses().get(self.player).getSpells().get(5).cast(self.player)
                    self.deactivate()
                    self.cancel()

        self.cooldown -= 1

    def activate(self, e):
        e.damage(2, self.player)
        e.setNoDamageTicks(0)

        e.setVelocity(e.getLocation().toVector().subtract(self.player.getLocation().toVector()).setY(0).normalize().add(Vector(0, 1, 0)))

        self.player.playSound(self.player.getLocation(), Sound.ENTITY_ARROW_HIT, 1, .9)
        self.player.playSound(self.player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, .1, 1.9)
        self.player.playSound(self.player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1)

    def deactivate(self):
        self.player.removeScoreboardTag('arrow_shield')

        for stand in self.stands:
            stand.remove()