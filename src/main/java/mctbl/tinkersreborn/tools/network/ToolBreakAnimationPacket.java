package mctbl.tinkersreborn.tools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.common.network.TinkerNetwork.AbstactPacketHandler;

public class ToolBreakAnimationPacket extends AbstractPacketThreadsafe {

    public static class Handler extends AbstactPacketHandler {
    }

    public ItemStack breakingTool;

    public ToolBreakAnimationPacket() {}

    public ToolBreakAnimationPacket(ItemStack breakingTool) {
        this.breakingTool = breakingTool;
    }

    @Override
    public void handleClientSafe(MessageContext netHandler) {
        // play the animation
        Minecraft.getMinecraft().thePlayer.renderBrokenItemStack(breakingTool);

    }

    @Override
    public void handleServerSafe(MessageContext netHandler) {
        // clientside only
        throw new UnsupportedOperationException("Clientside only");
    }

    @Override
    public void fromBytes(ByteBuf buf) {

        breakingTool = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, breakingTool);
    }
}
