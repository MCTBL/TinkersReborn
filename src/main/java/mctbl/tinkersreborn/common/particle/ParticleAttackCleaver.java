package mctbl.tinkersreborn.common.particle;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;

public class ParticleAttackCleaver extends ParticleAttack {

    public static final ResourceLocation TEXTURE = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/particle/slash_cleaver.png");

    public ParticleAttackCleaver(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
        double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    @Override
    protected void init() {
        super.init();
        this.height = 1.3f;
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
