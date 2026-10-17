package mctbl.tinkersreborn.library.inventory.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

public class BucketSlot extends Slot {

    public BucketSlot(IInventory inv, int slotIndex, int slotX, int slotY) {
        super(inv, slotIndex, slotX, slotY);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemBucket;
    }

}
