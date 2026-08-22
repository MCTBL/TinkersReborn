package mctbl.tinkersreborn.common.manuals.pages;

import java.util.List;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.TinkersRebornRecipeHolder.RecipeType;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.ToolCore.ToolPartRecord;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class ToolPage extends RecipePage {

    private static final String pattern = "{'title':tinkersreborn.tool.%s,'text':tinkersreborn.tool.%s.desc}";

    private static final int[][] inputPostiton = new int[][] { { 28, 28 }, { 7, 50 }, { 3, 24 }, { 28, 3 }, { 53, 24 },
        { 49, 50 } };
    private static final int backgroundStratX = 59;
    private static final int backgroundStratY = 96;

    protected ToolCore tool;

    protected ToolPage(ToolCore toolcore) {
        super(
            TinkersRebornUtils.jsonParser.parse(String.format(pattern, toolcore.toolTypeName, toolcore.toolTypeName))
                .getAsJsonObject());
        this.tool = toolcore;
        this.initToolRecipe();
    }

    @Override
    protected void initRecipe(JsonObject json) {
        // Do nothing XD
    }

    protected void initToolRecipe() {
        List<ToolPartRecord> toolParts = this.tool.getToolComponentsParts();
        ItemStack[][] inputs = new ItemStack[toolParts.size()][];
        for (int idx = 0; idx < toolParts.size(); idx++) {
            ToolPartRecord toolPart = toolParts.get(idx);
            inputs[idx] = new ItemStack[] { toolPart.toolPart()
                .getNewPartWithMaterial("_internal_render" + (idx % 4 + 1)) };
        }
        this.recipes = TinkersRebornRegistry.registerTinkersRebornToolRecipeIcon(
            this.tool.toolTypeName,
            inputs,
            this.tool.getToolForRender(),
            RecipeType.TOOLSTATION);
    }

    @Override
    protected void drawTitleString(int pageX, int pageY) {
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

    @Override
    public void setupTranslate() {
        super.setupTranslate();
        this.translatedText = this.translatedText.replace("\\n", "\\n\\n");
    }

    @Override
    protected void drawTextString(int pageX, int pageY) {
        fontRender.drawSplitString(translatedText, pageX, pageY + 11, 180, 0x000000);
    }

    @Override
    protected void renderToolStationRecipeBackGround(int pageX, int pageY, GuiManual manual) {
        manual.mc.getTextureManager()
            .bindTexture(modifyTexture);
        GL11.glColor4f(manual.backgroundR, manual.backgroundG, manual.backgroundB, 1.0F);
        manual.drawTexturedModalRect(pageX + backgroundStratX, pageY + backgroundStratY, 0, 0, 122, 69);
    }

    @Override
    protected void renderToolStationRecipeItemStack(int pageX, int pageY, GuiManual manual) {
        this.setUpForRenderItem(1.0F);
        ItemStack[][] inputStacks = selectedRecipe.inputStacks;
        ItemStack outputStack = selectedRecipe.outputStack;

        // 59 96
        this.renderItemStackIntoPage(
            outputStack,
            pageX + backgroundStratX + 103,
            pageY + backgroundStratY + 28,
            manual);
        this.renderStacks.add(new RenderStack(outputStack, backgroundStratX + 103, backgroundStratY + 28));

        int idxMax = Math.min(inputStacks.length, inputPostiton.length);
        for (int i = 0; i < idxMax; i++) {
            if (inputStacks[i] != null && inputStacks[i][0] != null) {
                ItemStack renderStack = inputStacks[i][this.counter % inputStacks[i].length];
                int[] postiton = inputPostiton[i];
                this.renderItemStackIntoPage(
                    renderStack,
                    pageX + backgroundStratX + postiton[0],
                    pageY + backgroundStratY + postiton[1],
                    manual);
                this.renderStacks
                    .add(new RenderStack(renderStack, backgroundStratX + postiton[0], backgroundStratY + postiton[1]));
            }
        }

        this.backUpForRenderItem();
    }
}
