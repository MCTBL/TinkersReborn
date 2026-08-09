package mctbl.tinkersreborn.plugins.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import mctbl.tinkersreborn.library.smeltery.CastingRecipe;
import mctbl.tinkersreborn.library.smeltery.ICastingRecipe;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public abstract class RecipeHandlerCastingBase extends RecipeHandlerBase {

    public static final Rectangle MOLTEN_FLOW = new Rectangle(60, 8, 6, 11);
    public static final Rectangle MOLTEN_FLOW_NO_ITEM = new Rectangle(60, 8, 6, 27);

    public class CachedCastingRecipe extends CachedBaseRecipe {

        private final List<PositionedStack> resources;
        private final FluidTankElement metal;
        private PositionedStack output;
        private final int coolDownTime;

        public CachedCastingRecipe(CastingRecipe recipe) {
            FluidStack fluid = recipe.getFluid();
            this.metal = new FluidTankElement(MOLTEN_FLOW, fluid.amount, fluid);
            this.metal.flowingTexture = true;
            this.resources = new ArrayList<>();
            if (recipe.cast != null) {
                this.resources.add(new PositionedStack(recipe.cast.getInputs(), 55, 19));
            } else {
                this.metal.position = MOLTEN_FLOW_NO_ITEM;
            }
            this.output = new PositionedStack(recipe.getResult(), 110, 18);
            this.coolDownTime = recipe.getTime();
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return this.resources;
        }

        @Override
        public PositionedStack getResult() {
            return this.output;
        }

        public int getCoolDownTime() {
            return this.coolDownTime;
        }

        @Override
        public List<FluidTankElement> getFluidTanks() {
            List<FluidTankElement> res = new ArrayList<>();
            res.add(this.metal);
            return res;
        }
    }

    private boolean isValidRecipe(CastingRecipe recipe) {
        return !TinkersRebornUtils.isStackEmpty(recipe.getResult());
    }

    @Override
    public String getGuiTexture() {
        return "tinkersreborn:textures/gui/nei/casting.png";
    }

    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(76, 17, 115, 32, 23, 16, 60, 0);

        int time = ((CachedCastingRecipe) this.arecipes.get(recipe)).getCoolDownTime();
        GuiDraw.drawString(String.format(TinkersStr.neiCoolDownDuration.toString(), time), 74, 41, 0x808080, false);
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(30, 0, 0, 0, 114, 61);
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(76, 18, 22, 15), this.getRecipeID()));
    }

    public abstract List<ICastingRecipe> getCastingRecipes();

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getRecipeID())) {
            for (ICastingRecipe recipe : this.getCastingRecipes()) {
                if (recipe instanceof CastingRecipe re && this.isValidRecipe(re))
                    this.arecipes.add(new CachedCastingRecipe(re));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (ICastingRecipe recipe : getCastingRecipes()) {
            if (recipe instanceof CastingRecipe re && this.isValidRecipe(re)
                && NEIServerUtils.areStacksSameTypeCrafting(result, re.getResult())) {
                this.arecipes.add(new CachedCastingRecipe(re));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingred) {
        for (ICastingRecipe recipe : getCastingRecipes()) {
            if (recipe instanceof CastingRecipe re && this.isValidRecipe(re)
                && (re.cast == null || re.cast.matches(Arrays.asList(ingred))
                    .isPresent())) {
                CachedCastingRecipe irecipe = new CachedCastingRecipe(re);
                irecipe.setIngredientPermutation(irecipe.resources, ingred);
                this.arecipes.add(irecipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(FluidStack ingredient) {
        for (ICastingRecipe recipe : getCastingRecipes()) {
            if (recipe instanceof CastingRecipe re && this.isValidRecipe(re)
                && areFluidsEqual(re.getFluid(), ingredient)) {
                this.arecipes.add(new CachedCastingRecipe(re));
            }
        }
    }
}
