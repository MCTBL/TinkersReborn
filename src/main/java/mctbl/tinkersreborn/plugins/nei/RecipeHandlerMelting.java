package mctbl.tinkersreborn.plugins.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.smeltery.utils.MeltingRecipe;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class RecipeHandlerMelting extends RecipeHandlerBase {

    public static final String RECIPEID = "tinkersreborn.melting";

    public static final Rectangle MOLTEN_TANK = new Rectangle(115, 20, 18, 18);

    public class CachedMeltingRecipe extends CachedBaseRecipe {

        private final PositionedStack input;
        private final int temperature;
        private final FluidTankElement output;

        public CachedMeltingRecipe(MeltingRecipe melting) {
            this.input = new PositionedStack(melting.input.getInputs(), 28, 21);
            this.temperature = melting.temperature;
            this.output = new FluidTankElement(MOLTEN_TANK, 1, melting.getResult());
            this.output.capacity = this.output.fluid != null ? this.output.fluid.amount : 1000;
        }

        @Override
        public PositionedStack getIngredient() {
            return this.input;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public List<FluidTankElement> getFluidTanks() {
            List<FluidTankElement> tanks = new ArrayList<>();
            tanks.add(this.output);
            return tanks;
        }
    }

    @Override
    public String getRecipeName() {
        return TinkersStr.neiMelting.toString();
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
        this.transferRects.add(new RecipeTransferRect(new Rectangle(72, 20, 16, 34), this.getRecipeID()));
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 160, 55);
    }

    @Override
    public void drawExtras(int recipe) {
        int temperature = ((CachedMeltingRecipe) this.arecipes.get(recipe)).temperature;
        GuiDraw.drawStringC(TinkersRebornUtils.temperatureString(temperature), 81, 9, 0x808080, false);
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getRecipeID())) {
            for (MeltingRecipe recipe : TinkersRebornRegistry.getAllMeltingRecipies()) {
                this.arecipes.add(new CachedMeltingRecipe(recipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(FluidStack result) {
        for (MeltingRecipe recipe : TinkersRebornRegistry.getAllMeltingRecipies()) {
            if (areFluidsEqual(recipe.output, result)) {
                this.arecipes.add(new CachedMeltingRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingred) {
        for (MeltingRecipe recipe : TinkersRebornRegistry.getAllMeltingRecipies()) {
            if (recipe.matches(ingred)) {
                this.arecipes.add(new CachedMeltingRecipe(recipe));
            }
        }
    }
}
