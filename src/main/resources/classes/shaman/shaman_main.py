from org.bukkit import Particle, Sound

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def __init__(self):
        self.p_left = None
        self.p_center = None
        self.p_right = None

        self.dir_left = None
        self.dir_center = None
        self.dir_right = None

        self.hit = set()

    def init(self):
        self.p_left = self.player.getEyeLocation().clone()
        self.p_center = self.p_left.clone()
        self.p_right = self.p_center.clone()

        self.dir_center = self.player.getEyeLocation().getDirection().clone()
        self.dir_left = self.dir_center.clone().rotateAroundY(-.3)
        self.dir_right = self.dir_center.clone().rotateAroundY(.3)

    def tick(self):
        self.p_left.add(self.dir_left)
        self.p_center.add(self.dir_center)
        self.p_right.add(self.dir_right)

        if self.t == 0:
            self.sound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .6)
            self.sound(Sound.ENTITY_ZOMBIE_INFECT, .5, 1.8)
            self.sound(Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1.4)
            if self.clone:
                self.sound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .5, .2)

        else:
            self.particles(self.p_left)
            self.particles(self.p_center)
            self.particles(self.p_right)

        for e in self.nearbyMobs(self.p_left, 1, 2, 1):
            if e in self.hit:
                continue
            self.hit.add(e)
            self.damage(e, True, .333)
        for e in self.nearbyMobs(self.p_center, 1, 2, 1):
            if e in self.hit:
                continue
            self.hit.add(e)
            self.damage(e, True, .333)
        for e in self.nearbyMobs(self.p_right, 1, 2, 1):
            if e in self.hit:
                continue
            self.hit.add(e)
            self.damage(e, True, .333)

    def particles(self, l):
        if self.clone:
            self.particle(l, Particle.SPELL_MOB, 0, 1, 1, 1, 1)
        else:
            self.particle(l, Particle.CRIT_MAGIC, 3, 0, 0, 0, 0)
        #self.particle(l, Particle.CRIT, 2, 0, 0, 0, 0)