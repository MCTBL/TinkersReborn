package mctbl.tinkersreborn.smeltery;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluidBlock;

public class TinkersRebornSmelteryEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void bucketFill(FillBucketEvent event) {
        if (event.getResult() != Event.Result.DEFAULT || event.current == null
            || event.target == null
            || event.target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return;
        }

        boolean vanillaBucket = event.current.getItem() == Items.bucket;
        if (!vanillaBucket && !FluidContainerRegistry.isEmptyContainer(event.current)) {
            return;
        }

        int x = event.target.blockX;
        int y = event.target.blockY;
        int z = event.target.blockZ;

        if (event.entityPlayer != null
            && !event.entityPlayer.canPlayerEdit(x, y, z, event.target.sideHit, event.current)) {
            return;
        }

        Block block = event.world.getBlock(x, y, z);
        if (!(block instanceof TinkersRebornFluidBlock fluidBlock) || event.world.getBlockMetadata(x, y, z) != 0) {
            return;
        }

        TinkersRebornFluid fluid = (TinkersRebornFluid) fluidBlock.getFluid();
        ItemStack filledContainer;
        if (vanillaBucket) {
            filledContainer = TinkersRebornGeneral.tinkersBucket.getNewFluidBucketWithMaterial(fluid.identifier);
        } else {
            filledContainer = FluidContainerRegistry
                .fillFluidContainer(new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), event.current);
        }

        if (filledContainer == null) {
            event.setResult(Event.Result.DENY);
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            return;
        }

        event.result = filledContainer;
        event.setResult(Event.Result.ALLOW);
        event.world.setBlockToAir(x, y, z);
    }
}
