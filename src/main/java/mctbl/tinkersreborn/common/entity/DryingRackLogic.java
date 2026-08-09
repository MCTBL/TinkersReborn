package mctbl.tinkersreborn.common.entity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;

public class DryingRackLogic extends TinkersRebornInventoryLogic implements ISidedInventory {

    private int currentTime;
    private int maxTime;

    public DryingRackLogic() {
        super(2, 1);
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
    protected String getDefaultName() {
        return "";
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote && maxTime > 0 && currentTime < maxTime) {
            currentTime++;
            if (currentTime >= maxTime) {
                inventory[1] = TinkersRebornRegistry.getDryingResult(inventory[0]);
                inventory[0] = null;
                updateDryingTime();
            }
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        super.setInventorySlotContents(slot, itemstack);
        updateDryingTime();
    }

    @Override
    public ItemStack decrStackSize(int slot, int quantity) {
        ItemStack stack = super.decrStackSize(slot, quantity);
        maxTime = 0;
        currentTime = 0;
        return stack;
    }

    public void updateDryingTime() {
        currentTime = 0;
        if (inventory[0] != null) maxTime = TinkersRebornRegistry.getDryingTime(inventory[0]);
        else maxTime = 0;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        currentTime = tags.getInteger("Time");
        maxTime = tags.getInteger("MaxTime");
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        tags.setInteger("Time", currentTime);
        tags.setInteger("MaxTime", maxTime);
        writeCustomNBT(tags);
    }

    public void readCustomNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
    }

    public void writeCustomNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord - 1, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int meta) {
        return new int[] { 0, 1, 2, 3, 4, 5 };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
        // nothing in slot 0 and 1
        return !this.isStackInSlot(1) && !this.isStackInSlot(0);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        return this.isStackInSlot(1);
    }

    public int getMaxTime() {
        return this.maxTime;
    }

    public int getCurrentTime() {
        return this.currentTime;
    }
}
