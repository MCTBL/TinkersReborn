package mctbl.tinkersreborn.tools.events;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.util.TinkersStr;

public class TinkersRebornVanillaToolNerfEvent {

    @SubscribeEvent
    public void breakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.entityPlayer == null) return;

        ItemStack itemStack = event.entityPlayer.getCurrentEquippedItem();
        if (itemStack == null) return;

        if (isUselessTool(itemStack.getItem())) event.newSpeed = 0;
    }

    @SubscribeEvent
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.entityPlayer == null) return;

        if (isUselessTool(event.itemStack.getItem())) {
            event.toolTip.add(EnumChatFormatting.DARK_RED + TinkersStr.uselessTool1.toString());
            event.toolTip.add(EnumChatFormatting.DARK_RED + TinkersStr.uselessTool2.toString());
        }
    }

    public static boolean isUselessTool(Item item) {
        if (item == null) return false;

        if (TinkersRebornTools.toolWhitelist.contains(item)) return false;

        return item instanceof ItemTool;
    }
}
