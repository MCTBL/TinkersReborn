package mctbl.tinkersreborn.library.entity;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public abstract class TinkersRebornInventoryLogic extends TileEntity implements IInventory {

    protected ItemStack[] inventory;
    protected String invName;
    protected int stackSizeLimit;

    public TinkersRebornInventoryLogic(int invSize) {
        this(invSize, 64);
    }

    public TinkersRebornInventoryLogic(int invSize, int maxStackSize) {
        inventory = new ItemStack[invSize];
        stackSizeLimit = maxStackSize;
    }

    /* Inventory management */
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot < inventory.length ? inventory[slot] : null;
    }

    public boolean isStackInSlot(int slot) {
        return slot < inventory.length && inventory[slot] != null;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    public ItemStack[] getInventory() {
        return this.inventory;
    }

    @Override
    public int getInventoryStackLimit() {
        return stackSizeLimit;
    }

    public boolean canDropInventorySlot(int slot) {
        return true;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }

        if (worldObj != null && worldObj.isRemote) {
            worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int quantity) {
        if (inventory[slot] != null) {
            if (inventory[slot].stackSize <= quantity) {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                return stack;
            }
            ItemStack split = inventory[slot].splitStack(quantity);
            if (inventory[slot].stackSize == 0) {
                inventory[slot] = null;
            }
            return split;
        } else {
            return null;
        }
    }

    /* Supporting methods */
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) return false;

        else return entityplayer.getDistance((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D)
            <= 64D;

    }

    public abstract Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z);

    @SideOnly(Side.CLIENT)
    public abstract GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z);

    /* NBT */
    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        readInventoryFromNBT(tags);
    }

    public void readInventoryFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        NBTTagList nbttaglist = tags.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];

        if (tags.hasKey("CustomName", 8)) {
            this.invName = tags.getString("CustomName");
        }

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);

            int j = nbttagcompound1.getShort("Slot");

            if (j >= 0 && j < this.inventory.length) {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        writeInventoryToNBT(tags);
    }

    public void writeInventoryToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setShort("Slot", (short) i);
                this.inventory[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        tags.setTag("Items", nbttaglist);

        if (this.isInvNameLocalized()) {
            tags.setString("CustomName", this.invName);
        }

    }

    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    public void openChest() {}

    public void closeChest() {}

    protected abstract String getDefaultName();

    public void setInvName(String name) {
        this.invName = name;
    }

    public String getInvName() {
        return this.isInvNameLocalized() ? this.invName : getDefaultName();
    }

    public boolean hasCustomInventoryName() {
        return isInvNameLocalized();
    }

    public boolean isInvNameLocalized() {
        return this.invName != null && this.invName.length() > 0;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        if (slot < getSizeInventory()) {
            if (inventory[slot] == null || itemstack.stackSize + inventory[slot].stackSize <= getInventoryStackLimit())
                return true;
        }
        return false;
    }

    @Override
    public String getInventoryName() {
        return getDefaultName();
    }

    public void placeBlock(EntityLivingBase entity, ItemStack stack) {}

    public void removeBlock() {}

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    public boolean isInventoryEmpty() {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (!TinkersRebornUtils.isStackEmpty(getStackInSlot(i))) {
                return false;
            }
        }

        return true;
    }

    public List<DisplayItem> getDisplayItems() {
        return Collections.emptyList();
    }

    public static class DisplayItem {

        public final ItemStack stack;
        public final float x, y, z;
        public final float scale;
        public final float rotation;

        public DisplayItem(ItemStack stack, float x, float y, float z, float scale, float rotation) {
            this.stack = stack;
            this.x = x;
            this.y = y;
            this.z = z;
            this.scale = scale;
            this.rotation = rotation;
        }
    }
}
