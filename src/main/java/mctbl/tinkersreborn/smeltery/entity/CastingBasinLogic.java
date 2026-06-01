package mctbl.tinkersreborn.smeltery.entity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.crafting.CastingRecipe;
import mctbl.tinkersreborn.library.event.SmelteryCastEvent;
import mctbl.tinkersreborn.library.event.SmelteryCastedEvent;

public class CastingBasinLogic extends CastingBlockLogic {

    public CastingBasinLogic() {
        super(TinkersRebornRegistry.basinCasting);
    }

    @Override
    public SmelteryCastEvent getCastingEvent(CastingRecipe recipe, FluidStack metal) {
        return new SmelteryCastEvent.CastingBasin(recipe, metal);
    }

    @Override
    public SmelteryCastedEvent getCastedEvent(CastingRecipe recipe, ItemStack result) {
        return new SmelteryCastedEvent.CastingBasin(recipe, result);
    }
}
