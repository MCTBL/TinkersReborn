package mctbl.tinkersreborn.tools.entity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.tools.gui.GuiCraftingStation;
import mctbl.tinkersreborn.tools.inventory.ContainerCraftingStation;

public class CraftingStationLogic extends TinkersRebornInventoryLogic {

    public CraftingStationLogic() {
        super(10);
    }

    @Override
    public boolean canDropInventorySlot(int slot) {
        return slot != 0;
    }

    @Override
    public ItemStack decrStackSize(int slot, int quantity) {
        if (slot == 0) {
            for (int i = 1; i < getSizeInventory(); i++) decrStackSize(i, 1);
        }
        return super.decrStackSize(slot, quantity);
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerCraftingStation(inventoryplayer, this);
    }

    @Override
    public String getDefaultName() {
        return "tinkersreborn.CraftingStation";
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiCraftingStation(inventoryplayer, this, world, x, y, z);
    }
}
