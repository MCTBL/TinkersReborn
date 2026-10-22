package mctbl.tinkersreborn.common.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.util.ColorUtil;

public class SlimeVineRender implements ISimpleBlockRenderingHandler {

    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        // nothing to do
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        IIcon icon = block.getIcon(world, x, y, z, 0);
        int colorMultiplier = block.colorMultiplier(world, x, y, z);

        Tessellator.instance.setColorOpaque(
            ColorUtil.red(colorMultiplier),
            ColorUtil.green(colorMultiplier),
            ColorUtil.blue(colorMultiplier));
        if ((meta & 4) != 0) {
            renderer.setRenderBounds(0, 0, 0, 1, 1, 0.0625);
            renderer.renderFaceZPos(block, x, y, z, icon);
            renderer.renderFaceZNeg(block, x, y, z, icon);
        }

        if ((meta & 1) != 0) {
            renderer.setRenderBounds(0, 0, 0.9375, 1, 1, 1);
            renderer.renderFaceZPos(block, x, y, z, icon);
            renderer.renderFaceZNeg(block, x, y, z, icon);
        }

        if ((meta & 2) != 0) {
            renderer.setRenderBounds(0, 0, 0, 0.0625, 1, 1);
            renderer.renderFaceXPos(block, x, y, z, icon);
            renderer.renderFaceXNeg(block, x, y, z, icon);
        }

        if ((meta & 8) != 0) {
            renderer.setRenderBounds(0.9375, 0, 0, 1, 1, 1);
            renderer.renderFaceXPos(block, x, y, z, icon);
            renderer.renderFaceXNeg(block, x, y, z, icon);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return model;
    }

}
