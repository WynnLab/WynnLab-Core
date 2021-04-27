def tick(self):
    self.target.setVelocity(self.caster.getLocation().clone().subtract(self.target.getLocation()).toVector())
    return True