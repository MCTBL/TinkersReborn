package mctbl.tinkersreborn.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.inventory.TinkersRebornContainer;
import mctbl.tinkersreborn.library.inventory.slots.SlotOnlyTake;
import mctbl.tinkersreborn.tools.entity.PartBuilderLogic;

public class ContainerPartBuilder extends TinkersRebornContainer {

    public PartBuilderLogic logic;
    public Slot[] slots;

    public ContainerPartBuilder(InventoryPlayer inventoryplayer, PartBuilderLogic builderlogic) {
        super(inventoryplayer);
        this.invPlayer = inventoryplayer;
        this.logic = builderlogic;

        // TODO position
        this.slots = new Slot[] { new Slot(builderlogic, 0, 167, 29), new SlotOnlyTake(builderlogic, 1, 149, 38),
            new SlotOnlyTake(builderlogic, 2, 167, 47) };

        for (Slot s : this.slots) {
            this.addSlotToContainer(s);
        }

        this.bindPlayerInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return logic.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        return null;
    }
}
