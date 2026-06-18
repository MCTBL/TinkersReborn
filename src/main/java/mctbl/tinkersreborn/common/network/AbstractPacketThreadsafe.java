package mctbl.tinkersreborn.common.network;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractPacketThreadsafe extends AbstractPacket {

    public static void init() {
        FMLCommonHandler.instance()
            .bus()
            .register(new ServerTickHandler());
    }

    @Override
    public final IMessage handleClient(MessageContext ctx) {
        Minecraft.getMinecraft()
            .func_152344_a(new Runnable() {

                @Override
                public void run() {
                    handleClientSafe(ctx);
                }
            });
        return null;
    }

    @Override
    public final IMessage handleServer(MessageContext ctx) {
        ServerTickHandler.serverTasks.add(new Runnable() {

            @Override
            public void run() {
                handleServerSafe(ctx);
            }
        });
        return null;
    }

    public abstract void handleClientSafe(MessageContext netHandler);

    public abstract void handleServerSafe(MessageContext netHandler);

}
