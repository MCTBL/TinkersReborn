package mctbl.tinkersreborn.tools.events;

import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.util.TinkersStr;

public class TinkersRebornVanillaHoeNerfHandler {

    @SubscribeEvent
    public void onHoeBlock(UseHoeEvent event) {
        // don't modify hoeing without tool (from machines, if they even send an event.)
        if (event.current == null) return;

        if (isUselessHoe(event.current.getItem())) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.entityPlayer == null) return;

        if (isUselessHoe(event.itemStack.getItem())) {
            event.toolTip.add(EnumChatFormatting.DARK_RED + TinkersStr.uselessHoe1.toString());
            event.toolTip.add(EnumChatFormatting.DARK_RED + TinkersStr.uselessTool2.toString());
        }
    }

    public static boolean isUselessHoe(Item item) {
        if (item == null) return false;

        if (TinkersRebornTools.toolWhitelist.contains(item)) return false;

        return item instanceof ItemHoe;
    }
}
