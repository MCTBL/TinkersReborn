package mctbl.tinkersreborn.tools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.tools.inventory.ContainerToolStation;

public class ToolStationTextPacket extends AbstractPacketThreadsafe {

    public String text;

    public ToolStationTextPacket() {}

    public ToolStationTextPacket(String text) {
        this.text = text;
    }

    @Override
    public void handleClientSafe(MessageContext ctx) {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;
        if (container instanceof ContainerToolStation tsc) {
            tsc.setToolName(text);
        }
    }

    @Override
    public void handleServerSafe(MessageContext ctx) {
        NetHandlerPlayServer netHandler = ctx.getServerHandler();
        Container container = netHandler.playerEntity.openContainer;
        if (container instanceof ContainerToolStation tsc) {
            if (tsc.toolName != null && text != null && tsc.toolName.equals(text)) return;

            tsc.setToolName(text);

            // find all people who also have the same gui open and update them too
            WorldServer server = netHandler.playerEntity.getServerForPlayer();
            for (EntityPlayer player : server.playerEntities) {
                if (player.openContainer instanceof ContainerToolStation other) {
                    if (tsc.sameGui(other)) {
                        // same gui, send him an update
                        other.setToolName(text);
                        TinkerNetwork.sendTo(this, (EntityPlayerMP) player);
                    }
                }
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        text = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, text);
    }
}
