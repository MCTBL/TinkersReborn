package mctbl.tinkersreborn.plugins.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.util.TinkersStr;

public class RecipeHandlerEntitySmeltery extends RecipeHandlerBase {

    public static final String RECIPEID = "tinkersreborn.smeltery.entity";

    public static final Rectangle OUTPUT_TANK = new Rectangle(119, 13, 18, 32);

    private World previewWorld;

    private final Map<Class<? extends EntityLivingBase>, EntityLivingBase> entityCache = new HashMap<>();

    public class CachedEntitySmelteryRecipe extends CachedBaseRecipe {

        public final Class<? extends EntityLivingBase> entityClass;
        public final List<FluidTankElement> fluid;

        public CachedEntitySmelteryRecipe(Class<? extends EntityLivingBase> entityClass, FluidStack fluid) {
            this.entityClass = entityClass;
            this.fluid = Arrays.asList(new FluidTankElement(OUTPUT_TANK, Math.max(32, fluid.amount), fluid));
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public List<FluidTankElement> getFluidTanks() {
            return this.fluid;
        }
    }

    private EntityLivingBase getPreviewEntity(Class<? extends EntityLivingBase> entityClass) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null) {
            return null;
        }

        if (previewWorld != mc.theWorld) {
            previewWorld = mc.theWorld;
            entityCache.clear();
        }

        return entityCache.computeIfAbsent(entityClass, type -> {
            try {
                return type.getConstructor(World.class)
                    .newInstance(mc.theWorld);
            } catch (ReflectiveOperationException e) {
                return null;
            }
        });
    }

    @Override
    public String getRecipeName() {
        return TinkersStr.neiEntitySmeltery.toString();
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
        GuiDraw.drawTexturedModalRect(0, 0, 0, 124, 160, 65);
    }

    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(76, 23, 160, 0, 23, 16, 60, 0);

        CachedEntitySmelteryRecipe cachedRecipe = (CachedEntitySmelteryRecipe) this.arecipes.get(recipe);
        EntityLivingBase previewEntity = this.getPreviewEntity(cachedRecipe.entityClass);
        if (previewEntity != null) {
            previewEntity.ticksExisted = cycleticks;
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            this.drawEntityLookingAtMouse(recipe, previewEntity, 39, 46, 24);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.drawExtras(recipe);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getRecipeID())) {
            for (Entry<String, FluidStack> entry : TinkersRebornRegistry.getAllEntityMelting()
                .entrySet()) {
                Class<? extends Entity> eneityClass = EntityList.stringToClassMapping.get(entry.getKey());
                if (EntityLivingBase.class.isAssignableFrom(eneityClass)) {
                    this.arecipes.add(
                        new CachedEntitySmelteryRecipe(
                            (Class<? extends EntityLivingBase>) eneityClass,
                            entry.getValue()));
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadCraftingRecipes(FluidStack result) {
        for (Entry<String, FluidStack> entry : TinkersRebornRegistry.getAllEntityMelting()
            .entrySet()) {
            if (areFluidsEqual(entry.getValue(), result)) {
                Class<? extends Entity> eneityClass = EntityList.stringToClassMapping.get(entry.getKey());
                if (EntityLivingBase.class.isAssignableFrom(eneityClass)) {
                    this.arecipes.add(
                        new CachedEntitySmelteryRecipe(
                            (Class<? extends EntityLivingBase>) eneityClass,
                            entry.getValue()));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadUsageRecipes(ItemStack ingred) {
        if (ingred.getItem() instanceof ItemMonsterPlacer) {
            FluidStack fluid = TinkersRebornRegistry
                .getMeltingForEntity(EntityList.getStringFromID(ingred.getItemDamage()));
            Class<? extends Entity> eneityClass = EntityList.getClassFromID(ingred.getItemDamage());
            if (fluid != null && EntityLivingBase.class.isAssignableFrom(eneityClass)) {
                this.arecipes
                    .add(new CachedEntitySmelteryRecipe((Class<? extends EntityLivingBase>) eneityClass, fluid));
            }
        }
    }

    private void drawEntityLookingAtMouse(int recipeIndex, EntityLivingBase entity, int entityX, int entityY,
        int scale) {
        if (entity == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen screen = mc.currentScreen;

        float lookX = 0.0F;
        float lookY = 0.0F;

        if (screen instanceof GuiRecipe<?>gui) {
            Point mouse = GuiDraw.getMousePosition();
            Point recipePosition = gui.getRecipePosition(recipeIndex);

            float localMouseX = mouse.x - gui.guiLeft - recipePosition.x;
            float localMouseY = mouse.y - gui.guiTop - recipePosition.y;
            lookX = entityX - localMouseX;

            float entityLookY = entityY - scale * 1.5F;
            lookY = entityLookY - localMouseY;

            lookX = MathHelper.clamp_float(lookX, -80.0F, 80.0F);
            lookY = MathHelper.clamp_float(lookY, -80.0F, 80.0F);
        }
        GuiInventory.func_147046_a(entityX, entityY, scale, lookX, lookY, entity);
    }
}
