package mctbl.tinkersreborn.tools.entity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.entity.TinkersRebornChestLogic;
import mctbl.tinkersreborn.tools.gui.GuiCastChest;
import mctbl.tinkersreborn.tools.inventory.ContainerCastChest;
import mctbl.tinkersreborn.tools.items.Pattern;

public class CastChestLogic extends TinkersRebornChestLogic {

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return this.isItemValid(itemstack);
    }

    public boolean isItemValid(ItemStack itemstack) {
        return itemstack.getItem() instanceof Pattern;
    }

    @Override
    public String getDefaultName() {
        return "tinkersreborn.CastChest";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerCastChest(inventoryplayer, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiCastChest(inventoryplayer, this, world, x, y, z);
    }
}
