package mctbl.tinkersreborn.smeltery.entity;

import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemIOHatchLogic extends TinkersRebornInventoryLogic implements ISidedInventory,
    ITinkersRebornIFacingLogic {
    public ForgeDirection faceDirection;
    public int mode;
    public static final int INPUT_MODE = 0, OUTPUT_MODE = 1;

    public ItemIOHatchLogic(int mode) {
        super(0,0);
        if(mode == INPUT_MODE) {
            inventory = new ItemStack[6];
            stackSizeLimit = 64;
        }
        this.mode = mode;
    }

    @Override
    public ForgeDirection getForgeDirection() {
        return this.faceDirection;
    }

    public void setInventory(ItemStack[] inventory) {
        if (mode == INPUT_MODE) {
            this.inventory = inventory;
        }
    }

    @Override
    public void setForgeDirection(ForgeDirection direction) {
        this.faceDirection = direction;
    }

    @Override
    public void setFacedDirection(EntityLivingBase player) {
        int facing = player != null ? MathHelper.floor_double(player.rotationYaw / 90F + 0.5D) & 3 : 0;
        switch (facing) {
            case 0 -> this.faceDirection = ForgeDirection.NORTH;
            case 1 -> this.faceDirection = ForgeDirection.EAST;
            case 2 -> this.faceDirection = ForgeDirection.SOUTH;
            case 3 -> this.faceDirection = ForgeDirection.WEST;
            default -> this.faceDirection = ForgeDirection.NORTH;
        }
    }

    /**
     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
     * block.
     *
     * @param p_94128_1_
     */
    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        if (mode == INPUT_MODE) {
            return new int[]{0, 1, 2, 3, 4, 5};
        }else return null;
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
     * side
     *
     * @param p_102007_1_
     * @param p_102007_2_
     * @param p_102007_3_
     */
    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        return mode == INPUT_MODE;
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
     * side
     *
     * @param p_102008_1_
     * @param p_102008_2_
     * @param p_102008_3_
     */
    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
        ForgeDirection direction = ForgeDirection.getOrientation(p_102008_3_);
        return direction == getForgeDirection();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return mode == INPUT_MODE ? super.getSizeInventory() : 0;
    }

    /**
     * Returns the stack in slot i
     *
     * @param slotIn
     */
    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return mode == INPUT_MODE ? super.getStackInSlot(slotIn) : null;
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
        return mode == INPUT_MODE ? super.decrStackSize(index, count) : null;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     *
     * @param index
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return mode == INPUT_MODE ? super.getStackInSlotOnClosing(index) : null;
    }

    @Override
    protected String getDefaultName() {
        if (mode == INPUT_MODE) {
            return "InputHatch";
        }else return "OutputHatch";
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     *
     * @param index
     * @param stack
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (mode == INPUT_MODE) {
            super.setInventorySlotContents(index, stack);
        }
    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        if (mode == INPUT_MODE) {
            return "InputHatch";
        }else return "OutputHatch";
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
        return mode == INPUT_MODE ? super.getInventoryStackLimit() : 0;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     *
     * @param player
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (mode == INPUT_MODE) {
            return super.isUseableByPlayer(player);
        }
        return false;
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        if (mode == INPUT_MODE) {}
        return null;
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        if (mode == INPUT_MODE) {}
        return null;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     *
     * @param index
     * @param stack
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (mode == INPUT_MODE) {return super.isItemValidForSlot(index, stack);}
        return false;
    }
}
