package mctbl.tinkersreborn.plugins.nei;

import net.minecraft.item.ItemStack;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;

public class NEITinkersRebornConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        registerHandler(new RecipeHandlerDryingRack());
        registerHandler(new RecipeHandlerMelting());

        API.addRecipeCatalyst(new ItemStack(TinkersRebornGeneral.dryingRack), RecipeHandlerDryingRack.RECIPEID);
        API.addRecipeCatalyst(new ItemStack(TinkersRebornSmeltery.smelteryController), RecipeHandlerMelting.RECIPEID);
    }

    @Override
    public String getName() {
        return "TinkersReborn";
    }

    @Override
    public String getVersion() {
        return "${version}";
    }

    private static void registerHandler(RecipeHandlerBase handler) {
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
    }
}
