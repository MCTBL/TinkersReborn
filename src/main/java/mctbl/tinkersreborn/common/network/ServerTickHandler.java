package mctbl.tinkersreborn.common.network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cpw.mods.fml.common.gameevent.TickEvent;

public class ServerTickHandler {

    static final Queue<Runnable> serverTasks = new ConcurrentLinkedQueue<>();

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Runnable task;
            while ((task = serverTasks.poll()) != null) {
                task.run();
            }
        }
    }
}
