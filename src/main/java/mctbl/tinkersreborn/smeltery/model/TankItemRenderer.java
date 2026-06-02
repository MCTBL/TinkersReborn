package mctbl.tinkersreborn.smeltery.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.util.ItemHelper;

public class TankItemRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

        RenderBlocks renderblocks = (RenderBlocks) data[0];
        Block block = Block.getBlockFromItem(item.getItem());
        int meta = item.getItemDamage();

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            if (item.hasTagCompound() && item.getTagCompound()
                .hasKey("Fluid")) {
                FluidStack liquid = FluidStack.loadFluidStackFromNBT(
                    item.getTagCompound()
                        .getCompoundTag("Fluid"));
                if (liquid != null) {
                    Fluid fluid = liquid.getFluid();
                    Block fluidBlock = fluid.getBlock();
                    if (fluidBlock != null) {
                        int color = fluid.getColor();
                        float r = ((color >> 16) & 255) / 255.0F;
                        float g = ((color >> 8) & 255) / 255.0F;
                        float b = (color & 255) / 255.0F;

                        GL11.glColor4f(r, g, b, 1.0F);

                        float height = (float) liquid.amount / 4000F - 0.01F;
                        renderblocks.setRenderBounds(0.01, 0.01, 0.01, 0.99, height, 0.99);
                        ItemHelper.renderStandardInvBlock(renderblocks, fluidBlock, 0);
                    }
                }
            }

            GL11.glColor4f(1F, 1F, 1F, 1F);
            renderblocks.setRenderBounds(0, 0, 0, 1, 1, 1);
            RenderingRegistry.instance()
                .renderInventoryBlock(renderblocks, block, meta, TankRender.tankModelID);
        } finally {
            GL11.glPopAttrib();
        }
    }

}
