package mctbl.tinkersreborn.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.inventory.TinkersRebornContainer;
import mctbl.tinkersreborn.tools.entity.CastChestLogic;

public class ContainerCastChest extends TinkersRebornContainer {

    public CastChestLogic chest;

    public ContainerCastChest(InventoryPlayer inventoryplayer, CastChestLogic chest) {
        super(inventoryplayer);
        this.chest = chest;

        int startX = 8;
        int startY = 18;
        for (int column = 0; column < 4; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlotToContainer(
                    new SlotForCast(chest, row + column * 9, startX + row * 18, startY + column * 18));
            }
        }

        this.bindPlayerInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return chest.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        // TODO Auto-generated method stub
        return super.transferStackInSlot(player, slotID);
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int inventorySize, int slotSize, boolean inOrder) {
        // TODO Auto-generated method stub
        return super.mergeItemStack(stack, inventorySize, slotSize, inOrder);
    }

    class SlotForCast extends Slot {

        public SlotForCast(IInventory builder, int par3, int par4, int par5) {
            super(builder, par3, par4, par5);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            // TODO
            return true;
        }
    }
}
