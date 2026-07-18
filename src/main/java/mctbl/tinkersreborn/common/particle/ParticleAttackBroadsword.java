package mctbl.tinkersreborn.common.particle;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;

public class ParticleAttackBroadsword extends ParticleAttack {

    private static final ResourceLocation SWEEP_TEXTURE = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/particle/sweep.png");

    public ParticleAttackBroadsword(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
        double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    @Override
    protected void init() {
        super.init();
        this.height = 0.5f;
        this.size = 2.5f;
    }

    @Override
    protected ResourceLocation getTexture() {
        return SWEEP_TEXTURE;
    }
}
