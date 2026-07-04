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
import mctbl.tinkersreborn.library.utils.BlockPos;

public class MultiServantLogic extends TileEntity implements IServantLogic {

    boolean hasMaster;
    BlockPos master;
    Block masterBlock;

    public boolean canUpdate() {
        return false;
    }

    public boolean getHasMaster() {
        return hasMaster;
    }

    public boolean hasValidMaster() {
        if (!hasMaster) return false;

        if (worldObj.getBlock(master.x, master.y, master.z) == masterBlock) return true;

        else {
            hasMaster = false;
            master = null;
            return false;
        }
    }

    @Override
    public boolean verifyMaster(IMasterLogic logic, World world, BlockPos pos) {
        if (this.hasMaster) {
            return this.hasValidMaster();
        } else {
            this.overrideMaster(pos);
            return true;
        }
    }

    public BlockPos getMasterPosition() {
        return master;
    }

    public void overrideMaster(BlockPos pos) {
        hasMaster = true;
        master = new BlockPos(pos.x, pos.y, pos.z);
        masterBlock = worldObj.getBlock(pos.x, pos.y, pos.z);
    }

    public void removeMaster() {
        hasMaster = false;
        master = null;
        masterBlock = null;
    }

    @Override
    public boolean setPotentialMaster(IMasterLogic master, World w, int x, int y, int z) {
        return !hasMaster;
    }

    @Override
    public void invalidateMaster(IMasterLogic master, World w, int x, int y, int z) {
        hasMaster = false;
        master = null;
    }

    public void notifyMasterOfChange() {
        if (hasValidMaster() && worldObj.getTileEntity(master.x, master.y, master.z) instanceof IMasterLogic logic) {
            logic.notifyChange(this, xCoord, yCoord, zCoord);
        }
    }

    public void readCustomNBT(NBTTagCompound tags) {
        hasMaster = tags.getBoolean("TiedToMaster");
        if (hasMaster) {
            int xCenter = tags.getInteger("xCenter");
            int yCenter = tags.getInteger("yCenter");
            int zCenter = tags.getInteger("zCenter");
            master = new BlockPos(xCenter, yCenter, zCenter);
            masterBlock = GameData.getBlockRegistry()
                .getObject(tags.getString("MasterBlockName"));
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
