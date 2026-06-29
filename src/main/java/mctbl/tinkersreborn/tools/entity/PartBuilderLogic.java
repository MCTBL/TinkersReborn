package mctbl.tinkersreborn.tools.entity;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.tools.gui.GuiPartBuilder;
import mctbl.tinkersreborn.tools.inventory.ContainerPartBuilder;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class PartBuilderLogic extends TinkersRebornInventoryLogic implements ISidedInventory {

    public PartBuilderLogic() {
        super(2);
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
    public void markDirty() {
        if (this.worldObj != null) {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
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
        return TinkersRebornUtils.translate(this.getDefaultName() + ".name");
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
    public List<DisplayItem> getDisplayItems() {
        // TODO Auto-generated method stub
        return super.getDisplayItems();
    }
}
