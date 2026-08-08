package mctbl.tinkersreborn.plugins.nei;

import net.minecraft.item.ItemStack;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;

public class NEITinkersRebornConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        // API.registerGuiOverlay(GuiCraftingStation.class, "tinkersreborn.craftingstation");
        RecipeHandlerDryingRack handler = new RecipeHandlerDryingRack();
        registerHandler(handler);
        API.addRecipeCatalyst(new ItemStack(TinkersRebornGeneral.dryingRack), RecipeHandlerDryingRack.RECIPEID);
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
