from org.bukkit import Particle, Sound

from com.wynnlab.spells import PySpell

class Spell(PySpell):
    def __init__(self):
        self.l = None

    def tick(self):
        # TODO: puffer
        #self.particle(l, Particle.CLOUD,8,2,2,2,0.2);
        #l.getWorld().spawnParticle(clone?Particle.SPELL_WITCH:Particle.SQUID_INK,l,6,2,2,2,clone?0.5:0.2);
        #if(tick%5==0) {
        #l.getWorld().playSound(l,Sound.BLOCK_FIRE_EXTINGUISH,0.2f,1);
        #for (Entity e : l.getWorld().getNearbyEntities(l, 3, 3, 3)) {
        #if (e instanceof Mob && !(e instanceof Player)) {
        #    ((LivingEntity) e).damage(2,player);
        #((LivingEntity) e).setNoDamageTicks(0);
        #((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
        #}
        #}
        #}
        #tick++;
        pass