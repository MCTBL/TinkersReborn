package mctbl.tinkersreborn.tools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.tools.gui.GuiToolStation;
import mctbl.tinkersreborn.tools.inventory.ContainerToolStation;

public class ToolStationSelectionPacket extends AbstractPacketThreadsafe {

    public ToolCore tool;
    public int activeSlots;

    public ToolStationSelectionPacket() {}

    public ToolStationSelectionPacket(ToolCore tool, int activeSlots) {
        this.tool = tool;
        this.activeSlots = activeSlots;
    }

    @Override
    public void handleClientSafe(MessageContext ctx) {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;
        if (container instanceof ContainerToolStation tsc) {
            tsc.setToolSelection(tool, activeSlots);
            if (Minecraft.getMinecraft().currentScreen instanceof GuiToolStation gts) {
                gts.onToolSelectionPacket(this);
            }
        }
    }

    @Override
    public void handleServerSafe(MessageContext ctx) {
        Container container = ctx.getServerHandler().playerEntity.openContainer;
        if (container instanceof ContainerToolStation tsc) {
            tsc.setToolSelection(tool, activeSlots);

            // find all people who also have the same gui open and update them too
            WorldServer server = ctx.getServerHandler().playerEntity.getServerForPlayer();
            for (EntityPlayer player : server.playerEntities) {
                if (player == ctx.getServerHandler().playerEntity) {
                    continue;
                }
                if (player.openContainer instanceof ContainerToolStation base) {
                    if (tsc.sameGui(base)) {
                        base.setToolSelection(tool, activeSlots);
                        // same gui, send him an update
                        TinkerNetwork.sendTo(this, (EntityPlayerMP) player);
                    }
                }
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int id = buf.readShort();
        if (id > -1) {
            Item item = Item.getItemById(id);
            if (item instanceof ToolCore) {
                tool = (ToolCore) item;
            }
        }

        activeSlots = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (tool == null) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(Item.getIdFromItem(tool));
        }

        buf.writeInt(activeSlots);
    }
}
