package mctbl.tinkersreborn.common.particle;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;

public class ParticleAttackLumberAxe extends ParticleAttack {

    public static final ResourceLocation TEXTURE = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/particle/slash_axe.png");

    public ParticleAttackLumberAxe(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
        double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    @Override
    protected void init() {
        super.init();
        this.size = 1.2f;
        this.lifeTime = 6;
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
