package mctbl.tinkersreborn.common.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.library.entity.TinkersEntityProperties;

public class TinkerEntityPropertiesSyncPacket extends AbstractPacketThreadsafe {

    public static class Handler extends TinkerNetwork.AbstactPacketHandler {
    }

    private int entityId;
    private Map<String, int[]> effects = new HashMap<>();

    public TinkerEntityPropertiesSyncPacket() {}

    public TinkerEntityPropertiesSyncPacket(EntityPlayer player, TinkersEntityProperties properties) {
        entityId = player.getEntityId();
        for (Map.Entry<String, TinkersEntityProperties.Effect> entry : properties.getEffects()
            .entrySet()) {
            effects.put(
                entry.getKey(),
                new int[] { entry.getValue()
                    .getRemainingTicks(),
                    entry.getValue()
                        .getLevel() });
        }
    }

    @Override
    public void handleClientSafe(MessageContext context) {
        if (Minecraft.getMinecraft().theWorld == null) return;
        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
        if (!(entity instanceof EntityPlayer)) return;
        TinkersEntityProperties properties = TinkersEntityProperties.getProps((EntityPlayer) entity);
        if (properties == null) return;
        Map<String, TinkersEntityProperties.Effect> replacement = new HashMap<>();
        for (Map.Entry<String, int[]> entry : effects.entrySet()) {
            replacement
                .put(entry.getKey(), new TinkersEntityProperties.Effect(entry.getValue()[0], entry.getValue()[1]));
        }
        properties.replaceEffects(replacement);
    }

    @Override
    public void handleServerSafe(MessageContext context) {
        throw new UnsupportedOperationException("Clientside only");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        int size = buf.readInt();
        effects = new HashMap<>();
        for (int i = 0; i < size; i++) {
            effects.put(ByteBufUtils.readUTF8String(buf), new int[] { buf.readInt(), buf.readInt() });
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(effects.size());
        for (Map.Entry<String, int[]> entry : effects.entrySet()) {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeInt(entry.getValue()[0]);
            buf.writeInt(entry.getValue()[1]);
        }
    }
}
