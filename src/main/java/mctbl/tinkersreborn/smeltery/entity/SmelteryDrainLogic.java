package mctbl.tinkersreborn.smeltery.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;

public class SmelteryDrainLogic extends MultiServantLogic implements IFluidHandler, ITinkersRebornIFacingLogic {

    public ForgeDirection faceDirection;

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (this.getMaster() instanceof IFluidTank tank && resource != null && canFill(from, resource.getFluid())) {
            return tank.fill(resource, doFill);
        } else {
            return 0;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (this.getMaster() instanceof IFluidTank tank && canDrain(from, resource.getFluid())
            && resource.getFluid() == tank.getFluid()
                .getFluid()) {
            return tank.drain(resource.amount, doDrain);
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (this.getMaster() instanceof IFluidTank tank && canDrain(from, null)) {
            return tank.drain(maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        // Check that the drain is coming from the from the front of the block
        // and that the fluid to be drained is in the smeltery.
        if (!hasValidMaster()) return false;

        boolean containsFluid = fluid == null;
        if (fluid != null) {
            List<FluidStack> fluidList = null;

            if (this.getMaster() instanceof SmelteryLogic tank) {
                fluidList = tank.moltenMetal;
            } else if (this.getMaster() instanceof TinkerTankLogic tank) {
                fluidList = tank.fluids;
            }

            if (fluidList != null) {
                for (FluidStack fstack : fluidList) {
                    if (fstack.getFluidID() == fluid.getID()) {
                        containsFluid = true;
                        break;
                    }
                }
            }
        }
        return containsFluid;
    }

    @Override
    @Nullable
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if ((from == getForgeDirection() || from == getForgeDirection().getOpposite()
            || from == ForgeDirection.UNKNOWN)) {
            if (this.getMaster() instanceof SmelteryLogic tank) {
                return tank.getMultiTankInfo();
            } else if (this.getMaster() instanceof TinkerTankLogic tank) {
                return tank.getMultiTankInfo();
            }
        }
        return null;
    }

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

}
