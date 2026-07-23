package mctbl.tinkersreborn.tools.traits;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.EntityProjectileBase;
import mctbl.tinkersreborn.library.tools.traits.AbstractProjectileTrait;

public class TraitHovering extends AbstractProjectileTrait {

    public TraitHovering() {
        super("hovering", 0xFFC100);
    }

    @Override
    public void onLaunch(EntityProjectileBase projectileBase, World world, @Nullable EntityLivingBase shooter) {
        projectileBase.motionX /= 2d;
        projectileBase.motionY /= 2d;
        projectileBase.motionZ /= 2d;
    }

    @Override
    public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {
        double slowdownCompensation = 0.99f / slowdown;
        projectile.motionX *= slowdownCompensation;
        projectile.motionY *= slowdownCompensation;
        projectile.motionZ *= slowdownCompensation;

        projectile.motionY += projectile.getGravity() * 95d / 100d;

        if (world.isRemote && random.nextInt(2) == 0) {
            float vx = (random.nextFloat() - 0.5f) / 15f;
            float vy = (random.nextFloat()) / 15f;
            float vz = (random.nextFloat() - 0.5f) / 15f;
            world.spawnParticle("flame", projectile.posX, projectile.posY, projectile.posZ, vx, vy, vz);
        }
    }
}
