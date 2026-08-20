package mctbl.tinkersreborn.common.manuals.pages;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.common.manuals.TinkersRebornTurnPageButton;
import mctbl.tinkersreborn.common.manuals.TinkersRebornTurnPageButton.ButtonType;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.library.manuals.TinkersRebornRecipeHolder;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class RecipePage extends AbstractManualPage {

    protected final String title;
    protected String translatedTitle;
    protected final String text;
    protected String translatedText;
    protected final String align;

    protected TinkersRebornRecipeHolder[] recipes;

    // for loop crafting recipe
    int counter = 0;

    int selectedIdx;
    private TinkersRebornTurnPageButton previousRecipeButton;
    private TinkersRebornTurnPageButton nextRecipeButton;
    private TinkersRebornRecipeHolder selectedRecipe;

    private static final ItemStack fuel = TinkersRebornRegistry.getOrRegisterManualIcon("minecraft:coal");

    private static final ResourceLocation craftingTexture = new ResourceLocation(
        "tinkersreborn",
        "textures/gui/book/crafting.png");
    private static final ResourceLocation smeltingTexture = new ResourceLocation(
        "tinkersreborn",
        "textures/gui/book/smelting.png");
    private static final ResourceLocation modifyTexture = new ResourceLocation(
        "tinkersreborn",
        "textures/gui/book/modify.png");

    protected RecipePage(JsonObject json) {
        super(json);
        this.text = json.has("text") ? json.get("text")
            .getAsString() : "";
        this.title = json.has("title") ? json.get("title")
            .getAsString() : "";
        this.align = json.has("align") ? json.get("align")
            .getAsString() : "left";

        String iconStr = json.has("output") ? json.get("output")
            .getAsString() : null;

        // TODO NBT recipe?
        // ItemStack tempStack = TinkersRebornRegistry.getItemStackFromString(iconStr);
        // if(json.has("nbt")) {
        // NBTTagCompound tag = new NBTTagCompound();
        // for(Entry<String, JsonElement> entry : json.get("nbt").getAsJsonObject().entrySet()) {
        // JsonElement ele = entry.getValue();
        // try {
        // tag.setInteger(entry.getKey(), ele.getAsInt());
        // }catch (ClassCastException e) {
        // tag.setString(entry.getKey(), ele.getAsString());
        // }
        // }
        // tempStack.setTagCompound(tag);
        // }
        // TinkersRebornRegistry.registerManualIcon(iconStr, tempStack);

        this.recipes = TinkersRebornRegistry.getOrRegisterRecipeIcon(iconStr);
        this.previousRecipeButton = new TinkersRebornTurnPageButton(0, 8, 15, ButtonType.previousPage);
        this.nextRecipeButton = new TinkersRebornTurnPageButton(
            1,
            contentWidth - 8 - ButtonType.previousPage.getTextureWidth(),
            15,
            ButtonType.nextPage);
        this.selectedIdx = 0;
    }

    @Override
    public void renderPage(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        this.selectedRecipe = this.recipes[this.selectedIdx];
        if (manualTicks == 19) {
            this.counter = (this.counter + 1) % this.selectedRecipe.varietyOfOre;
        }
        super.renderPage(pageX, pageY, manualMouseX, manualMouseY, partialTicks, manualTicks, manual);
    }

    @Override
    public void renderBackgroundLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        if (this.selectedIdx >= 0 && this.selectedIdx < this.recipes.length) {
            switch (this.selectedRecipe.recipeType) {
                case FURNACE -> this.renderFurnaceRecipeBackGround(pageX, pageY, manual);
                case TOOLSTATION -> this.renderToolStationRecipeBackGround();
                default -> {
                    if (this.selectedRecipe.recipeSize == 2) {
                        this.render22CraftingRecipeBackGround(pageX, pageY, manual);
                    } else {
                        this.render33CraftingRecipeBackGround(pageX, pageY, manual);
                    }
                }
            }

        }

    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {

        if (this.translatedTitle != null && !this.translatedTitle.isEmpty()) {
            String underLineTitle = ColorUtil.addUnderLine(translatedTitle);
            if (this.align.equals("center")) {
                this.drawStrCenterAt(underLineTitle, pageX + contentWidth / 2, pageY);
            } else if (this.align.equals("right")) {
                fontRender.drawString(
                    underLineTitle,
                    pageX + contentWidth - fontRender.getStringWidth(underLineTitle),
                    pageY,
                    0x000000);
            } else {
                fontRender.drawString(underLineTitle, pageX, pageY, 0x000000);
            }
        }

        if (this.translatedText != null && !this.translatedText.isEmpty()) {
            fontRender.drawSplitString(translatedText, pageX, pageY + 114, 180, 0x000000);
        }

        if (this.selectedIdx >= 0 && this.selectedIdx < this.recipes.length) {
            this.setUpForRenderItem();
            switch (this.selectedRecipe.recipeType) {
                case FURNACE -> this.renderFurnaceRecipeItemStack(pageX, pageY, manual);
                case TOOLSTATION -> this.renderToolStationRecipeItemStack();
                default -> {
                    if (this.selectedRecipe.recipeSize == 2) {
                        this.render22CraftingRecipeItemStack(pageX, pageY, manual);
                    } else {
                        this.render33CraftingRecipeItemStack(pageX, pageY, manual);
                    }
                }
            }
            this.backUpForRenderItem();
        }

        this.drawManualControls(manualMouseX, manualMouseY, pageX, pageY);
    }

    private void drawManualControls(int manualMouseX, int manualMouseY, int pageX, int pageY) {
        this.updateManualControlState();
        this.previousRecipeButton.drawButton(Minecraft.getMinecraft(), manualMouseX, manualMouseY, pageX, pageY);
        this.nextRecipeButton.drawButton(Minecraft.getMinecraft(), manualMouseX, manualMouseY, pageX, pageY);
    }

    private void updateManualControlState() {
        this.previousRecipeButton.visible = this.selectedIdx > 0;
        this.nextRecipeButton.visible = this.selectedIdx + 1 < this.recipes.length;
    }

    @Override
    public void setupTranslate() {
        if (this.title.isEmpty() && this.recipes.length > 0) {
            this.translatedTitle = this.recipes[0].outputStack.getDisplayName();
        } else {
            this.translatedTitle = TinkersRebornUtils.translate(this.title);
        }
        this.translatedText = TinkersRebornUtils.translate(this.text);
    }

    private void renderFurnaceRecipeBackGround(int pageX, int pageY, GuiManual manual) {
        manual.mc.getTextureManager()
            .bindTexture(smeltingTexture);
        GL11.glColor4f(manual.backgroundR, manual.backgroundG, manual.backgroundB, 1.0F);
        manual.drawTexturedModalRect(pageX + 35, pageY, 0, 0, 114, 111);
    }

    private void renderFurnaceRecipeItemStack(int pageX, int pageY, GuiManual manual) {

    }

    private void renderToolStationRecipeBackGround() {}

    private void renderToolStationRecipeItemStack() {}

    private void render22CraftingRecipeBackGround(int pageX, int pageY, GuiManual manual) {
        manual.mc.getTextureManager()
            .bindTexture(craftingTexture);
        GL11.glColor4f(manual.backgroundR, manual.backgroundG, manual.backgroundB, 1.0F);
        manual.drawTexturedModalRect(pageX + 19, pageY + 18, 0, 114, 155, 78);
    }

    private void render22CraftingRecipeItemStack(int pageX, int pageY, GuiManual manual) {
        ItemStack[][] inputStacks = selectedRecipe.inputStacks;
        ItemStack outputStack = selectedRecipe.outputStack;

        this.renderItemStackIntoPage(outputStack, (pageX + 137) / 2, (pageY + 41) / 2, manual);

        for (int i = 0; i < inputStacks.length; i++) {
            if (inputStacks[i] != null && inputStacks[i][0] != null) {
                ItemStack renderStack = inputStacks[i][this.counter % inputStacks[i].length];
                this.renderItemStackIntoPage(
                    renderStack,
                    (pageX + 25 + 36 * (i % 2)) / 2,
                    (pageY + 24 + (i / 2) * 35) / 2,
                    manual);
            }
        }
    }

    private void render33CraftingRecipeBackGround(int pageX, int pageY, GuiManual manual) {
        manual.mc.getTextureManager()
            .bindTexture(craftingTexture);
        GL11.glColor4f(manual.backgroundR, manual.backgroundG, manual.backgroundB, 1.0F);
        manual.drawTexturedModalRect(pageX - 1, pageY, 0, 0, 183, 114);
    }

    private void render33CraftingRecipeItemStack(int pageX, int pageY, GuiManual manual) {}

    public static class RecipePageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            return Arrays.asList(new RecipePage(definition.getData()));
        }

    }

}
