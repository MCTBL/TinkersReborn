package mctbl.tinkersreborn.tools.entity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.tools.gui.GuiPartBuilder;
import mctbl.tinkersreborn.tools.inventory.ContainerPartBuilder;

public class PartBuilderLogic extends TinkersRebornInventoryLogic implements ISidedInventory {

    public boolean isCrafted;

    public PartBuilderLogic() {
        super(3);
        this.isCrafted = false;
    }

    @Override
    public String getDefaultName() {
        return "tinkersreborn.PartBuilder";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerPartBuilder(inventoryplayer, this);
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiPartBuilder(inventoryplayer, this, world, x, y, z);
    }

    @Override
    public boolean canDropInventorySlot(int slot) {
        return slot <= 1;
    }

    // Called when emptying a slot, not when another item is placed in it
    @Override
    public ItemStack decrStackSize(int slotID, int quantity) {

        return null;
    }

    public void tryBuildPart(int slotID) {

    }

    // Called when a slot has something placed into it.
    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {

    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public String getInventoryName() {
        return getDefaultName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean canUpdate() {
        return false;
    }

}
