package mctbl.tinkersreborn.library.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Crosshair implements ICrosshair {

    private final ResourceLocation texture;
    private final int size;

    public Crosshair(ResourceLocation texture) {
        this(texture, 16);
    }

    public Crosshair(ResourceLocation texture, int size) {
        this.texture = texture;
        this.size = size;
    }

    @Override
    public void render(float charge, float width, float height, float partialTicks) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(texture);
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        float spread = (1.0f - charge) * 25f;

        drawCrosshair(spread, width, height, partialTicks);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    protected void drawCrosshair(float spread, float width, float height, float partialTicks) {
        drawSquareCrosshairPart(width / 2f - spread, height / 2f - spread, 0);
        drawSquareCrosshairPart(width / 2f + spread, height / 2f - spread, 1);
        drawSquareCrosshairPart(width / 2f - spread, height / 2f + spread, 2);
        drawSquareCrosshairPart(width / 2f + spread, height / 2f + spread, 3);
    }

    protected void drawSquareCrosshairPart(double x, double y, int part) {
        int s = size / 4;

        double z = -90;

        double u1 = 0;
        double v1 = 0;

        switch (part) {
            // top left
            case 0:
                x -= s;
                y -= s;
                break;
            case 1:
                u1 = 0.5;
                x += s;
                y -= s;
                break;
            case 2:
                v1 = 0.5;
                x -= s;
                y += s;
                break;
            case 3:
                u1 = 0.5;
                v1 = 0.5;
                x += s;
                y += s;
                break;
        }

        double u2 = u1 + 0.5;
        double v2 = v1 + 0.5;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x - s, y - s, z, u1, v1);
        tessellator.addVertexWithUV(x - s, y + s, z, u1, v2);
        tessellator.addVertexWithUV(x + s, y + s, z, u2, v2);
        tessellator.addVertexWithUV(x + s, y - s, z, u2, v1);
        tessellator.draw();
    }
}
