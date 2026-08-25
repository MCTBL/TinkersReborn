package mctbl.tinkersreborn.tools.modifiers;

import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.EntityProjectileBase;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.traits.AbstractProjectileTrait;

public class ModFins extends AbstractProjectileTrait {

    public ModFins() {
        super("fins", 0xABCDEF);

        addAspects(ModifierAspect.projectileOnly);
    }

    @Override
    public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {
        if (projectile.isInWater()) {
            double speedup = 1f / slowdown;
            projectile.motionX *= speedup;
            projectile.motionY *= speedup;
            projectile.motionZ *= speedup;

            // apply regular slowdown, but a bit less :>
            double regularSlowdown = 1d - projectile.getSlowdown() * 0.8d;
            projectile.motionX *= regularSlowdown;
            projectile.motionY *= regularSlowdown;
            projectile.motionZ *= regularSlowdown;
        }
    }
}
