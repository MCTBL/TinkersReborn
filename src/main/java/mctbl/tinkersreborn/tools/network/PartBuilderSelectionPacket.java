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
import mctbl.tinkersreborn.common.network.TinkerNetwork.AbstactPacketHandler;
import mctbl.tinkersreborn.tools.gui.GuiPartBuilder;
import mctbl.tinkersreborn.tools.inventory.ContainerPartBuilder;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;

public class PartBuilderSelectionPacket extends AbstractPacketThreadsafe {

    public static class Handler extends AbstactPacketHandler {
    }

    TinkersRebornToolPart toolpart;

    public PartBuilderSelectionPacket() {}

    public PartBuilderSelectionPacket(TinkersRebornToolPart toolpart) {
        this.toolpart = toolpart;
    }

    @Override
    public void handleClientSafe(MessageContext netHandler) {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;
        if (container instanceof ContainerPartBuilder cpb) {
            cpb.toolPartSelected(this.toolpart);
            if (Minecraft.getMinecraft().currentScreen instanceof GuiPartBuilder gpb) {
                gpb.onPartSelectionPacket(this);
            }
        }
    }

    @Override
    public void handleServerSafe(MessageContext ctx) {
        Container container = ctx.getServerHandler().playerEntity.openContainer;
        if (container instanceof ContainerPartBuilder cpb) {
            cpb.toolPartSelected(this.toolpart);

            // find all people who also have the same gui open and update them too
            WorldServer server = ctx.getServerHandler().playerEntity.getServerForPlayer();
            for (EntityPlayer player : server.playerEntities) {
                if (player == ctx.getServerHandler().playerEntity) {
                    continue;
                }
                if (player.openContainer instanceof ContainerPartBuilder base) {
                    if (cpb.sameGui(base)) {
                        base.toolPartSelected(this.toolpart);
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
            if (item instanceof TinkersRebornToolPart) {
                toolpart = (TinkersRebornToolPart) item;
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (toolpart == null) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(Item.getIdFromItem(toolpart));
        }
    }

}
