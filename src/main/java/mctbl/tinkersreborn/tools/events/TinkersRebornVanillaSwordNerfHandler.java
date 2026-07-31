package mctbl.tinkersreborn.tools.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.util.TinkersStr;

public class TinkersRebornVanillaSwordNerfHandler {

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (!(event.source.damageType.equals("player"))) return;

        // only players
        if (!(event.source.getEntity() instanceof EntityPlayer player)) return;
        // the tool
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null) return;

        if (isUselessWeapon(stack.getItem())) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.entityPlayer == null) return;

        if (isUselessWeapon(event.itemStack.getItem())) {
            event.toolTip.add(EnumChatFormatting.DARK_RED + TinkersStr.uselessWeapon1.toString());
            event.toolTip.add(EnumChatFormatting.DARK_RED + TinkersStr.uselessTool2.toString());
        }
    }

    public static boolean isUselessWeapon(Item item) {
        if (item == null) return false;

        if (TinkersRebornTools.toolWhitelist.contains(item)) return false;

        return item instanceof ItemSword;
    }
}
