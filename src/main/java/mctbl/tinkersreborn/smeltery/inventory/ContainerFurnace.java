package mctbl.tinkersreborn.smeltery.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

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

        sideInventory = new ContainerFurnaceSideInventory(tile, 0, 0, 4);
        addSubContainer(sideInventory, false);
        addPlayerInventory(inventoryPlayer, 8, 115);

        oldHeats = new int[tile.getSizeInventory()];
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
