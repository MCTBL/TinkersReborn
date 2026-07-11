package mctbl.tinkersreborn.library.entity;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public abstract class TinkersRebornInventoryLogic extends TileEntity implements IInventory, ITinkersRebornIFacingLogic {

    public Random rand = TinkersReborn.random;
    public ForgeDirection faceDirection;

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
        this.faceDirection = ForgeDirection.getOrientation(tags.getByte("Direction"));
        NBTTagList nbttaglist = tags.getTagList("Items", 10);
        int size = tags.hasKey("InvSize", 3) ? tags.getInteger("InvSize") : this.getSizeInventory();
        this.inventory = new ItemStack[size];

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
        tags.setByte("Direction", (byte) this.faceDirection.ordinal());
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setShort("Slot", (short) i);
                this.inventory[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        tags.setInteger("InvSize", this.inventory.length);
        tags.setTag("Items", nbttaglist);

        if (this.isInvNameLocalized()) {
            tags.setString("CustomName", this.invName);
        }

    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
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
        return TinkersRebornUtils.translate(this.getDefaultName() + ".name");
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

    @Override
    public void setFacedDirection(EntityLivingBase player) {
        int facing = player != null ? MathHelper.floor_double((double) (player.rotationYaw / 90F) + 0.5D) & 3 : 0;
        switch (facing) {
            case 0 -> this.faceDirection = ForgeDirection.NORTH;
            case 1 -> this.faceDirection = ForgeDirection.EAST;
            case 2 -> this.faceDirection = ForgeDirection.SOUTH;
            case 3 -> this.faceDirection = ForgeDirection.WEST;
            default -> this.faceDirection = ForgeDirection.NORTH;
        }
    }

    @Override
    public ForgeDirection getForgeDirection() {
        return this.faceDirection;
    }

    @Override
    public void setForgeDirection(ForgeDirection direction) {
        this.faceDirection = direction;
    }

    public BlockPos getBlockPos() {
        return BlockPos.of(this.xCoord, this.yCoord, this.zCoord);
    }

    // for render item in world
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
