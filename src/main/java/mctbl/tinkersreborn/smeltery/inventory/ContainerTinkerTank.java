package mctbl.tinkersreborn.smeltery.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;
import mctbl.tinkersreborn.library.inventory.slots.BucketSlot;
import mctbl.tinkersreborn.library.inventory.slots.SlotOnlyTake;
import mctbl.tinkersreborn.smeltery.entity.TinkerTankLogic;

public class ContainerTinkerTank extends ContainerMultiModule<TinkerTankLogic> {

    public ContainerTinkerTank(InventoryPlayer inventoryPlayer, TinkerTankLogic tile) {
        super(tile);

        addSlotToContainer(new BucketSlot(tile.buckets, 0, 94, 16));
        addSlotToContainer(new SlotOnlyTake(tile.buckets, 1, 94, 80));

        addPlayerInventory(inventoryPlayer, 8, 115);
    }

}
