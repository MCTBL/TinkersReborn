package mctbl.tinkersreborn.library.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.eventhandler.Event;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;

public class TinkerSmelteryEvent extends Event {

    public final BlockPos pos;
    public final SmelteryLogic smeltery;

    public TinkerSmelteryEvent(BlockPos pos, SmelteryLogic smeltery) {
        this.pos = pos;
        this.smeltery = smeltery;
    }

    /** Fired when an item finishes melting down in the smeltery */
    public static class OnMelting extends TinkerSmelteryEvent {

        public FluidStack result;
        /** Itemstack is not in the smeltery anymore */
        public final ItemStack itemStack;

        public OnMelting(BlockPos pos, SmelteryLogic smeltery, ItemStack itemStack, FluidStack result) {
            super(pos, smeltery);
            this.itemStack = itemStack;
            this.result = result;
        }

        public static OnMelting fireEvent(SmelteryLogic smeltery, ItemStack stack, FluidStack result) {
            OnMelting event = new OnMelting(smeltery.getBlockPos(), smeltery, stack, result);
            MinecraftForge.EVENT_BUS.post(event);
            return event;
        }
    }
}
