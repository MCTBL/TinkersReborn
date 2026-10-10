package mctbl.tinkersreborn.smeltery.inventory;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;
import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.smeltery.entity.FurnaceLogic;

public class ContainerFurnace extends ContainerMultiModule<FurnaceLogic> {

    protected ContainerSideInventory<FurnaceLogic> sideInventory;
    protected int oldFuel = 0;
    protected int oldFuelTotal = 0;
    protected int[] oldHeats;

    public ContainerFurnace(InventoryPlayer inventoryPlayer, FurnaceLogic tile) {
        super(tile);

        sideInventory = new ContainerFurnaceSideInventory(tile, 0, 0, calcColumns());
        addSubContainer(sideInventory, false);
        addPlayerInventory(inventoryPlayer, 8, 115);

        oldHeats = new int[tile.getSizeInventory()];
    }

    public int calcColumns() {
        return 4;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        if (isFurnaceSlot(slotIn)) {
            ItemStack result = FurnaceRecipes.smelting()
                .getSmeltingResult(stack);
            return result != null;
        }
        return super.canMergeSlot(stack, slotIn);
    }

    private boolean isFurnaceSlot(Slot slot) {
        return slot.inventory == this.inventory;
    }

    @Override
    protected boolean mergeItemStackMove(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
        if (stack.stackSize <= 0) return false;

        boolean flag = false;
        int k = useEndIndex ? endIndex - 1 : startIndex;

        while ((!useEndIndex && k < endIndex) || (useEndIndex && k >= startIndex)) {
            Slot slot = this.inventorySlots.get(k);
            ItemStack existing = slot.getStack();

            if (existing == null || existing.stackSize == 0) {
                if (this.isFurnaceSlot(slot)) {
                    int cap = ContainerFurnaceSideInventory.getMaxStackForItem(stack);
                    if (cap <= 0) return false;
                    int amount = Math.min(cap, stack.stackSize);
                    amount = Math.min(amount, slot.getSlotStackLimit());
                    if (amount <= 0) return false;

                    slot.putStack(stack.splitStack(amount));
                    slot.onSlotChanged();
                    flag = true;
                    if (stack.stackSize == 0) break;
                } else {
                    int limit = slot.getSlotStackLimit();
                    ItemStack stack2 = stack.copy();
                    if (stack2.stackSize > limit) {
                        stack2.stackSize = limit;
                        stack.stackSize -= limit;
                    } else {
                        stack.stackSize = 0;
                    }
                    if (slot.isItemValid(stack2) && this.canMergeSlot(stack2, slot)) {
                        slot.putStack(stack2);
                        slot.onSlotChanged();
                        flag = true;
                        if (stack.stackSize == 0) break;
                    }
                }
            }

            if (useEndIndex) --k;
            else++k;
        }

        return flag;
    }

    @Override
    public void addCraftingToCrafters(ICrafting listener) {
        super.addCraftingToCrafters(listener);
        listener.sendProgressBarUpdate(this, 0, tile.fuelReleaseTicks);
        listener.sendProgressBarUpdate(this, 1, tile.fuelTotalTicks);

        for (int i = 0; i < oldHeats.length; i++) {
            listener.sendProgressBarUpdate(this, i + 2, tile.getTemperature(i));
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        int fuel = tile.fuelReleaseTicks;
        if (fuel != oldFuel) {
            for (ICrafting crafter : this.crafters) {
                crafter.sendProgressBarUpdate(this, 0, fuel);
            }
            oldFuel = fuel;
        }

        int fuelTotal = tile.fuelTotalTicks;
        if (fuelTotal != oldFuelTotal) {
            for (ICrafting crafter : this.crafters) {
                crafter.sendProgressBarUpdate(this, 1, fuelTotal);
            }
            oldFuelTotal = fuelTotal;
        }

        for (int i = 0; i < oldHeats.length; i++) {
            int temp = tile.getTemperature(i);
            if (temp != oldHeats[i]) {
                oldHeats[i] = temp;
                for (ICrafting crafter : this.crafters) {
                    crafter.sendProgressBarUpdate(this, i + 2, temp);
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0 || id == 1) {
            tile.updateFuelFromPacket(id, data);
        } else {
            tile.updateTemperatureFromPacket(id - 2, data);
        }
    }
}
