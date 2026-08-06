package mctbl.tinkersreborn.common.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.common.player.TinkersRebornPlayerStats;

public class TinkersRebornPlayerHandler {

    @SubscribeEvent
    public void onEntityConstructingWrapper(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer entityPlayer
            && TinkersRebornPlayerStats.get((EntityPlayer) event.entity) == null) {
            TinkersRebornPlayerStats.register(entityPlayer);
        }
    }

}
