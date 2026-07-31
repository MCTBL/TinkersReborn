package mctbl.tinkersreborn.tools.events;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.util.TinkersStr;

public class TinkersRebornVanillaBowNerfHandler {

    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
        if (event.entityPlayer == null) return;

        if (event.result == null) return;

        if (isUselessBow(event.result.getItem())) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.entityPlayer == null) return;

        if (isUselessBow(event.itemStack.getItem())) {
            event.toolTip.add(EnumChatFormatting.DARK_RED + TinkersStr.uselessBow1.toString());
            event.toolTip.add(EnumChatFormatting.DARK_RED + TinkersStr.uselessTool2.toString());
        }
    }

    public static boolean isUselessBow(Item item) {
        if (item == null) return false;

        if (TinkersRebornTools.toolWhitelist.contains(item)) return false;

        return item instanceof ItemBow;
    }
}
