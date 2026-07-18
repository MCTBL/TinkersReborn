package mctbl.tinkersreborn.common.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ParticleAttack extends EntityFX {

    protected int life;

    protected int lifeTime;
    protected float size;
    protected double height;

    protected int animPhases;
    protected int animPerRow;

    protected ParticleAttack(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
        double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);

        this.life = 0;
        init();
    }

    protected void init() {
        this.lifeTime = 4;
        this.size = 1f;
        this.height = 1f;

        this.animPerRow = 4;
        this.animPhases = 8;
    }

    protected abstract ResourceLocation getTexture();

    @Override
    public void renderParticle(Tessellator tess, float partialTicks, float rotationX, float rotationZ, float rotationYZ,
        float rotationXY, float rotationXZ) {
        float progress = (life + partialTicks) / lifeTime;
        int i = (int) (progress * animPhases);
        int rows = MathHelper.ceiling_float_int((float) animPhases / (float) animPerRow);

        if (i < animPhases) {
            Minecraft.getMinecraft().renderEngine.bindTexture(getTexture());
            float f = (float) (i % animPerRow) / (float) animPerRow;
            float f1 = f + 1f / animPerRow - 0.005f;
            float f2 = (float) (i / animPerRow) / (float) rows;
            float f3 = f2 + 1f / rows - 0.005f;
            float f4 = 0.5F * this.size;
            float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
            float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
            float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();

            tess.startDrawingQuads();
            tess.setBrightness(this.getBrightnessForRender(partialTicks));
            tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);

            tess.addVertexWithUV(
                f5 - rotationX * f4 - rotationXY * f4,
                (f6 - rotationZ * f4 * height),
                f7 - rotationYZ * f4 - rotationXZ * f4,
                f1,
                f3);

            tess.addVertexWithUV(
                f5 - rotationX * f4 + rotationXY * f4,
                (f6 + rotationZ * f4 * height),
                f7 - rotationYZ * f4 + rotationXZ * f4,
                f1,
                f2);
            tess.addVertexWithUV(
                f5 + rotationX * f4 + rotationXY * f4,
                (f6 + rotationZ * f4 * height),
                f7 + rotationYZ * f4 + rotationXZ * f4,
                f,
                f2);
            tess.addVertexWithUV(
                f5 + rotationX * f4 - rotationXY * f4,
                (f6 - rotationZ * f4 * height),
                f7 + rotationYZ * f4 - rotationXZ * f4,
                f,
                f3);

            tess.draw();

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.life;

        if (this.life == this.lifeTime) {
            this.setDead();
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
