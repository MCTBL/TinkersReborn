package mctbl.tinkersreborn.library.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TinkersRebornInnerInventory implements IInventory {

    protected ItemStack[] itemStacks;
    protected int stackLimit = 1;

    public TinkersRebornInnerInventory(int invSize) {
        this.itemStacks = new ItemStack[invSize];
    }

    @Override
    public int getSizeInventory() {
        return this.itemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.itemStacks[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack wait2Remove = this.getStackInSlot(index);
        int num2Remove = Math.min(wait2Remove.stackSize, count);
        ItemStack removeStack = wait2Remove.copy();
        removeStack.stackSize = num2Remove;
        wait2Remove.stackSize -= num2Remove;
        if (wait2Remove.stackSize <= 0) {
            this.setInventorySlotContents(index, null);
        }
        return removeStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.itemStacks[index] = stack;
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return this.stackLimit;
    }

    @Override
    public void markDirty() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void openInventory() {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeInventory() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        // TODO Auto-generated method stub
        return false;
    }

}
