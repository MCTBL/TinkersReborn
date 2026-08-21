package mctbl.tinkersreborn.smeltery.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;
import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.library.inventory.slots.SlotOnlyTake;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;

public class ContainerSmeltery extends ContainerMultiModule<SmelteryLogic> {

    protected ContainerSideInventory<SmelteryLogic> sideInventory;

    protected int oldFuel = 0;
    protected int oldFuelTotla = 0;
    protected int[] oldHeats;

    protected Slot bucketSlotIn;
    protected Slot bucketSlotOut;

    public ContainerSmeltery(InventoryPlayer inventoryPlayer, SmelteryLogic tile) {
        super(tile);

        addSlotToContainer(new BucketSlot(tile.buckets, 0, 94, 16));
        addSlotToContainer(new SlotOnlyTake(tile.buckets, 1, 94, 80));

        sideInventory = new ContainerSmelterySideInventory(tile, 0, 0, calcColumns());
        addSubContainer(sideInventory, true);

        addPlayerInventory(inventoryPlayer, 8, 115);

        oldHeats = new int[tile.getSizeInventory()];
    }

    public int calcColumns() {
        return 3;
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

        // update fuel only when switching between none and some
        int fuel = tile.fuelReleaseTicks;
        if (fuel != oldFuel) {
            for (ICrafting crafter : this.crafters) {
                crafter.sendProgressBarUpdate(this, 0, fuel);
            }
            oldFuel = fuel;
        }

        int fuelTotla = tile.fuelTotalTicks;
        if (fuelTotla != oldFuelTotla) {
            for (ICrafting crafter : this.crafters) {
                crafter.sendProgressBarUpdate(this, 1, fuelTotla);
            }
            oldFuelTotla = fuelTotla;
        }

        // send changed heats
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
        // 0 is fuel
        if (id == 0 || id == 1) {
            tile.updateFuelFromPacket(id, data);
        } else {
            // id = index of the melting progress to update + 1, if 0 its the fuel boolean
            // data = temperature
            tile.updateTemperatureFromPacket(id - 2, data);
        }
    }

    public static class BucketSlot extends Slot {

        public BucketSlot(IInventory inv, int slotIndex, int slotX, int slotY) {
            super(inv, slotIndex, slotX, slotY);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() instanceof ItemBucket;
        }
    }
}
