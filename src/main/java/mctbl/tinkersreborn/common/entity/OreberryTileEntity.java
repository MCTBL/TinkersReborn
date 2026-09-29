package mctbl.tinkersreborn.common.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class OreberryTileEntity extends TileEntity {

    public static final String STATE_STRING_KEY = "state";
    public int state;

    public void bushGrow() {
        if (!this.worldObj.isRemote && this.state < 3) {
            this.state++;
            this.markDirty();
            this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public boolean harvest() {
        if (!this.worldObj.isRemote && this.state == 3) {
            this.state--;
            this.markDirty();
            this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return true;
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.state = compound.getInteger(STATE_STRING_KEY);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(STATE_STRING_KEY, this.state);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(STATE_STRING_KEY, this.state);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        this.state = packet.func_148857_g()
            .getInteger(STATE_STRING_KEY);
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

}
