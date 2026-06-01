package mctbl.tinkersreborn.smeltery.entity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.crafting.CastingRecipe;
import mctbl.tinkersreborn.library.event.SmelteryCastEvent;
import mctbl.tinkersreborn.library.event.SmelteryCastedEvent;

public class CastingTableLogic extends CastingBlockLogic {

    public CastingTableLogic() {
        super(TinkersRebornRegistry.tableCasting);
    }

    @Override
    public SmelteryCastEvent getCastingEvent(CastingRecipe recipe, FluidStack metal) {
        return new SmelteryCastEvent.CastingTable(recipe, metal);
    }

    @Override
    public SmelteryCastedEvent getCastedEvent(CastingRecipe recipe, ItemStack result) {
        return new SmelteryCastedEvent.CastingTable(recipe, result);
    }
}
