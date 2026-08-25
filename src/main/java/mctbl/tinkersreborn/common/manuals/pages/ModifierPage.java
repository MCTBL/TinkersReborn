package mctbl.tinkersreborn.common.manuals.pages;

import java.util.List;

import org.lwjgl.opengl.GL11;

import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.tools.modifiers.AbstractModifier;
import mctbl.tinkersreborn.library.tools.modifiers.IModifierDisplay;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ModifierPage extends AbstractManualPage {
    
    protected static final ResourceLocation modifyTexture = new ResourceLocation(
	        "tinkersreborn",
	        "textures/gui/book/modify.png");
    private static final String pattern = "{'name':%s}";
    
    /**
     * width, height, x, y, texX, texY
     */
    private static final int[][] sizeAndPosition = new int[][] {{22, 22, 135, 130, 0, 75}, {40, 22, 126, 130, 0, 97}, {58, 22, 117, 130, 0, 119}, {58, 41, 117, 119, 0, 141}};
    
    private static final int[][][] stackPosition = new int[][][] {{{138, 133}}, {{129, 133}, {147, 133}}, {{120, 133}, {138, 133}, {156, 133}}, {{120, 122}, {138, 122}, {156, 122}, {129, 141}, {147, 141}}};
    
    protected final String title;
    protected String translatedTitle;
    protected final String text;
    protected String translatedText;
    protected final int collor;
    protected final List<List<ItemStack>> inputStacks;
    protected final int stackMaxSize;
    
    protected int counter = 0;
    protected int recipeCounter = 0;
    
    protected ModifierPage(IModifierDisplay modifier) {
	super(TinkersRebornUtils.jsonParser.parse(String.format(pattern, ((AbstractModifier)modifier).identifier))
                .getAsJsonObject());
	
	AbstractModifier castModifier = (AbstractModifier)modifier;
        this.title = String.format("modifier.%s.name", castModifier.identifier);
        this.text = String.format("modifier.%s.desc", castModifier.identifier);
        this.collor = castModifier.getColor();
        this.inputStacks = modifier.getItems();
        this.stackMaxSize = Math.min(this.inputStacks.stream().mapToInt(List::size).max().orElse(1) - 1, 3);
    }
    
    @Override
    public void renderPage(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        if (manualTicks == 19 && ++this.counter > 5) {
            this.recipeCounter ++;
            if (this.recipeCounter >= this.inputStacks.size()) {
                this.recipeCounter = 0;
            }
            this.counter = 0;
        }
        super.renderPage(pageX, pageY, manualMouseX, manualMouseY, partialTicks, manualTicks, manual);
    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
	    int manualTicks, GuiManual manual) {
        this.drawStrCenterAt(
                ColorUtil.addUnderLine(this.translatedTitle),
                pageX + contentWidth / 2,
                pageY,
                this.collor,
                true);
        
        fontRender.drawSplitString(
                this.translatedText,
                pageX,
                pageY + 16,
                180,
                0x000000);
        
        int[][] stackPos = stackPosition[this.stackMaxSize];
        List<ItemStack> stacks = this.inputStacks.get(this.recipeCounter);
        for(int idx = 0; idx < stacks.size(); idx ++) {
            ItemStack renderStack = stacks.get(idx);
            this.renderItemStackIntoPage(
        	    renderStack,
                    pageX + stackPos[idx][0],
                    pageY + stackPos[idx][1],
                    manual);
            this.renderStacks
            .add(new RenderStack(renderStack, stackPos[idx][0], stackPos[idx][1]));
        }
    }
    
    @Override
    public void renderBackgroundLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
            int manualTicks, GuiManual manual) {
        manual.mc.getTextureManager()
        .bindTexture(modifyTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        manual.drawTexturedModalRect(pageX + 125, pageY + 116, 214, 0, 42, 46);
        
        GL11.glColor4f(manual.backgroundR, manual.backgroundG, manual.backgroundB, 1.0F);
        int[] sizeAndPos = sizeAndPosition[this.stackMaxSize];
        manual.drawTexturedModalRect(pageX + sizeAndPos[2], pageY + sizeAndPos[3], sizeAndPos[4], sizeAndPos[5], sizeAndPos[0], sizeAndPos[1]);
    }

    @Override
    public void setupTranslate() {
        this.translatedTitle = TinkersRebornUtils.translate(this.title);
        if (TinkersRebornUtils.canTranslate(this.text)) {
            this.translatedText = TinkersRebornUtils.translate(this.text)
                    .replace("\\n", "\\n\\n");
        }
    }
    
}
