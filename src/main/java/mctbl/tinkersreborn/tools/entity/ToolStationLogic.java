package mctbl.tinkersreborn.tools.entity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.gui.GuiToolStation;
import mctbl.tinkersreborn.tools.inventory.ContainerToolStation;

public class ToolStationLogic extends TinkersRebornInventoryLogic implements ISidedInventory {

    public ItemStack previousTool;
    public String toolName;

    public ToolStationLogic() {
        super(4);
        toolName = "";
    }

    public ToolStationLogic(int slots) {
        super(slots);
        toolName = "";
    }

    @Override
    public boolean canDropInventorySlot(int slot) {
        return slot != 0;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        return null;
    }

    @Override
    public String getDefaultName() {
        return "tinkersreborn.ToolStation";
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerToolStation(inventoryplayer, this);
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiToolStation(inventoryplayer, world, BlockPos.of(x, y, z), this);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        super.setInventorySlotContents(slot, stack);
        if (slot != 0) {
            // buildTool(toolName);
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack itemstack = super.decrStackSize(slot, amount);
        if (slot != 0) {}
        return itemstack;
    }

    @Override
    public void markDirty() {
        if (this.worldObj != null) {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    // public void buildTool(String name) {
    // }
    //
    // public void setToolname(String name) {
    // }

    // protected ItemStack tryRenameTool(ItemStack output, String name) {
    // return null;
    // }

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
        return "null";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    public static boolean canRename(NBTTagCompound tags, ItemStack tool) {
        // return tags != null && (!tags.hasKey("Name")
        // || tags.getString("Name").equals("\u00A7f" + ToolBuilder.defaultToolName(tool)));
        return false;
    }

}
