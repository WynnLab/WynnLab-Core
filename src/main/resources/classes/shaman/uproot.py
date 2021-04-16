from org.bukkit import Bukkit, Material, Particle, Sound
from org.bukkit.entity import EntityType, Mob
from org.bukkit.inventory import ItemStack
from org.bukkit.util import Vector

from com.wynnlab.api import PersistentDataAPI
from com.wynnlab.spells import PySpell

from java.lang import Math

class Spell(PySpell):
    def __init__(self):
        self.catch_mobs = None
        self.shift = False

    def init(self):
        self.shift = self.player.isSneaking()

    def tick(self):
        if self.t == 0 and self.player.getScoreboardTags().contains('totem'):
            # Find totem
            totem = None
            totem_id = PersistentDataAPI.getInt(PersistentDataAPI.getData(self.player), 'totem')
            for e in self.player.getWorld().getEntities():
                if e.getEntityId() == totem_id:
                    totem = e

            if totem is None:
                self.t += 1
                return

            # Reset Totem
            totem_loc = totem.getLocation()
            totem.remove()

            holo_id = PersistentDataAPI.getInt(PersistentDataAPI.getData(self.player), 'totem_holo')
            if not holo_id is None:
                for e in self.player.getWorld().getEntities():
                    if e.getEntityId() == holo_id:
                        e.remove()

            task_id = PersistentDataAPI.getInt(PersistentDataAPI.getData(self.player), 'totem_task')
            if not task_id is None:
                Bukkit.getScheduler().cancelTask(task_id)

            totem = self.player.getWorld().spawnEntity(totem_loc, EntityType.ARMOR_STAND)
            totem.setGravity(True)
            totem.setInvulnerable(True)
            totem.setVisible(True)
            totem.addScoreboardTag('totem')

            totem_item = ItemStack(Material.STONE_SHOVEL)
            totem_meta = totem_item.getItemMeta()
            totem_meta.setUnbreakable(True)
            totem_meta.setDamage(29 if self.clone else 28)
            totem_item.setItemMeta(totem_meta)

            totem.getEquipment().setHelmet(totem_item)

            PersistentDataAPI.setInt(PersistentDataAPI.getData(self.player), 'totem', totem.getEntityId())

            holo = self.player.getWorld().spawnEntity(totem.getLocation().clone().add(0, 2.7, 0), EntityType.ARMOR_STAND)
            holo.setMarker(True)
            holo.setVisible(False)
            holo.setInvulnerable(True)
            holo.setCustomName(PySpell.colorText('20s', 'c'))
            holo.setCustomNameVisible(True)

            PersistentDataAPI.setInt(PersistentDataAPI.getData(self.player), 'totem_holo', holo.getEntityId())

            self.castSpell('SHAMAN', 5, totem, holo, True)

        v = self.player.getEyeLocation().getDirection()
        vr = Vector(1, 0, 0) if v.getY() == 0 else (v.clone().setY(0).normalize().rotateAroundY(Math.PI / 2))
        vi = v.clone().rotateAroundAxis(vr, Math.PI / 2)

        l = self.player.getEyeLocation().clone().add(v.clone().multiply(self.t))

        if self.t < 20:
            self.catch_mobs = self.catchable_mobs(l)
            if self.catch_mobs.size() > 0:
                self.catch(l, v, vi)
                self.cancel()

            l1 = l.clone().add(vi.clone().multiply(2).rotateAroundAxis(v, self.t / 4))
            l2 = l.clone().add(vi.clone().multiply(2).rotateAroundAxis(v, (self.t / 4) + Math.PI))

            self.particle(l1, Particle.FIREWORKS_SPARK if self.clone else Particle.TOTEM, 2, 0, 0, 0, .1)
            self.particle(l2, Particle.FIREWORKS_SPARK if self.clone else Particle.TOTEM, 2, 0, 0, 0, .1)

            self.sound(l, Sound.BLOCK_LAVA_EXTINGUISH, .1, .7)
        else:
            self.catch(l, v, vi)

    def catchable_mobs(self, l):
        return PySpell.nearbyMobsAndTag(self.player.getWorld(), l, 3, 3, 3, 'totem')

    def catch(self, l, v, vi):
        for i in range(0, 360, 30):
            p_l = l.clone().add(vi.clone().multiply(2).rotateAroundAxis(v, i * DEG2RAD))
            self.particle(p_l, Particle.FIREWORKS_SPARK if self.clone else Particle.TOTEM, 3, 0, 0, 0, .2)
            self.particle(p_l, Particle.CLOUD, 1, 0, 0, 0, .5)

        for e in self.catch_mobs if not self.catch_mobs is None else self.catchable_mobs(l):
            p_l = e.getLocation().clone().subtract(self.player.getEyeLocation()).toVector().normalize().multiply(3 if self.shift else -2)
            e.setVelocity(p_l.clone().multiply(2 if isinstance(e, Mob) else 1))

            if isinstance(e, Mob):
                self.damage(e, False, .5, .7, .3, 0, 0, 0, 0)

        self.sound(l, Sound.BLOCK_LAVA_EXTINGUISH, 1, .7)
        self.sound(l, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1.7)
        self.sound(l, Sound.ENTITY_ENDER_DRAGON_FLAP, .4, 1.7)