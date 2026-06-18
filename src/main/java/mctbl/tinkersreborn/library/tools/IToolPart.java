package mctbl.tinkersreborn.library.tools;

import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;

public interface IToolPart {

    /**
     * Returns the material of the part this itemstack holds.
     *
     * @return TinkersRebornMaterial
     */
    TinkersRebornMaterial getMaterial(ItemStack stack);
}
