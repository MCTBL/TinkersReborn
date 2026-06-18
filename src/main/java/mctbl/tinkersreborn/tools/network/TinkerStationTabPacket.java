package mctbl.tinkersreborn.tools.network;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.library.blocks.ITinkersToolStationBlock;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

/**
 * Sent to the server when the user clicks on a tab in the TinkerStation GUI
 */
public class TinkerStationTabPacket extends AbstractPacketThreadsafe {

    public int blockX;
    public int blockY;
    public int blockZ;

    public TinkerStationTabPacket() {}

    @SideOnly(Side.CLIENT)
    public TinkerStationTabPacket(BlockPos pos) {
        this.blockX = pos.getX();
        this.blockY = pos.getY();
        this.blockZ = pos.getZ();
    }

    @Override
    public void handleClientSafe(MessageContext ctx) {
        // Serverside only
        throw new UnsupportedOperationException("Serverside only");
    }

    @Override
    public void handleServerSafe(MessageContext ctx) {
        NetHandlerPlayServer netHandler = ctx.getServerHandler();
        EntityPlayerMP player = netHandler.playerEntity;

        ItemStack heldStack = null;
        if (!TinkersRebornUtils.isStackEmpty(player.inventory.getItemStack())) {
            heldStack = player.inventory.getItemStack();
            // set it to null so it's not getting dropped
            player.inventory.setItemStack(null);
        }

        BlockPos pos = new BlockPos(blockX, blockY, blockZ);

        Block b = player.getEntityWorld()
            .getBlock(blockX, blockY, blockZ);
        if (b instanceof ITinkersToolStationBlock ttb) {
            ttb.openGui(player, player.getEntityWorld(), pos);
        } else {
            player.openGui(TinkersReborn.instance, 0, player.getEntityWorld(), blockX, blockY, blockZ);
        }

        // set held item again for the new container
        if (heldStack != null) {
            player.inventory.setItemStack(heldStack);
            // also send it to the client
            netHandler.sendPacket(new S2FPacketSetSlot(-1, -1, heldStack));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockX = buf.readInt();
        blockY = buf.readInt();
        blockZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockX);
        buf.writeInt(blockY);
        buf.writeInt(blockZ);
    }
}
