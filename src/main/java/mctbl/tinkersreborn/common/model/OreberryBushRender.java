package mctbl.tinkersreborn.common.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.common.entity.OreberryTileEntity;
import mctbl.tinkersreborn.util.ItemHelper;

public class OreberryBushRender implements ISimpleBlockRenderingHandler {

    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (modelId == model) {
            renderer.setRenderBounds(0.125F, 0.0F, 0.125F, 0.875F, 0.75F, 0.875F);
            ItemHelper.renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        if (modelId == model && world.getTileEntity(x, y, z) instanceof OreberryTileEntity ob) {

            if (ob.state == 0) {
                renderer.setRenderBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                renderer.renderStandardBlock(block, x, y, z);
            } else if (ob.state == 1) {
                renderer.setRenderBounds(0.125F, 0.0F, 0.125F, 0.875F, 0.75F, 0.875F);
                renderer.renderStandardBlock(block, x, y, z);
            } else {
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelID) {
        return true;
    }

    @Override
    public int getRenderId() {
        return model;
    }

}
