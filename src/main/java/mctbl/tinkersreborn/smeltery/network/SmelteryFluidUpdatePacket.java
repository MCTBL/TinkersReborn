package mctbl.tinkersreborn.smeltery.network;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;

public class SmelteryFluidUpdatePacket extends AbstractPacketThreadsafe {

    public static class Handler extends AbstactPacketHandler {
    }

    public BlockPos pos;
    public List<FluidStack> liquids;

    public SmelteryFluidUpdatePacket() {}

    public SmelteryFluidUpdatePacket(BlockPos pos, List<FluidStack> liquids) {
        this.pos = pos;
        this.liquids = liquids.stream()
            .map(FluidStack::copy)
            .collect(Collectors.toList());
    }

    @Override
    public void handleClientSafe(MessageContext netHandler) {
        TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(pos.x, pos.y, pos.z);
        if (te instanceof SmelteryLogic smeltery) {
            smeltery.updateFluidsFromPacket(liquids);
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
        int size = buf.readInt();
        liquids = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            NBTTagCompound fluidTag = ByteBufUtils.readTag(buf);
            FluidStack liquid = FluidStack.loadFluidStackFromNBT(fluidTag);
            liquids.add(liquid);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePos(pos, buf);
        buf.writeInt(liquids.size());
        for (FluidStack liquid : liquids) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            liquid.writeToNBT(fluidTag);
            ByteBufUtils.writeTag(buf, fluidTag);
        }
    }
}
