package mctbl.tinkersreborn.common.manuals.pages;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class BaseMaterialPage extends AbstractManualPage {

    protected final String title;
    protected String translatedTitle;
    protected final String text;
    protected String translatedText;
    protected final List<ItemStack> staticStacks;
    protected final List<ItemStack> loopToolStacks;
    protected static final int stackInOnePage = 8;
    protected int toolStackStartIdx = 0;
    protected int counter = 0;

    protected final TinkersRebornMaterial material;

    private static final String pattern = "{'name':%s}";

    public BaseMaterialPage(TinkersRebornMaterial material) {
        super(
            TinkersRebornUtils.jsonParser.parse(String.format(pattern, material.identifier))
                .getAsJsonObject());
        this.title = String.format("material.%s.name", material.identifier);
        this.text = String.format("material.%s.flavour", material.identifier);
        this.material = material;
        this.staticStacks = new ArrayList<>();
        this.staticStacks.add(material.getRepresentativeItem());
        if (material.isCraftable()) {
            this.staticStacks
                .add(TinkersRebornRegistry.getOrRegisterManualIcon("tinkersreborn:tinkersreborn.ToolStation"));
        }
        if (material.isCastable()) {
            this.staticStacks
                .add(TinkersRebornRegistry.getOrRegisterManualIcon("tinkersreborn:tinkersreborn.ToolFroge"));
        }
        this.loopToolStacks = new ArrayList<>();
        for (ToolCore core : TinkersRebornRegistry.getAllTools()) {
            ItemStack tempTool = core.buildTool(material, null);
            if (tempTool != null) {
                TinkersRebornRegistry.getOrRegisterManualIcon(core.toolTypeName + "." + material.identifier, tempTool);
                loopToolStacks.add(tempTool);
            }
        }
    }

    @Override
    public void renderPage(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        if (manualTicks == 19 && ++this.counter >= 5) {
            this.toolStackStartIdx += (stackInOnePage - staticStacks.size());
            if (this.toolStackStartIdx >= this.loopToolStacks.size()) {
                this.toolStackStartIdx = 0;
            }
            this.counter = 0;
        }
        super.renderPage(pageX, pageY, manualMouseX, manualMouseY, partialTicks, manualTicks, manual);
    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        this.drawStrCenterAt(
            ColorUtil.addUnderLine(this.material.localizedName()),
            pageX + contentWidth / 2,
            pageY,
            this.material.materialTextColor,
            true);

        // left 0 right 1
        boolean isLeft = pageX == GuiManual.leftPageStartX;
        int stackStartX = isLeft ? 0 : 164;
        int idx = 0;
        for (ItemStack s : this.staticStacks) {
            this.renderItemStackIntoPage(s, pageX + stackStartX, pageY + 1 + idx * 20, manual);
            this.renderStacks.add(new RenderStack(s, stackStartX, 1 + idx * 20));
            idx++;
        }

        for (ItemStack s : this.loopToolStacks.subList(
            this.toolStackStartIdx,
            Math.min(this.toolStackStartIdx + stackInOnePage - staticStacks.size(), loopToolStacks.size()))) {
            this.renderItemStackIntoPage(s, pageX + stackStartX, pageY + 1 + idx * 20, manual);
            this.renderStacks.add(new RenderStack(s, stackStartX, 1 + idx * 20));
            idx++;
        }
    }

    @Override
    public void setupTranslate() {
        this.translatedTitle = TinkersRebornUtils.translate(this.title);
        if (TinkersRebornUtils.canTranslate(this.text)) {
            this.translatedText = TinkersRebornUtils.translate(this.text);
            this.translatedText = this.translatedText.replace("\\n", "\\n\\n");
        }
    }

}
