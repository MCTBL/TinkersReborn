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
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.entity.CastingBlockLogic;

public class FluidUpdatePacket extends AbstractPacketThreadsafe {

    public static class Handler extends AbstactPacketHandler {
    }

    public BlockPos pos;
    public FluidStack fluid;

    public FluidUpdatePacket() {}

    public FluidUpdatePacket(BlockPos pos, FluidStack fluid) {
        this.pos = pos;
        this.fluid = fluid;
    }

    @Override
    public void handleClientSafe(MessageContext netHandler) {
        TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(pos.x, pos.y, pos.z);
        if (te instanceof CastingBlockLogic t) {
            t.updateFluidTo(fluid);
        }
    }

    @Override
    public void handleServerSafe(MessageContext ctx) {
        // Clientside only
        throw new UnsupportedOperationException("Clientside only");

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = readPos(buf);
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        fluid = FluidStack.loadFluidStackFromNBT(tag);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePos(pos, buf);
        NBTTagCompound tag = new NBTTagCompound();
        if (fluid != null) {
            fluid.writeToNBT(tag);
        }
        ByteBufUtils.writeTag(buf, tag);
    }
}
