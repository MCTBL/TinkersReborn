package mctbl.tinkersreborn.smeltery.entity;

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

import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;

public class SmelteryDrainLogic extends MultiServantLogic implements IFluidHandler, ITinkersRebornIFacingLogic {

    public ForgeDirection faceDirection;

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (hasValidMaster() && resource != null && canFill(from, resource.getFluid())) {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.fill(resource, doFill);
        } else {
            return 0;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (hasValidMaster() && canDrain(from, resource.getFluid())) {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            if (resource.getFluid() == smeltery.getFluid()
                .getFluid()) {
                return smeltery.drain(resource.amount, doDrain);
            }
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (hasValidMaster() && canDrain(from, null)) {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.drain(maxDrain, doDrain);
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
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            for (FluidStack fstack : smeltery.moltenMetal) {
                if (fstack.getFluidID() == fluid.getID()) {
                    containsFluid = true;
                    break;
                }
            }
        }
        return containsFluid;
    }

    @Override
    @Nullable
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (hasValidMaster() && (from == getForgeDirection() || from == getForgeDirection().getOpposite()
            || from == ForgeDirection.UNKNOWN)) {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj
                .getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.getMultiTankInfo();
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

    public int comparatorStrength() {
        BlockPos master = this.getMasterPosition();
        // invalid smeltery
        if (master == null) return 0;
        SmelteryLogic smeltery = (SmelteryLogic) worldObj.getTileEntity(master.x, master.y, master.z);

        // this can happen when the comparator checks its strength before the drain is getting updated on a broken
        // master (smeltery controller)
        if (smeltery == null) return 0;

        if (smeltery.maxMoltenMetalAmount == 0) return 0;

        return MathHelper.ceiling_float_int(15f * smeltery.currentMoltenMetalAmount / smeltery.maxMoltenMetalAmount);
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

    @Override
    public ForgeDirection getForgeDirection() {
        return this.faceDirection;
    }

    @Override
    public void setForgeDirection(ForgeDirection direction) {
        this.faceDirection = direction;
    }

}
