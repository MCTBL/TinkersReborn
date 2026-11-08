package mctbl.tinkersreborn.common.particle;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleAttackLongsword extends ParticleAttack {

    private static final ResourceLocation SWEEP_TEXTURE = new ResourceLocation(
        "tinkersreborn",
        "textures/particle/sweep.png");

    public ParticleAttackLongsword(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
        double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    @Override
    protected void init() {
        super.init();
        this.height = 0.5f;
        this.size = 1.8f;
    }

    @Override
    protected ResourceLocation getTexture() {
        return SWEEP_TEXTURE;
    }
}
