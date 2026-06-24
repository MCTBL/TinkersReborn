package mctbl.tinkersreborn.common.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;

@SideOnly(Side.CLIENT)
public class TinkersRebornParticle extends EntityFX {
    // TODO render incorrect

    public static final ResourceLocation TEXTURE = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/particle/particles.png");
    public static final ResourceLocation VANILLA_PARTICLE_TEXTURES = new ResourceLocation(
        "textures/particle/particles.png");
    private final float u0, v0, u1, v1;

    protected TextureManager textureManager;
    protected final Type type;

    private int layer = 0;

    public TinkersRebornParticle(int typeId, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
        double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        if (typeId < 0 || typeId > Type.values().length) {
            typeId = 0;
        }

        this.type = Type.values()[typeId];

        this.particleMaxAge = 20;
        this.particleTextureIndexX = type.x / 8;
        this.particleTextureIndexY = type.y / 8;

        this.u0 = (float) type.x / 128f;
        this.v0 = (float) (128 - type.y - 8) / 128f;
        this.u1 = (float) (type.x + 8) / 128f;
        this.v1 = (float) (128 - type.y) / 128f;

        this.motionY += 0.1f;
        this.motionX += -0.25f + rand.nextFloat() * 0.5f;
        this.motionZ += -0.25f + rand.nextFloat() * 0.5f;

        this.textureManager = Minecraft.getMinecraft()
            .getTextureManager();

        particleRed = particleBlue = particleGreen = particleAlpha = 1f;

        // has to be set after constructor because parent class accesses layer-0-only
        // functions
        this.layer = 3;

        this.particleScale = 0.2F;
        this.particleMaxAge = 20;
        this.noClip = false;
    }

    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    public void onUpdate() {
        float r = this.particleRed;
        float g = this.particleGreen;
        float b = this.particleBlue;
        super.onUpdate();

        this.particleRed = r * 0.975f;
        this.particleGreen = g * 0.975f;
        this.particleBlue = b * 0.975f;
    }

    // @Override
    // public void renderParticle(Tessellator tessellator, float partialTicks, float rotX, float rotZ, float rotYZ,
    // float rotXY, float rotXZ) {
    //
    // TinkersReborn.LOG.info("Render TinkersRebornParticle");
    // TinkersReborn.LOG.info("x={}, y={}, z={}", this.lastTickPosX, this.lastTickPosY, this.lastTickPosZ);
    //
    // Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
    // super.renderParticle(tessellator, partialTicks, rotX, rotZ, rotYZ, rotXY, rotXZ);
    // // this.textureManager.bindTexture(VANILLA_PARTICLE_TEXTURES);
    // }

    @Override
    public void renderParticle(Tessellator tess, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY,
        float rotXZ) {
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

        TinkersReborn.LOG.info(
            "Render TinkersRebornParticle from ({}, {}), to ({}, {}) at {} {} {}",
            this.u0,
            this.v0,
            this.u1,
            this.v1,
            this.prevPosX,
            this.prevPosY,
            this.prevPosZ);

        float scale = 0.1F * this.particleScale;

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);

        tess.addVertexWithUV(
            x - rotX * scale - rotXY * scale,
            y - rotZ * scale,
            z - rotYZ * scale - rotXZ * scale,
            u1,
            v1);

        tess.addVertexWithUV(
            x - rotX * scale + rotXY * scale,
            y + rotZ * scale,
            z - rotYZ * scale + rotXZ * scale,
            u1,
            v0);

        tess.addVertexWithUV(
            x + rotX * scale + rotXY * scale,
            y + rotZ * scale,
            z + rotYZ * scale + rotXZ * scale,
            u0,
            v0);

        tess.addVertexWithUV(
            x + rotX * scale - rotXY * scale,
            y - rotZ * scale,
            z + rotYZ * scale - rotXZ * scale,
            u0,
            v1);
    }

    @Override
    public int getFXLayer() {
        // layer 3 seems to be a "binds its own texture" layer
        return layer;
    }

    public enum Type {

        HEART_FIRE(0, 0),
        HEART_CACTUS(8, 0),
        HEART_ELECTRO(16, 0),
        HEART_BLOOD(24, 0),
        HEART_ARMOR(32, 0);

        int x, y;

        Type(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
