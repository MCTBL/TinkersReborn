package mctbl.tinkersreborn.smeltery.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;

public class CastingChannelLogic extends TileEntity implements IFluidHandler {

    public static final int fillMax = TinkersRebornMaterial.VALUE_Ingot * 3;
    public static final int outputMax = TinkersRebornMaterial.VALUE_Ingot;
    FluidTank internalTank = new FluidTank(fillMax);
    Map<ForgeDirection, FluidTank> subTanks = new EnumMap<>(ForgeDirection.class);
    public ForgeDirection lastProvider;
    public List<ForgeDirection> validOutputs = new ArrayList<>();
    public int ticks = 0;
    public int recentlyFilledDelay;

    public CastingChannelLogic() {
        lastProvider = ForgeDirection.UNKNOWN;
        validOutputs.add(ForgeDirection.DOWN);
        validOutputs.add(ForgeDirection.NORTH);
        validOutputs.add(ForgeDirection.SOUTH);
        validOutputs.add(ForgeDirection.EAST);
        validOutputs.add(ForgeDirection.WEST);

        subTanks.put(ForgeDirection.NORTH, new FluidTank(fillMax / 4));
        subTanks.put(ForgeDirection.SOUTH, new FluidTank(fillMax / 4));
        subTanks.put(ForgeDirection.WEST, new FluidTank(fillMax / 4));
        subTanks.put(ForgeDirection.EAST, new FluidTank(fillMax / 4));
    }

    @Override
    public void updateEntity() {
        ticks++;

        if (ticks == 6) // && !flagActiveFaucet)
            this.distributeFluids();
        if (ticks >= 12) // && !flagActiveFaucet)
        {
            if (recentlyFilledDelay != 0) recentlyFilledDelay--;
            if (recentlyFilledDelay == 0 || lastProvider == ForgeDirection.UP) this.outputFluids();

            ticks = 0;
        }
    }

    public void changeOutputs(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ForgeDirection toggle = ForgeDirection.UNKNOWN;
        if (side == 0 || side == 1) {
            if (hitX > 0.3125 && hitX < 0.6875) if (hitZ > 0 && hitZ < 0.3125) toggle = ForgeDirection.NORTH;
            if (hitX > 0.3125 && hitX < 0.6875) if (hitZ > 0.6875 && hitZ < 1) toggle = ForgeDirection.SOUTH;
            if (hitZ > 0.3125 && hitZ < 0.6875) if (hitX > 0 && hitX < 0.3125) toggle = ForgeDirection.WEST;
            if (hitZ > 0.3125 && hitZ < 0.6875) if (hitX > 0.6875 && hitX < 1) toggle = ForgeDirection.EAST;

            if (side == 0)
                if (hitX > 0.3125 && hitX < 0.6875) if (hitZ > 0.3125 && hitZ < 0.6875) toggle = ForgeDirection.DOWN;
        } else {
            if (side == 2) {
                if (hitX > 0 && hitX < 0.3125) toggle = ForgeDirection.WEST;
                if (hitX > 0.6875 && hitX < 1) toggle = ForgeDirection.EAST;
            }
            if (side == 3) {
                if (hitX > 0 && hitX < 0.3125) toggle = ForgeDirection.WEST;
                if (hitX > 0.6875 && hitX < 1) toggle = ForgeDirection.EAST;
            }
            if (side == 4) {
                if (hitZ > 0 && hitZ < 0.3125) toggle = ForgeDirection.NORTH;
                if (hitZ > 0.6875 && hitZ < 1) toggle = ForgeDirection.SOUTH;
            }
            if (side == 5) {
                if (hitZ > 0 && hitZ < 0.3125) toggle = ForgeDirection.NORTH;
                if (hitZ > 0.6875 && hitZ < 1) toggle = ForgeDirection.SOUTH;
            }
        }

        if (toggle != ForgeDirection.UNKNOWN && toggle != ForgeDirection.UP) {
            TileEntity tile = worldObj
                .getTileEntity(xCoord + toggle.offsetX, yCoord + toggle.offsetY, zCoord + toggle.offsetZ);
            if (tile instanceof IFluidHandler) if (validOutputs.contains(toggle)) {
                validOutputs.remove(toggle);
                if (tile instanceof CastingChannelLogic channel && toggle != ForgeDirection.DOWN)
                    channel.validOutputs.remove(toggle.getOpposite());
            } else {
                validOutputs.add(toggle);
                if (tile instanceof CastingChannelLogic channel && toggle != ForgeDirection.DOWN
                    && !channel.validOutputs.contains(toggle.getOpposite())) {
                    channel.validOutputs.add(toggle.getOpposite());
                }
            }
        }
        markDirtyForRendering();
    }

    private void distributeFluids() {
        // Move Fluid to Output Tanks
        Set<ForgeDirection> connected = this.getOutputs()
            .keySet();

        connected.remove(ForgeDirection.DOWN);
        connected.remove(lastProvider);

        int output = Math.min(internalTank.getFluidAmount(), outputMax);
        int connectedAmount = connected.size();
        if (connectedAmount < 1) connectedAmount = 1;
        int scaledAmount = output / connectedAmount;

        for (ForgeDirection dirOut : connected) {
            if (this.internalTank.getFluid() == null) break;

            FluidStack tempFS = new FluidStack(
                internalTank.getFluid()
                    .getFluid(),
                scaledAmount);
            int fit = subTanks.get(dirOut)
                .fill(tempFS, false);

            if (fit > 0) fit = internalTank.drain(fit, true).amount;

            tempFS.amount = fit;
            fit = subTanks.get(dirOut)
                .fill(tempFS, true);

            markDirtyForRendering();
        }

        // Get Fluid from most recent InputTank
        FluidTank inputTank = subTanks.get(lastProvider);
        if (inputTank != null && inputTank.getFluid() != null) {
            // Tank can be null if input was received from top
            FluidStack tempFS = new FluidStack(
                inputTank.getFluid()
                    .getFluid(),
                Math.min(inputTank.getFluidAmount(), outputMax));
            int fit = internalTank.fill(tempFS, false);

            if (fit > 0) fit = inputTank.drain(fit, true).amount;

            tempFS.amount = fit;
            fit = internalTank.fill(tempFS, true);

            markDirtyForRendering();
        }
    }

    private void outputFluids() {

        Map<ForgeDirection, TileEntity> connected = this.getOutputs();
        connected.remove(lastProvider);
        if (connected.containsKey(ForgeDirection.DOWN) && this.internalTank.getFluid() != null) // Prioritizes
                                                                                                // FluidHandlers below
        {
            int output = Math.min(internalTank.getFluid().amount, outputMax);
            FluidStack tempFS = new FluidStack(
                internalTank.getFluid()
                    .getFluid(),
                output);
            int fittingBelow = ((IFluidHandler) connected.get(ForgeDirection.DOWN))
                .fill(ForgeDirection.UP, tempFS, false);
            if (fittingBelow > 0) fittingBelow = this.drain(ForgeDirection.DOWN, fittingBelow, true).amount;
            tempFS.amount = fittingBelow;
            fittingBelow = ((IFluidHandler) connected.get(ForgeDirection.DOWN)).fill(ForgeDirection.UP, tempFS, true);

            connected.remove(ForgeDirection.DOWN);
        }
        if (connected.size() != 0) {
            for (ForgeDirection dir : connected.keySet()) {
                // Iterates to connected FluidHandlers
                if (subTanks.get(dir) != null && subTanks.get(dir)
                    .getFluid() != null) {

                    FluidStack tempFS = new FluidStack(
                        subTanks.get(dir)
                            .getFluid()
                            .getFluid(),
                        Math.min(
                            subTanks.get(dir)
                                .getFluidAmount(),
                            outputMax));
                    int fit = ((IFluidHandler) connected.get(dir)).fill(dir.getOpposite(), tempFS, false);

                    if (fit > 0) fit = this.drain(dir, fit, true).amount;

                    tempFS.amount = fit;
                    fit = ((IFluidHandler) connected.get(dir)).fill(dir.getOpposite(), tempFS, true);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public float tankBelow() {
        TileEntity te = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
        if (te instanceof CastingChannelLogic) return -0.5f;
        if (te instanceof LavaTankLogic) return -1.0f;
        if (te instanceof CastingBasinLogic) return -0.75f;
        if (te instanceof CastingTableLogic) return -0.125f;
        if (te instanceof IFluidHandler) return 0f;

        return 0.5f;
    }

    private void markDirtyForRendering() {
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public Map<ForgeDirection, TileEntity> getOutputs() {
        Map<ForgeDirection, TileEntity> map = new EnumMap<>(ForgeDirection.class);
        for (ForgeDirection fd : this.validOutputs) {
            int tX = this.xCoord + fd.offsetX;
            int tY = this.yCoord + fd.offsetY;
            int tZ = this.zCoord + fd.offsetZ;
            TileEntity tile = this.worldObj.getTileEntity(tX, tY, tZ);
            if (tile instanceof IFluidHandler) map.put(fd, tile);
        }
        return map;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (doFill) {
            markDirtyForRendering();
            this.lastProvider = from;
        }

        if (from == ForgeDirection.UP) {
            return this.internalTank.fill(resource, doFill);
        } else if (from == ForgeDirection.NORTH || from == ForgeDirection.SOUTH
            || from == ForgeDirection.WEST
            || from == ForgeDirection.EAST) {
                return this.subTanks.get(from)
                    .fill(resource, doFill);
            }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        FluidStack fluidStack = null;
        if (from == ForgeDirection.DOWN)
            if (this.internalTank.getFluid() != null) fluidStack = this.internalTank.getFluid();
            else if (validOutputs.contains(from)) fluidStack = this.subTanks.get(from)
                .getFluid();

        // wrong fluid
        if (fluidStack != null && fluidStack.getFluid() != resource.getFluid()) return null;

        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (from == ForgeDirection.DOWN) return this.internalTank.drain(maxDrain, doDrain);
        else if (from == ForgeDirection.NORTH || from == ForgeDirection.SOUTH
            || from == ForgeDirection.WEST
            || from == ForgeDirection.EAST) {
                return this.subTanks.get(from)
                    .drain(maxDrain, doDrain);
            }

        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (from == ForgeDirection.DOWN) return false;
        if (from == ForgeDirection.UP) return true;
        return validOutputs.contains(from);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if (from == ForgeDirection.UP) return false;
        return validOutputs.contains(from);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (from == null || from == ForgeDirection.UP || from == ForgeDirection.DOWN || from == ForgeDirection.UNKNOWN)
            return new FluidTankInfo[] { new FluidTankInfo(internalTank) };
        else return new FluidTankInfo[] { new FluidTankInfo(subTanks.get(from)) };
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    public void readCustomNBT(NBTTagCompound tags) {
        NBTTagCompound nbtTank = tags.getCompoundTag("internalTank");
        if (nbtTank != null) internalTank.readFromNBT(nbtTank);

        for (ForgeDirection fdSub : subTanks.keySet()) {
            NBTTagCompound nbtSubTank = tags.getCompoundTag("subTank_" + fdSub.name());
            if (nbtSubTank != null) subTanks.get(fdSub)
                .readFromNBT(nbtSubTank);
        }

        int[] validFDs = tags.getIntArray("validOutputs");
        if (validFDs != null) {
            validOutputs = new ArrayList<>();
            for (int i : validFDs) validOutputs.add(convertIntToFD(i));
        }
        this.lastProvider = this.convertIntToFD(tags.getInteger("LastProvider"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void writeCustomNBT(NBTTagCompound tags) {
        NBTTagCompound nbtTank = new NBTTagCompound();
        internalTank.writeToNBT(nbtTank);
        tags.setTag("internalTank", nbtTank);
        for (ForgeDirection fdSub : subTanks.keySet()) {
            NBTTagCompound nbtSubTank = new NBTTagCompound();
            subTanks.get(fdSub)
                .writeToNBT(nbtSubTank);
            tags.setTag("subTank_" + fdSub.name(), nbtSubTank);
        }

        int[] validFDs = new int[validOutputs.size()];
        int it = 0;
        for (ForgeDirection fd : validOutputs) {
            if (fd != null) validFDs[it] = convertFDToInt(fd);
            it++;
        }
        tags.setIntArray("validOutputs", validFDs);
        tags.setInteger("LastProvider", this.convertFDToInt(this.lastProvider));
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readCustomNBT(packet.func_148857_g());
        this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
    }

    public int convertFDToInt(ForgeDirection dir) {
        return dir.ordinal();
    }

    public ForgeDirection convertIntToFD(int i) {
        return ForgeDirection.getOrientation(i);
    }
}
