package mctbl.tinkersreborn.library.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Callback interface for head drop registration.
 * Allows dynamic ItemStack generation based on the entity instance,
 * e.g. writing a player's GameProfile into a skull item.
 */
@FunctionalInterface
public interface HeadDropCallback {

    /**
     * Returns the head ItemStack for the given entity,
     * or null if no head should be dropped.
     */
    ItemStack getHeadDrop(EntityLivingBase entity);
}
