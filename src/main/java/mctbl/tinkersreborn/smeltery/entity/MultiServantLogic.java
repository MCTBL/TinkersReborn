package mctbl.tinkersreborn.smeltery.entity;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameData;
import mctbl.tinkersreborn.library.entity.IMasterLogic;
import mctbl.tinkersreborn.library.entity.IServantLogic;
import mctbl.tinkersreborn.library.world.CoordTuple;

public class MultiServantLogic extends TileEntity implements IServantLogic {

    boolean hasMaster;
    CoordTuple master;
    Block masterBlock;
    byte masterMate;

    public boolean canUpdate() {
        return false;
    }

    public boolean getHasMaster() {
        return hasMaster;
    }

    public boolean hasValidMaster() {
        if (!hasMaster) return false;

        if (worldObj.getBlock(master.x, master.y, master.z) == masterBlock
            && worldObj.getBlockMetadata(master.x, master.y, master.z) == masterMate) return true;

        else {
            hasMaster = false;
            master = null;
            return false;
        }
    }

    public CoordTuple getMasterPosition() {
        return master;
    }

    public void overrideMaster(int x, int y, int z) {
        hasMaster = true;
        master = new CoordTuple(x, y, z);
        masterBlock = worldObj.getBlock(x, y, z);
        masterMate = (byte) worldObj.getBlockMetadata(x, y, z);
    }

    public void removeMaster() {
        hasMaster = false;
        master = null;
        masterBlock = null;
        masterMate = 0;
    }

    @Override
    public boolean setPotentialMaster(IMasterLogic master, World w, int x, int y, int z) {
        return !hasMaster;
    }

    @Override
    public boolean verifyMaster(IMasterLogic logic, World world, int x, int y, int z) {
        if (hasMaster) {
            return hasValidMaster();
        } else {
            overrideMaster(x, y, z);
            return true;
        }
    }

    @Override
    public void invalidateMaster(IMasterLogic master, World w, int x, int y, int z) {
        hasMaster = false;
        master = null;
    }

    public void notifyMasterOfChange() {
        if (hasValidMaster()) {
            IMasterLogic logic = (IMasterLogic) worldObj.getTileEntity(master.x, master.y, master.z);
            logic.notifyChange(this, xCoord, yCoord, zCoord);
        }
    }

    public void readCustomNBT(NBTTagCompound tags) {
        hasMaster = tags.getBoolean("TiedToMaster");
        if (hasMaster) {
            int xCenter = tags.getInteger("xCenter");
            int yCenter = tags.getInteger("yCenter");
            int zCenter = tags.getInteger("zCenter");
            master = new CoordTuple(xCenter, yCenter, zCenter);
            masterBlock = GameData.getBlockRegistry()
                .getObject(tags.getString("MasterBlockName"));
            masterMate = tags.getByte("masterMate");
        }
    }

    public void writeCustomNBT(NBTTagCompound tags) {
        tags.setBoolean("TiedToMaster", hasMaster);
        if (hasMaster) {
            tags.setInteger("xCenter", master.x);
            tags.setInteger("yCenter", master.y);
            tags.setInteger("zCenter", master.z);
            tags.setString(
                "MasterBlockName",
                GameData.getBlockRegistry()
                    .getNameForObject(masterBlock));
            tags.setByte("masterMate", masterMate);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public World getWorld() {
        return super.getWorldObj();
    }

}
