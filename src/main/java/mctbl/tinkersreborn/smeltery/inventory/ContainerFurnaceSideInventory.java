package mctbl.tinkersreborn.smeltery.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.smeltery.entity.FurnaceLogic;

public class ContainerFurnaceSideInventory extends ContainerSideInventory<FurnaceLogic> {

    public ContainerFurnaceSideInventory(FurnaceLogic tile, int x, int y, int columns) {
        super(tile, x, y, columns);
    }

    @Override
    protected Slot createSlot(IInventory itemHandler, int index, int x, int y) {
        return new FurnaceSlot(itemHandler, index, x, y);
    }

    public static int getMaxStackForItem(ItemStack stack) {
        if (stack == null) return 0;
        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(stack);
        if (result == null || result.stackSize <= 0) return 0;
        return Math.min(16, 64 / result.stackSize);
    }

    protected static class FurnaceSlot extends Slot {

        public FurnaceSlot(IInventory itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack != null && FurnaceRecipes.smelting()
                .getSmeltingResult(stack) != null;
        }

        @Override
        public int getSlotStackLimit() {
            int cap = getMaxStackForItem(this.getStack());
            return cap > 0 ? cap : 16;
        }
    }
}
