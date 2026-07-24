package mctbl.tinkersreborn.library.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CrosshairT extends Crosshair {

    public CrosshairT(ResourceLocation texture) {
        super(texture);
    }

    public CrosshairT(ResourceLocation texture, int size) {
        super(texture, size);
    }

    @Override
    protected void drawCrosshair(float spread, float width, float height, float partialTicks) {
        // drawTipCrosshairPart(width / 2f, height / 2f - spread, 0);
        drawTipCrosshairPart(width / 2f - spread, height / 2f, 1);
        drawTipCrosshairPart(width / 2f + spread, height / 2f, 2);
        drawTipCrosshairPart(width / 2f, height / 2f + spread, 3);
    }

    private void drawTipCrosshairPart(double x, double y, int part) {
        final double s = 8d;
        final double z = -90;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        if (part == 0) {
            // up
            tessellator.addVertexWithUV(x - s, y - s, z, 0, 0);
            tessellator.addVertexWithUV(x, y, z, 0.46875, 0.46875);
            tessellator.addVertexWithUV(x + s, y - s, z, 0.9375, 0);
        } else if (part == 1) {
            // left
            tessellator.addVertexWithUV(x - s, y - s, z, 0, 0);
            tessellator.addVertexWithUV(x - s, y + s, z, 0, 0.9375);
            tessellator.addVertexWithUV(x, y, z, 0.46875, 0.46875);
        } else if (part == 2) {
            // right
            tessellator.addVertexWithUV(x, y, z, 0.46875, 0.46875);
            tessellator.addVertexWithUV(x + s, y + s, z, 0.9375, 0.9375);
            tessellator.addVertexWithUV(x + s, y - s, z, 0.9375, 0);
        } else if (part == 3) {
            // down
            tessellator.addVertexWithUV(x + s, y + s, z, 0.9375, 0.9375);
            tessellator.addVertexWithUV(x, y, z, 0.46875, 0.46875);
            tessellator.addVertexWithUV(x - s, y + s, z, 0, 0.9375);
        }

        tessellator.draw();
    }
}
