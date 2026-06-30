package mctbl.tinkersreborn.smeltery.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;
import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;

public class ContainerSmeltery extends ContainerMultiModule<SmelteryLogic> {

    protected ContainerSideInventory<SmelteryLogic> sideInventory;

    protected int oldFuel = 0;
    protected int[] oldHeats;

    public ContainerSmeltery(InventoryPlayer inventoryPlayer, SmelteryLogic tile) {
        super(tile);

        sideInventory = new ContainerSmelterySideInventory(tile, 0, 0, calcColumns());
        addSubContainer(sideInventory, true);

        addPlayerInventory(inventoryPlayer, 8, 84);

        oldHeats = new int[tile.getSizeInventory()];
    }

    public int calcColumns() {
        return 3;
    }

    @Override
    public void addCraftingToCrafters(ICrafting listener) {
        super.addCraftingToCrafters(listener);

        listener.sendProgressBarUpdate(this, 0, tile.getFuel());
        for (int i = 0; i < oldHeats.length; i++) {
            listener.sendProgressBarUpdate(this, i + 1, tile.getTemperature(i));
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        // update fuel only when switching between none and some
        int fuel = tile.getFuel();
        if (fuel > 0 != oldFuel > 0) {
            for (ICrafting crafter : this.crafters) {
                crafter.sendProgressBarUpdate(this, 0, fuel);
            }
            oldFuel = fuel;
        }

        // send changed heats
        for (int i = 0; i < oldHeats.length; i++) {
            int temp = tile.getTemperature(i);
            if (temp != oldHeats[i]) {
                oldHeats[i] = temp;
                for (ICrafting crafter : this.crafters) {
                    crafter.sendProgressBarUpdate(this, i + 1, temp);
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        // 0 is fuel
        if (id == 0) {
            tile.updateFuelFromPacket(0, data);
        } else {
            // id = index of the melting progress to update + 1, if 0 its the fuel boolean
            // data = temperature
            tile.updateTemperatureFromPacket(id - 1, data);
        }
    }
}
