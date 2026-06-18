package mctbl.tinkersreborn.tools.entity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.TinkersRebornChestLogic;
import mctbl.tinkersreborn.tools.gui.GuiPartChest;
import mctbl.tinkersreborn.tools.inventory.ContainerPartChest;

public class PartChestLogic extends TinkersRebornChestLogic {

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return false;
    }

    public boolean isItemValid(ItemStack itemstack) {
        return true;
    }

    @Override
    public String getDefaultName() {
        return "tinkersreborn.PartChest";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerPartChest(inventoryplayer, this);
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiPartChest(inventoryplayer, this, world, x, y, z);
    }

}
