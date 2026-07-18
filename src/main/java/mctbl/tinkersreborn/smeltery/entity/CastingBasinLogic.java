package mctbl.tinkersreborn.smeltery.entity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.smeltery.ICastingRecipe;

public class CastingBasinLogic extends CastingBlockLogic {

    @Override
    protected ICastingRecipe findRecipe(ItemStack cast, Fluid fluid) {
        return TinkersRebornRegistry.getBasinCasting(cast, fluid);
    }

}
