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

    private static class FurnaceSlot extends Slot {

        public FurnaceSlot(IInventory itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (stack == null) return false;
            ItemStack result = FurnaceRecipes.smelting()
                .getSmeltingResult(stack);
            return result != null && result.stackSize <= 64;
        }

        @Override
        public int getSlotStackLimit() {
            return 16;
        }
    }
}
