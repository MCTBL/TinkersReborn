package mctbl.tinkersreborn.util;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.tools.AmmoCore;
import mctbl.tinkersreborn.library.tools.ToolCore;

public class AmmoHelper {

    private AmmoHelper() {}

    public static ItemStack findAmmoFromInventory(List<Item> ammoItems, EntityLivingBase entity) {
        if (ammoItems == null || entity == null || !(entity instanceof EntityPlayer player)) {
            return null;
        }

        // we specifically check the equipment inventory first because it contains the
        // offhand
        Container itemHandler = player.inventoryContainer;
        ItemStack ammo = null;
        if (TinkersRebornUtils.isStackEmpty(ammo)) {
            if (itemHandler != null) {
                int hotbarSize = 0;
                // find an itemstack that matches our input. Hotbar first
                if (player instanceof EntityPlayer) {
                    hotbarSize = Math.min(InventoryPlayer.getHotbarSize(), itemHandler.inventorySlots.size());
                    ammo = validAmmoInRange(itemHandler, ammoItems, 0, hotbarSize);
                }
                // then remaining inventory
                if (TinkersRebornUtils.isStackEmpty(ammo)) {
                    ammo = validAmmoInRange(itemHandler, ammoItems, hotbarSize, itemHandler.inventorySlots.size());
                }
            }
        }

        return ammo;
    }

    private static ItemStack validAmmoInRange(Container container, List<Item> ammoItems, int from, int to) {
        for (int i = from; i < to; i++) {
            ItemStack in = container.getSlot(i)
                .getStack();
            for (Item ammoItem : ammoItems) {
                // same item
                if (!TinkersRebornUtils.isStackEmpty(in) && in.getItem() == ammoItem) {
                    // no ammoitem or ammoitem with ammo
                    if (!(ammoItem instanceof AmmoCore ammo) || ammo.getCurrentAmmo(in) > 0) {
                        return in;
                    }
                }
            }
        }
        return null;
    }

    public static ItemStack getMatchingItemstackFromInventory(ItemStack stack, Entity entity, boolean damagedOnly) {
        if (stack == null) {
            return null;
        }

        // try main and off hand first, because priority (yes they're also covered in the loop below.)
        if (entity instanceof EntityPlayer player) {
            ItemStack in = player.getHeldItem();
            if (ToolCore.isEqualTinkersItem(in, stack) && (!damagedOnly || in.getItemDamage() > 0)) {
                return in;
            }
            Container itemHandler = player.inventoryContainer;

            // find an itemstack that matches our input
            assert itemHandler != null;
            for (int i = 0; i < itemHandler.inventorySlots.size(); i++) {
                in = itemHandler.getSlot(i)
                    .getStack();
                if (ToolCore.isEqualTinkersItem(in, stack) && (!damagedOnly || in.getItemDamage() > 0)) {
                    return in;
                }
            }
        }

        return null;
    }
}
