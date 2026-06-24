package mctbl.tinkersreborn.common.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;

@SideOnly(Side.CLIENT)
public class TinkersRebornParticle extends EntityFX {

    public static final ResourceLocation TEXTURE = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/particle/particles.png");
    public static final ResourceLocation VANILLA_PARTICLE_TEXTURES = new ResourceLocation(
        "textures/particle/particles.png");
    private final float u0, v0, u1, v1;
    protected final float oSize;

    protected final Type type;

    private int layer = 0;

    public TinkersRebornParticle(int typeId, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
        double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        if (typeId < 0 || typeId >= Type.values().length) {
            typeId = 0;
        }
        this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 0.5F;
        this.oSize = this.particleScale;

        this.type = Type.values()[typeId];

        this.particleMaxAge = 20;

        this.u0 = (float) type.x / 128f;
        this.v0 = (float) type.y / 128f;
        this.u1 = (float) (type.x + 8) / 128f;
        this.v1 = (float) (type.y + 8) / 128f;

        this.motionX = xSpeedIn + (this.rand.nextDouble() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionY = ySpeedIn + (this.rand.nextDouble() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionZ = zSpeedIn + (this.rand.nextDouble() * 2.0D - 1.0D) * 0.4000000059604645D;

        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.motionX += xSpeedIn * 0.4D;
        this.motionY += ySpeedIn * 0.4D;
        this.motionZ += zSpeedIn * 0.4D;

        this.motionX += -0.25f + rand.nextFloat() * 0.5f;
        this.motionY += 0.1f;
        this.motionZ += -0.25f + rand.nextFloat() * 0.5f;

        particleRed = particleBlue = particleGreen = particleAlpha = 1f;

        // has to be set after constructor because parent class accesses layer-0-only
        // functions
        this.layer = 3;

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

    @Override
    public void renderParticle(Tessellator tess, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY,
        float rotXZ) {
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

        float f = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge * 32.0F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);

//        TinkersReborn.LOG.info("renderParticle f={} particleAge={} particleAge={}", f, particleAge, partialTicks);
        this.particleScale = this.oSize * f;

        float scale = this.particleScale;

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        tess.startDrawingQuads();
        tess.setBrightness(this.getBrightnessForRender(partialTicks));
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

        tess.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
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
