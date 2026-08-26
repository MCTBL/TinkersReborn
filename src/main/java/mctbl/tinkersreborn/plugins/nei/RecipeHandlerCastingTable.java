package mctbl.tinkersreborn.plugins.nei;

import java.util.List;

import codechicken.lib.gui.GuiDraw;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.smeltery.ICastingRecipe;
import mctbl.tinkersreborn.util.TinkersStr;

public class RecipeHandlerCastingTable extends RecipeHandlerCastingBase {

    public static final String RECIPEID = "tinkersreborn.smeltery.casting";

    @Override
    public String getRecipeID() {
        return RECIPEID;
    }

    @Override
    public String getHandlerId() {
        return RECIPEID;
    }

    @Override
    public String getRecipeName() {
        return TinkersStr.neiCastingtable.toString();
    }

    @Override
    public void drawBackground(int recipe) {
        super.drawBackground(recipe);
        GuiDraw.drawTexturedModalRect(55, 35, 115, 0, 16, 16);
    }

    @Override
    public List<ICastingRecipe> getCastingRecipes() {
        return TinkersRebornRegistry.getTableCasting();
    }

}
