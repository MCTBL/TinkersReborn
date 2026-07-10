package mctbl.tinkersreborn.smeltery.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import mctbl.tinkersreborn.library.blocks.IActiveLogic;
import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;

public class FaucetLogic extends TileEntity implements ITinkersRebornIFacingLogic, IActiveLogic, IFluidHandler {

    public ForgeDirection faceDirection;

    boolean active;
    public FluidStack liquid;
    public boolean hasRedstonePower = false;

    public boolean activateFaucet() {
        if (liquid == null && active) {
            int x = xCoord - getForgeDirection().offsetX;
            int z = zCoord - getForgeDirection().offsetZ;

            TileEntity drainte = worldObj.getTileEntity(x, yCoord, z);
            TileEntity tankte = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);

            if (drainte instanceof IFluidHandler && tankte instanceof IFluidHandler) {
                FluidStack templiquid = ((IFluidHandler) drainte)
                    .drain(getForgeDirection(), TinkersRebornMaterial.VALUE_Ingot, false);
                if (templiquid != null) {
                    int drained = ((IFluidHandler) tankte).fill(ForgeDirection.UP, templiquid, false);
                    if (drained > 0) {
                        liquid = ((IFluidHandler) drainte).drain(getForgeDirection(), drained, true);
                        ((IFluidHandler) tankte).fill(ForgeDirection.UP, liquid, true);
                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void updateEntity() {
        if (liquid != null) {
            liquid.amount -= TinkersRebornMaterial.VALUE_Ingot;
            if (liquid.amount <= 0) {
                liquid = null;
                if (!activateFaucet()) {
                    active = false;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    public void readCustomNBT(NBTTagCompound tags) {
        this.faceDirection = ForgeDirection.getOrientation(tags.getByte("Direction"));
        if (tags.getBoolean("hasLiquid")) {
            this.liquid = FluidStack.loadFluidStackFromNBT(tags.getCompoundTag("Fluid"));
        } else this.liquid = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void writeCustomNBT(NBTTagCompound tags) {
        tags.setByte("Direction", (byte) this.faceDirection.ordinal());
        tags.setBoolean("hasLiquid", liquid != null);
        if (liquid != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            tags.setTag("Fluid", nbt);
        }
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readCustomNBT(packet.func_148857_g());
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public void setActive(boolean flag) {
        if (!active) {
            active = true;
            active = activateFaucet();
        } else {
            active = false;
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    @Nullable
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return null;
    }

    @Override
    public ForgeDirection getForgeDirection() {
        return this.faceDirection;
    }

    @Override
    public void setFrogeDirection(ForgeDirection direction) {
        this.faceDirection = direction;
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

}
