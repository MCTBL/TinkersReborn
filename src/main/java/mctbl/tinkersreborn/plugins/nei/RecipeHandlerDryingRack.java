package mctbl.tinkersreborn.plugins.nei;

import java.awt.Rectangle;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.utils.DryingRecipe;
import mctbl.tinkersreborn.util.TinkersStr;

public class RecipeHandlerDryingRack extends RecipeHandlerBase {

    public static final String RECIPEID = "tinkersreborn.dryingrack";

    public class CachedDryingRackRecipe extends CachedBaseRecipe {

        public PositionedStack input;
        public PositionedStack output;
        public int time;

        public CachedDryingRackRecipe(ItemStack input, ItemStack output, int time) {
            this.input = new PositionedStack(input, 44, 18);
            this.output = new PositionedStack(output, 98, 18);
            this.time = time;
        }

        @Override
        public PositionedStack getIngredient() {
            return this.input;
        }

        @Override
        public PositionedStack getResult() {
            return this.output;
        }
    }

    @Override
    public String getRecipeName() {
        return TinkersStr.neiDryingrack.toString();
    }

    @Override
    public String getRecipeID() {
        return RECIPEID;
    }

    @Override
    public String getHandlerId() {
        return RECIPEID;
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(68, 20, 22, 15), this.getRecipeID()));
    }

    @Override
    public String getGuiTexture() {
        return "tinkersreborn:textures/gui/nei/dryingrack.png";
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 160, 65);
    }

    @Override
    public void drawExtras(int recipe) {
        int time = ((CachedDryingRackRecipe) this.arecipes.get(recipe)).time;
        int seconds = time / 20;
        GuiDraw.drawStringC(
            String.format(TinkersStr.neiDryingrackDuration.toString(), time, seconds),
            81,
            40,
            0x808080,
            false);
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getRecipeID())) {
            for (DryingRecipe drying : TinkersRebornRegistry.getAllDryingRecipes()) {
                for (ItemStack input : drying.input.getInputs()) {
                    this.arecipes.add(new CachedDryingRackRecipe(input, drying.getResult(), drying.time));
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (DryingRecipe drying : TinkersRebornRegistry.getAllDryingRecipes()) {
            for (ItemStack input : drying.input.getInputs()) {
                if (NEIServerUtils.areStacksSameTypeCrafting(drying.getResult(), result)) {
                    this.arecipes.add(new CachedDryingRackRecipe(input, drying.getResult(), drying.time));
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingred) {
        for (DryingRecipe drying : TinkersRebornRegistry.getAllDryingRecipes()) {
            for (ItemStack input : drying.input.getInputs()) {
                if (NEIServerUtils.areStacksSameTypeCrafting(input, ingred)) {
                    this.arecipes.add(new CachedDryingRackRecipe(input, drying.getResult(), drying.time));
                }
            }
        }
    }
}
