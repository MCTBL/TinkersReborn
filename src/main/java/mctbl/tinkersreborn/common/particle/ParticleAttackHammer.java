package mctbl.tinkersreborn.common.particle;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;

@SideOnly(Side.CLIENT)
public class ParticleAttackHammer extends ParticleAttack {

    public static final ResourceLocation TEXTURE = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/particle/slash_hammer.png");

    public ParticleAttackHammer(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
        double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    @Override
    protected void init() {
        super.init();
        this.size = 1.2f;
        this.lifeTime = 4;
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
