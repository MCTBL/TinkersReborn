package mctbl.tinkersreborn.smeltery.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;

public class ContainerSmelterySideInventory extends ContainerSideInventory<SmelteryLogic> {

    public ContainerSmelterySideInventory(SmelteryLogic tile, int x, int y, int columns) {
        super(tile, x, y, columns);
    }

    @Override
    protected Slot createSlot(IInventory itemHandler, int index, int x, int y) {
        return new SmelterySlot(itemHandler, index, x, y);
    }

    private static class SmelterySlot extends Slot {

        public SmelterySlot(IInventory itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return true;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }
}
