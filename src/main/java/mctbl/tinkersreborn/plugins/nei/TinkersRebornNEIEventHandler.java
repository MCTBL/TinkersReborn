package mctbl.tinkersreborn.plugins.nei;

import net.minecraft.item.ItemStack;

import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;

public class TinkersRebornNEIEventHandler {

    @SubscribeEvent
    public void registerHandlerInfo(NEIRegisterHandlerInfosEvent event) {
        event.registerHandlerInfo(
            RecipeHandlerDryingRack.RECIPEID,
            "TinkersReborn",
            TinkersReborn.MODID,
            builder -> builder.setDisplayStack(new ItemStack(TinkersRebornGeneral.dryingRack))
                .setHeight(65)
                .setWidth(160)
                .setMultipleWidgetsAllowed(true));

        event.registerHandlerInfo(
            RecipeHandlerMelting.RECIPEID,
            "TinkersReborn",
            TinkersReborn.MODID,
            builder -> builder.setDisplayStack(new ItemStack(TinkersRebornSmeltery.smelteryController))
                .setHeight(55)
                .setWidth(160)
                .setMultipleWidgetsAllowed(true));
    }

}
