package mctbl.tinkersreborn.smeltery.network;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.common.network.TinkerNetwork.AbstactPacketHandler;
import mctbl.tinkersreborn.library.gui.container.BaseContainer;
import mctbl.tinkersreborn.smeltery.entity.TinkerTankLogic;

// Fired when a player clicks a fluid in the smeltery GUI to move it to the bottom
public class TinkerTankFluidClicked extends AbstractPacketThreadsafe {

    public static class Handler extends AbstactPacketHandler {
    }

    public int index; // index of the clicked fluid

    public TinkerTankFluidClicked() {}

    public TinkerTankFluidClicked(int index) {
        this.index = index;
    }

    @Override
    public void handleClientSafe(MessageContext netHandler) {
        // Serverside only
        throw new UnsupportedOperationException("Serverside only");
    }

    @Override
    public void handleServerSafe(MessageContext ctx) {
        if (ctx.getServerHandler().playerEntity.openContainer instanceof BaseContainer container
            && container.getTile() instanceof TinkerTankLogic tank) {
            tank.moveFluidToFirst(index);
            tank.onTankChanged(tank.fluids);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(index);
    }
}
