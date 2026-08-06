package mctbl.tinkersreborn.common.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.player.TinkersRebornPlayerStats;

public class TinkersRebornPlayerHandler {

    @SubscribeEvent
    public void onEntityConstructingWrapper(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer entityPlayer
            && TinkersRebornPlayerStats.get((EntityPlayer) event.entity) == null) {
            TinkersRebornPlayerStats.register(entityPlayer);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer newPlayer = event.entityPlayer;
        EntityPlayer oldPlayer = event.original;

        TinkersRebornPlayerStats oldStats = TinkersRebornPlayerStats.get(oldPlayer);
        TinkersRebornPlayerStats newStats = TinkersRebornPlayerStats.get(newPlayer);
        newStats.heartCanister = oldStats.heartCanister;

        newStats.init(newPlayer, newPlayer.worldObj);
        newStats.heartCanister.recalculateHealth(newPlayer, newStats);

        if (TinkersRebornConfig.keepHunger) {
            newPlayer.getFoodStats()
                .setFoodLevel(
                    oldPlayer.getFoodStats()
                        .getFoodLevel());
        }
    }
}
