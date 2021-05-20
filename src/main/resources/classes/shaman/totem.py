from org.bukkit import Bukkit, Material, Sound
from org.bukkit.entity import EntityType
from org.bukkit.inventory import ItemStack

from com.wynnlab.api import PersistentDataAPI
from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def tick(self):
        if self.player.getScoreboardTags().contains('totem'):
            totem_id = PersistentDataAPI.getInt(PersistentDataAPI.getData(self.player), 'totem', None)
            if not totem_id is None:
                for e in self.player.getWorld().getEntities():
                    if e.getEntityId() == totem_id:
                        e.remove()

            holo_id = PersistentDataAPI.getInt(PersistentDataAPI.getData(self.player), 'totem_holo', None)
            if not holo_id is None:
                for e in self.player.getWorld().getEntities():
                    if e.getEntityId() == holo_id:
                        e.remove()

            totem_task = PersistentDataAPI.getInt(PersistentDataAPI.getData(self.player), 'totem_task', None)
            if not totem_task is None:
                Bukkit.getScheduler().cancelTask(totem_task)

        self.player.addScoreboardTag('totem')

        self.sound(Sound.ENTITY_PLAYER_ATTACK_WEAK, .8, .3)
        self.sound(Sound.ENTITY_IRON_GOLEM_HURT, 1, .8)
        if self.clone:
            self.sound(Sound.ENTITY_BLAZE_SHOOT, 1, .9)
            self.sound(Sound.ENTITY_BLAZE_AMBIENT, .1, .7)

        dir = self.player.getEyeLocation().getDirection().clone().setY(self.player.getEyeLocation().getDirection().getY())

        totem = self.player.getWorld().spawnEntity(self.player.getLocation().clone().add(0, 1, 0).add(dir), EntityType.ARMOR_STAND)

        totem.addScoreboardTag('totem')
        totem.setGravity(True)
        totem.setInvulnerable(True)
        totem.setVisible(True)

        totem_item = ItemStack(Material.STONE_SHOVEL)
        totem_meta = totem_item.getItemMeta()
        totem_meta.setUnbreakable(True)
        totem_meta.setDamage(29 if self.clone else 28)
        totem_item.setItemMeta(totem_meta)

        totem.getEquipment().setHelmet(totem_item)

        totem.setVelocity(dir.setY(1))

        PersistentDataAPI.setInt(PersistentDataAPI.getData(self.player), 'totem', totem.getEntityId())

        self.castSpell('SHAMAN', 5, totem, None, False)