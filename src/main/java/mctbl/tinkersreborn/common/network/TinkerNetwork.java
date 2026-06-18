package mctbl.tinkersreborn.common.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.utils.BlockPos;

public class TinkerNetwork {

    public static TinkerNetwork instance = new TinkerNetwork();

    public final SimpleNetworkWrapper network;
    protected final AbstactPacketHandler handler;
    private int id = 0;

    public TinkerNetwork() {
        network = new SimpleNetworkWrapper(TinkersReborn.MODID);
        handler = new AbstactPacketHandler();
    }

    public void setUp() {
        // register packet

    }

    /**
     * Packet will be received on both client and server side.
     */
    public void registerPacket(Class<? extends AbstractPacket> packetClazz) {
        registerPacketClient(packetClazz);
        registerPacketServer(packetClazz);
    }

    /**
     * Packet will only be received on the client side
     */
    public void registerPacketClient(Class<? extends AbstractPacket> packetClazz) {
        registerPacketImpl(packetClazz, Side.CLIENT);
    }

    /**
     * Packet will only be received on the server side
     */
    public void registerPacketServer(Class<? extends AbstractPacket> packetClazz) {
        registerPacketImpl(packetClazz, Side.SERVER);
    }

    private void registerPacketImpl(Class<? extends AbstractPacket> packetClazz, Side side) {
        network.registerMessage(handler, packetClazz, id++, side);
    }

    public static class AbstactPacketHandler implements IMessageHandler<AbstractPacket, IMessage> {

        @Override
        public IMessage onMessage(AbstractPacket packet, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                return packet.handleServer(ctx);
            } else {
                return packet.handleClient(ctx);
            }
        }
    }

    public static void sendPacket(Entity player, Packet packet) {
        if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).playerNetServerHandler != null) {
            ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(packet);
        }
    }

    public static void sendToAll(AbstractPacket packet) {
        instance.network.sendToAll(packet);
    }

    public static void sendTo(AbstractPacket packet, EntityPlayerMP player) {
        instance.network.sendTo(packet, player);
    }

    public static void sendToAllAround(AbstractPacket packet, NetworkRegistry.TargetPoint point) {
        instance.network.sendToAllAround(packet, point);
    }

    public static void sendToDimension(AbstractPacket packet, int dimensionId) {
        instance.network.sendToDimension(packet, dimensionId);
    }

    public static void sendToServer(AbstractPacket packet) {
        instance.network.sendToServer(packet);
    }

    public static void sendToClients(WorldServer world, BlockPos pos, AbstractPacket packet) {
        Chunk chunk = world.getChunkFromBlockCoords(pos.x, pos.z);
        for (EntityPlayer player : world.playerEntities) {
            // only send to relevant players
            if (!(player instanceof EntityPlayerMP)) {
                continue;
            }
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            if (world.getPlayerManager()
                .isPlayerWatchingChunk(playerMP, chunk.xPosition, chunk.zPosition)) {
                TinkerNetwork.sendTo(packet, playerMP);
            }
        }
    }
}
