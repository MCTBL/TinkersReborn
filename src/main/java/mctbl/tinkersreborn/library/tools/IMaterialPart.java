package mctbl.tinkersreborn.library.tools;

import net.minecraft.item.ItemStack;

/**
 * If a part has material info need implement this interface
 * Author MCTBL
 * Time 2026-05-24 07:19:41
 */
public interface IMaterialPart {

    public int getMaterialId(ItemStack stack);

}
