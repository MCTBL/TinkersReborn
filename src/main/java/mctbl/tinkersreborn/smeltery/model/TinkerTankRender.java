package mctbl.tinkersreborn.smeltery.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.library.utils.BlockSkinRenderHelper;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.entity.TinkerTankLogic;
import mctbl.tinkersreborn.util.ItemHelper;

public class TinkerTankRender implements ISimpleBlockRenderingHandler {

    public static int tankModel = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (modelId == tankModel) {
            ItemHelper.renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelID,
        RenderBlocks renderer) {
        if (modelID == tankModel && world.getBlock(x, y, z) == TinkersRebornSmeltery.tankController)
            return renderTank(world, x, y, z, block, renderer);
        return false;
    }

    public boolean renderTank(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer) {
        boolean ret = renderer.renderStandardBlock(block, x, y, z);
        final TinkerTankLogic logic = (TinkerTankLogic) world.getTileEntity(x, y, z);
        if (logic != null && logic.getActive()) {
            BlockPos from = logic.minPos.add(-1, 0, -1);
            BlockPos to = logic.maxPos.add(1, 0, 1);

            // Liquids
            float base = 0F;
            int yBase = 0;
            int liquidBase = 0;
            int cap = logic.maxMoltenMetalAmount / logic.multiLayers;

            for (FluidStack liquid : logic.fluids) {
                int liquidSize = liquid.amount;
                while (liquidSize > 0) {
                    int room = cap - liquidBase;
                    int countSize = Math.min(liquidSize, room);
                    liquidSize -= countSize;

                    float height = countSize > cap ? 1.0F : (float) countSize / (float) cap;
                    float renderBase = base;
                    float renderHeight = height + base;
                    base += height;
                    liquidBase += countSize;

                    if (renderHeight < 0.01) {
                        renderHeight = 0.01f;
                    }

                    renderer.setRenderBounds(0, renderBase, 0, 1, renderHeight, 1);
                    Fluid fluid = liquid.getFluid();
                    final int renderY = from.y + yBase;
                    for (int xi = from.x; xi <= to.x; xi++) {
                        for (int zi = from.z; zi <= to.z; zi++) {
                            Block b = world.getBlock(xi, renderY, zi);
                            if (b == TinkersRebornSmeltery.searedGlass || b == TinkersRebornSmeltery.soulGlass
                                || b == Blocks.air) {
                                float minX = xi == from.x ? 0.01F : 0F;
                                float minZ = zi == from.z ? 0.01F : 0F;
                                float maxX = xi == to.x ? 0.99F : 1F;
                                float maxZ = zi == to.z ? 0.99F : 1F;
                                renderer.setRenderBounds(minX, renderBase, minZ, maxX, renderHeight, maxZ);
                                if (fluid.canBePlacedInWorld()) {
                                    BlockSkinRenderHelper
                                        .renderMetadataBlock(fluid.getBlock(), 0, xi, renderY, zi, renderer, world);
                                } else {
                                    BlockSkinRenderHelper.renderLiquidBlock(
                                        fluid.getStillIcon(),
                                        fluid.getFlowingIcon(),
                                        xi,
                                        renderY,
                                        zi,
                                        renderer,
                                        world,
                                        false,
                                        fluid.getColor(liquid));
                                }
                            }
                        }
                    }

                    if (countSize == room) {
                        base = 0F;
                        yBase++;
                        liquidBase = 0;
                    }
                    ret = true;
                }
            }
        }
        return ret;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return tankModel;
    }

}
