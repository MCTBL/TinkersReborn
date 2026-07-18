package mctbl.tinkersreborn.common.particle;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;

public class ParticleAttackRapier extends ParticleAttack {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/particle/sweep.png");

    public ParticleAttackRapier(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
        double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    @Override
    protected void init() {
        super.init();

        this.animPhases = 8;
        this.height = 1f;
        this.size = 0.2f;
        this.lifeTime = 2;
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
