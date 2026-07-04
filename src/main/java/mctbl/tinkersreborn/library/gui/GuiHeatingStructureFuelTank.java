package mctbl.tinkersreborn.library.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;
import mctbl.tinkersreborn.library.utils.FuelInfo;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class GuiHeatingStructureFuelTank extends GuiMultiModule {

    protected FuelInfo fuelInfo;

    public GuiHeatingStructureFuelTank(ContainerMultiModule<?> container) {
        super(container);
    }

    /**
     * Draws the fuel at the specified location
     *
     * @param displayX Display X location, excluding cornerX
     * @param displayY Display Y location, excluding cornerY
     * @param width    Width
     * @param height   Height of the whole area, note that displayed size may differ due to how full the fuel is
     */
    protected void drawFuel(int displayX, int displayY, int width, int height) {
        if (fuelInfo.fluid != null && fuelInfo.fluid.amount > 0) {
            int x = displayX + cornerX;
            int y = displayY + cornerY + height;
            int w = width;
            int h = height * fuelInfo.fluid.amount / fuelInfo.maxCap;

            drawFluidIcon(x, y - h, w, h, this.zLevel, fuelInfo.fluid);
        }
    }

    public static void drawFluidIcon(int x, int y, int width, int height, float zLevel, Fluid fluid) {
        if (fluid == null) return;

        IIcon icon = fluid.getStillIcon();
        if (icon == null) return;

        int color = fluid.getColor();
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        double uMin = icon.getMinU();
        double uMax = icon.getMaxU();
        double vMin = icon.getMinV();
        double vMax = icon.getMaxV();

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, 1.0F);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, zLevel, uMin, vMax); // 左下
        tessellator.addVertexWithUV(x + width, y + height, zLevel, uMax, vMax); // 右下
        tessellator.addVertexWithUV(x + width, y, zLevel, uMax, vMin); // 右上
        tessellator.addVertexWithUV(x, y, zLevel, uMin, vMin); // 左上
        tessellator.draw();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawFluidIcon(int x, int y, int width, int height, float zLevel, FluidStack fluidStack) {
        if (fluidStack == null) return;
        drawFluidIcon(x, y, width, height, zLevel, fluidStack.getFluid());
    }

    /**
     * Draws the fuel tooltip
     */
    protected void drawFuelTooltip(int mouseX, int mouseY) {
        List<String> text = new ArrayList<>();
        FluidStack fuel = fuelInfo.fluid;
        text.add(EnumChatFormatting.WHITE + TinkersRebornUtils.translate("gui.smeltery.fuel"));
        if (fuel != null) {
            text.add(fuel.getLocalizedName());

            text.add(fuel.amount + "mB");

            text.add(
                String.format(
                    TinkersStr.smelteryFuelHeat.toString(),
                    TinkersRebornUtils.temperatureString(fuelInfo.heat)));

        } else {
            text.add(TinkersStr.smelteryFuelEmpty.toString());
        }
        this.drawHoveringText(text, mouseX, mouseY, this.fontRendererObj);
    }

}
