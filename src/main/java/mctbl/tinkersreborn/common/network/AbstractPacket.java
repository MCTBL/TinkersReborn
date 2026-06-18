package mctbl.tinkersreborn.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.library.utils.BlockPos;

public abstract class AbstractPacket implements IMessage {

    public abstract IMessage handleClient(MessageContext netHandler);

    public abstract IMessage handleServer(MessageContext netHandler);

    protected void writePos(BlockPos pos, ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    protected BlockPos readPos(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        return new BlockPos(x, y, z);
    }
}
