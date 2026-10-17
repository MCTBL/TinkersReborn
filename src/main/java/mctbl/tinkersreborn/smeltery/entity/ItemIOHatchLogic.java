package mctbl.tinkersreborn.smeltery.entity;

import java.util.stream.IntStream;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.entity.TinkersRebornMultiBlockInvenotryLogic;

public class ItemIOHatchLogic extends MultiServantLogic implements ISidedInventory, ITinkersRebornIFacingLogic {

    public ForgeDirection faceDirection;

    @Override
    public void writeCustomNBT(NBTTagCompound tags) {
        super.writeCustomNBT(tags);
        tags.setByte("Direction", (byte) this.faceDirection.ordinal());
    }

    @Override
    public void readCustomNBT(NBTTagCompound tags) {
        super.readCustomNBT(tags);
        this.faceDirection = ForgeDirection.getOrientation(tags.getByte("Direction"));
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
    public void setFacedDirection(EntityLivingBase player) {
        if (player == null) {
            this.faceDirection = ForgeDirection.UNKNOWN;
            return;
        }
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
    public ForgeDirection getForgeDirection() {
        return this.faceDirection != null ? this.faceDirection : ForgeDirection.UNKNOWN;
    }

    @Override
    public void setForgeDirection(ForgeDirection direction) {
        this.faceDirection = direction;
    }

    @Override
    public int getSizeInventory() {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.getSizeInventory();
        }
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.getStackInSlot(slotIn);
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.decrStackSize(index, count);
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.getStackInSlotOnClosing(index);
        }
        return null;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            masterEntity.markDirty();
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            masterEntity.setInventorySlotContents(index, stack);
        }
    }

    @Override
    public String getInventoryName() {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.getInventoryName();
        }
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.hasCustomInventoryName();
        }
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.getInventoryStackLimit();
        }
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.isUseableByPlayer(player);
        }
        return false;
    }

    @Override
    public void openInventory() {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            masterEntity.openInventory();
        }
    }

    @Override
    public void closeInventory() {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            masterEntity.closeInventory();
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.isItemValidForSlot(index, stack);
        }
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        int invSize = 0;
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            invSize = masterEntity.getSizeInventory();
        }
        return IntStream.range(0, invSize)
            .toArray();
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        if (this.getMaster() instanceof TinkersRebornMultiBlockInvenotryLogic masterEntity) {
            return masterEntity.canExtractItem(slot, stack);
        }
        return false;
    }
}
