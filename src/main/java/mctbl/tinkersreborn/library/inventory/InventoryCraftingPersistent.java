package mctbl.tinkersreborn.library.inventory;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.isStackEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.util.TinkersRebornUtils;

// variant of InventoryCrafting that saves its itemstacks into the given inventory
public class InventoryCraftingPersistent extends InventoryCrafting {

    private final int length;
    private final Container eventHandler;
    private final IInventory parent;
    private boolean doNotCallUpdates;

    public InventoryCraftingPersistent(Container eventHandler, IInventory parent, int width, int height) {
        super(eventHandler, width, height);
        int k = width * height;

        assert (k == parent.getSizeInventory());

        this.parent = parent;
        this.length = k;
        this.eventHandler = eventHandler;
        this.doNotCallUpdates = false;
    }

    @Override
    public int getSizeInventory() {
        return this.length;
    }

    public boolean isEmpty() {
        int s = this.getSizeInventory();
        for (int idx = 0; idx < s; s++) {
            ItemStack stack = this.getStackInSlot(idx);
            if (stack != null && stack.stackSize != 0) return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= this.getSizeInventory() ? null : this.parent.getStackInSlot(index);
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Nonnull
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (!isStackEmpty(this.getStackInSlot(index))) {
            ItemStack itemstack;

            if (this.getStackInSlot(index).stackSize <= count) {
                itemstack = this.getStackInSlot(index);
                this.setInventorySlotContents(index, null);
                return itemstack;
            } else {
                itemstack = this.getStackInSlot(index)
                    .splitStack(count);

                if (this.getStackInSlot(index).stackSize == 0) {
                    this.setInventorySlotContents(index, null);
                }

                onCraftMatrixChanged();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        this.parent.setInventorySlotContents(index, stack);
        onCraftMatrixChanged();
    }

    @Override
    public void markDirty() {
        this.parent.markDirty();
    }

    /**
     * If set to true no eventhandler.onCraftMatrixChanged calls will be made. This
     * is used to prevent recipe check when changing the item slots when something
     * is crafted (since each slot with an item is reduced by 1, it changes ->
     * callback)
     */
    public void setDoNotCallUpdates(boolean doNotCallUpdates) {
        this.doNotCallUpdates = doNotCallUpdates;
    }

    public void onCraftMatrixChanged() {
        if (!doNotCallUpdates) {
            this.eventHandler.onCraftMatrixChanged(this);
        }
    }

    public List<ItemStack> getRemainingItems() {
        List<ItemStack> list = new ArrayList<>();

        int length = this.getSizeInventory();
        for (int idx = 0; idx < length; idx++) {
            ItemStack stack = this.getStackInSlot(length);
            if (!TinkersRebornUtils.isStackEmpty(stack)) {
                list.add(stack);
            }
        }

        return list;
    }

    public List<ItemStack> getStackList() {
        List<ItemStack> list = new ArrayList<>();

        int length = this.getSizeInventory();
        for (int idx = 0; idx < length; idx++) {
            list.add(this.getStackInSlot(length));
        }

        return list;
    }
}
