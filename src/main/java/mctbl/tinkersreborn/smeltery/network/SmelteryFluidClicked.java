package mctbl.tinkersreborn.smeltery.network;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.common.network.TinkerNetwork.AbstactPacketHandler;
import mctbl.tinkersreborn.library.gui.container.BaseContainer;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;

// Fired when a player clicks a fluid in the smeltery GUI to move it to the bottom
public class SmelteryFluidClicked extends AbstractPacketThreadsafe {

    public static class Handler extends AbstactPacketHandler {
    }

    public int index; // index of the clicked fluid

    public SmelteryFluidClicked() {}

    public SmelteryFluidClicked(int index) {
        this.index = index;
    }

    @Override
    public void handleClientSafe(MessageContext netHandler) {
        // Serverside only
        throw new UnsupportedOperationException("Serverside only");
    }

    @Override
    public void handleServerSafe(MessageContext ctx) {
        if (ctx.getServerHandler().playerEntity.openContainer instanceof BaseContainer container) {
            TileEntity te = container.getTile();
            if (te instanceof SmelteryLogic smeltery) {
                // smeltery.getTank().moveFluidToBottom(index);
                smeltery.moveFluidToFirst(index);
                smeltery.onTankChanged(smeltery.moltenMetal, null);
            }
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
