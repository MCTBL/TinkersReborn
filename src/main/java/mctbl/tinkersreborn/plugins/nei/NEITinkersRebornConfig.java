package mctbl.tinkersreborn.plugins.nei;

import codechicken.nei.api.IConfigureNEI;

public class NEITinkersRebornConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
//        API.registerGuiOverlay(GuiCraftingStation.class, "tinkersreborn.craftingstation");
    }

    @Override
    public String getName() {
        return "TinkersReborn";
    }

    @Override
    public String getVersion() {
        return "${version}";
    }

}
