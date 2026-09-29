package mctbl.tinkersreborn.smeltery.entity;

import java.util.stream.IntStream;

import mctbl.tinkersreborn.library.entity.TinkersRebornSearedMultiBlockLogic;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;

public class ItemIOHatchLogic extends TinkersRebornInventoryLogic
    implements ISidedInventory, ITinkersRebornIFacingLogic {

    public ForgeDirection faceDirection;
    public TinkersRebornSearedMultiBlockLogic logic;

    public ItemIOHatchLogic() {
        super(0, 0);
    }

    @Override
    public ForgeDirection getForgeDirection() {
        return this.faceDirection;
    }

    public void setFurnace(TinkersRebornSearedMultiBlockLogic furnace) {
        this.logic = furnace;
    }

    @Override
    public void setForgeDirection(ForgeDirection direction) {
        this.faceDirection = direction;
    }

    @Override
    public void setFacedDirection(EntityLivingBase player) {
        if (player.rotationPitch < -45.0F) {
            this.faceDirection = ForgeDirection.DOWN;
            return;
        }
        if (player.rotationPitch > 45.0F) {
            this.faceDirection = ForgeDirection.UP;
            return;
        }
        int facing = MathHelper.floor_double(player.rotationYaw / 90F + 0.5D) & 3;
        switch (facing) {
            case 0 -> this.faceDirection = ForgeDirection.NORTH;
            case 1 -> this.faceDirection = ForgeDirection.EAST;
            case 2 -> this.faceDirection = ForgeDirection.SOUTH;
            case 3 -> this.faceDirection = ForgeDirection.WEST;
            default -> this.faceDirection = ForgeDirection.NORTH;
        }
    }


    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (logic == null) return new int[0];
        if (ForgeDirection.getOrientation(side) != this.faceDirection) return new int[0];
        return IntStream.range(0, logic.getSizeInventory())
            .toArray();
    }


    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        if (ForgeDirection.getOrientation(side) != this.faceDirection) return false;
        if (logic == null || stack == null) return false;
        return logic.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        if (ForgeDirection.getOrientation(side) != this.faceDirection) return false;
        if (logic == null || stack == null) return false;
        return this.logic.getTemperature(side) <= 0;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return logic != null ? logic.getSizeInventory() : 0;
    }

    /**
     * Returns the stack in slot i
     *
     * @param slotIn
     */
    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return logic != null ? logic.getStackInSlot(slotIn) : null;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     *
     * @param index
     * @param count
     */
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return logic != null ? logic.decrStackSize(index, count) : null;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     *
     * @param index
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return logic != null ? logic.getStackInSlotOnClosing(index) : null;
    }

    @Override
    protected String getDefaultName() {
        return "IOHatch";
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     *
     * @param index
     * @param stack
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (logic != null) {
            logic.setInventorySlotContents(index, stack);
        }
    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        return getDefaultName();
    }

    /**
     * Returns if the inventory is named
     */
    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    @Override
    public int getInventoryStackLimit() {
        return logic != null ? logic.getInventoryStackLimit() : 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     *
     * @param player
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return super.isUseableByPlayer(player);
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean canDropInventorySlot(int slot) {
        return false;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        if (this.logic != null) {
            tags.setInteger("MasterX", this.logic.xCoord);
            tags.setInteger("MasterY", this.logic.yCoord);
            tags.setInteger("MasterZ", this.logic.zCoord);
        }
        byte index = (byte) this.faceDirection.ordinal();
        tags.setByte("Direction", index);
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        if (tags.hasKey("MasterX") && this.worldObj != null) {
            TileEntity te = this.worldObj
                .getTileEntity(tags.getInteger("MasterX"), tags.getInteger("MasterY"), tags.getInteger("MasterZ"));
            if (te instanceof FurnaceLogic f) {
                this.logic = f;
            }
        }
        if (tags.hasKey("Direction")) {
            this.faceDirection = ForgeDirection.getOrientation(tags.getInteger("Direction"));
        }
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     *
     * @param index
     * @param stack
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return logic != null && logic.isItemValidForSlot(index, stack);
    }
}
