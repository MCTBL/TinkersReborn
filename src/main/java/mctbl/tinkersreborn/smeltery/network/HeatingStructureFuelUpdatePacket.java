package mctbl.tinkersreborn.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.common.network.TinkerNetwork.AbstactPacketHandler;
import mctbl.tinkersreborn.library.entity.TinkersRebornMultiBlockInvenotryLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;

// Sent to the client when the smeltery consumes fuel
public class HeatingStructureFuelUpdatePacket extends AbstractPacketThreadsafe {

    public static class Handler extends AbstactPacketHandler {
    }

    public BlockPos pos;
    public BlockPos tank;
    public int temperature;
    public FluidStack fuel;

    public HeatingStructureFuelUpdatePacket() {}

    public HeatingStructureFuelUpdatePacket(BlockPos pos, BlockPos tank, int temperature, FluidStack fuel) {
        this.pos = pos;
        this.tank = tank;
        this.temperature = temperature;
        this.fuel = fuel;
    }

    @Override
    public void handleClientSafe(MessageContext netHandler) {
        TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(this.pos.x, this.pos.y, this.pos.z);
        if (te instanceof TinkersRebornMultiBlockInvenotryLogic trmbi) trmbi.updateFuelTemperatureFromPacket(this);

    }

    @Override
    public void handleServerSafe(MessageContext ctx) {
        // Clientside only
        throw new UnsupportedOperationException("Clientside only");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = readPos(buf);
        tank = readPos(buf);

        temperature = buf.readInt();

        NBTTagCompound fluidTag = ByteBufUtils.readTag(buf);
        fuel = FluidStack.loadFluidStackFromNBT(fluidTag);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePos(pos, buf);
        writePos(tank, buf);

        buf.writeInt(temperature);

        NBTTagCompound fluidTag = new NBTTagCompound();
        fuel.writeToNBT(fluidTag);
        ByteBufUtils.writeTag(buf, fluidTag);
    }
}
