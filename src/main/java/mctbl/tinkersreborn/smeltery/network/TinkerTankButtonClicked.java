package mctbl.tinkersreborn.smeltery.network;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.common.network.TinkerNetwork.AbstactPacketHandler;
import mctbl.tinkersreborn.library.gui.container.BaseContainer;
import mctbl.tinkersreborn.smeltery.entity.TinkerTankLogic;

public class TinkerTankButtonClicked extends AbstractPacketThreadsafe {

    public static class Handler extends AbstactPacketHandler {
    }

    public boolean isShiftClick;

    public TinkerTankButtonClicked(boolean isShiftClick) {
        this.isShiftClick = isShiftClick;
    }

    public TinkerTankButtonClicked() {
        this.isShiftClick = false;
    }

    @Override
    public void handleClientSafe(MessageContext netHandler) {
        // Serverside only
        throw new UnsupportedOperationException("Serverside only");
    }

    @Override
    public void handleServerSafe(MessageContext netHandler) {
        if (netHandler.getServerHandler().playerEntity.openContainer instanceof BaseContainer container
            && container.getTile() instanceof TinkerTankLogic tank) {
            tank.fillOrClearBucket(this.isShiftClick, netHandler.getServerHandler().playerEntity);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isShiftClick = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isShiftClick);
    }

}
