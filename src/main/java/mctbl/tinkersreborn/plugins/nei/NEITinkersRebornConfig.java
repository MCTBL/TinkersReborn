package mctbl.tinkersreborn.plugins.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEITinkersRebornConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        // API.registerGuiOverlay(GuiCraftingStation.class, "tinkersreborn.craftingstation");
        registerHandler(new RecipeHandlerDryingRack());
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
