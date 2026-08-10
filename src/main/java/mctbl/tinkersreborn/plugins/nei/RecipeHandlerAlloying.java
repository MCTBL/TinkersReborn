package mctbl.tinkersreborn.plugins.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.crafting.AlloyRecipe;
import mctbl.tinkersreborn.util.TinkersStr;

public class RecipeHandlerAlloying extends RecipeHandlerBase {

    public static final String RECIPEID = "tinkersreborn.smeltery.alloying";

    public static final Rectangle OUTPUT_TANK = new Rectangle(118, 12, 18, 32);

    public class CachedAlloyingRecipe extends CachedBaseRecipe {

        private final List<FluidTankElement> fluidTanks;
        private int minAmount;

        public CachedAlloyingRecipe(AlloyRecipe recipe) {
            this.fluidTanks = new ArrayList<>();

            int maxAmount = recipe.getFluids()
                .get(0).amount;
            int mult = 1;
            this.minAmount = maxAmount;
            for (FluidStack stack : recipe.getFluids()) {
                if (stack.amount > maxAmount) {
                    maxAmount = stack.amount;
                }
                if (stack.amount < this.minAmount) {
                    this.minAmount = stack.amount;
                }
            }
            FluidTankElement tank = new FluidTankElement(OUTPUT_TANK, maxAmount * mult, recipe.getResult());
            tank.fluid.amount *= mult;
            this.fluidTanks.add(tank);

            int width = 36 / recipe.getFluids()
                .size();
            int counter = 0;
            for (FluidStack stack : recipe.getFluids()) {
                if (counter == recipe.getFluids()
                    .size() - 1) {
                    tank = new FluidTankElement(
                        new Rectangle(21 + width * counter, 12, 36 - width * counter, 32),
                        maxAmount * mult,
                        stack);
                } else {
                    tank = new FluidTankElement(
                        new Rectangle(21 + width * counter, 12, width, 32),
                        maxAmount * mult,
                        stack);
                }
                tank.fluid.amount *= mult;
                this.fluidTanks.add(tank);
                counter++;
            }
        }

        @Override
        public PositionedStack getIngredient() {
            return null;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public List<FluidTankElement> getFluidTanks() {
            return this.fluidTanks;
        }
    }

    @Override
    public String getRecipeName() {
        return TinkersStr.neiAlloying.toString();
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
    public String getGuiTexture() {
        return "tinkersreborn:textures/gui/nei/smeltery.png";
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(76, 24, 22, 15), this.getRecipeID()));
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 59, 160, 65);
    }

    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(76, 21, 160, 0, 23, 16, 60, 0);
        super.drawExtras(recipe);
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getRecipeID())) {
            for (AlloyRecipe recipe : TinkersRebornRegistry.getAlloys()) {
                if (!recipe.getFluids()
                    .isEmpty()) {
                    this.arecipes.add(new CachedAlloyingRecipe(recipe));
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(FluidStack result) {
        for (AlloyRecipe recipe : TinkersRebornRegistry.getAlloys()) {
            if (areFluidsEqual(recipe.getResult(), result) && !recipe.getFluids()
                .isEmpty()) {
                this.arecipes.add(new CachedAlloyingRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(FluidStack ingredient) {
        for (AlloyRecipe recipe : TinkersRebornRegistry.getAlloys()) {
            for (FluidStack liquid : recipe.getFluids()) {
                if (areFluidsEqual(liquid, ingredient) && !recipe.getFluids()
                    .isEmpty()) {
                    this.arecipes.add(new CachedAlloyingRecipe(recipe));
                    break;
                }
            }
        }
    }
}
