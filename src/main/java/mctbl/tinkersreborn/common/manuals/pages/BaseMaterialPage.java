package mctbl.tinkersreborn.common.manuals.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.materials.IMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ITrait;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.traits.AbstractTraitLeveled;
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

    private static final MaterialStatusType[] StatsTypeOrder = new MaterialStatusType[] { MaterialStatusType.HEAD,
        MaterialStatusType.HANDLE, MaterialStatusType.EXTRA };

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
            this.renderItemStackIntoPage(s, pageX + stackStartX, pageY + idx * 20, manual);
            this.renderStacks.add(new RenderStack(s, stackStartX, idx * 20));
            idx++;
        }

        for (ItemStack s : this.loopToolStacks.subList(
            this.toolStackStartIdx,
            Math.min(this.toolStackStartIdx + stackInOnePage - staticStacks.size(), loopToolStacks.size()))) {
            this.renderItemStackIntoPage(s, pageX + stackStartX, pageY + idx * 20, manual);
            this.renderStacks.add(new RenderStack(s, stackStartX, idx * 20));
            idx++;
        }

        int textStartX = isLeft ? 28 : 12;
        int textStartY = 15;
        for (int i = 0; i < 2; i++) {
            if (i == 1 && this.translatedText != null && !this.translatedText.isEmpty()) {
                fontRender
                    .drawSplitString(this.translatedText, pageX + textStartX + 70, pageY + textStartY, 70, 0x000000);
            }

            MaterialStatusType statusType = StatsTypeOrder[i];
            // head and handle
            IMaterialStats stats = this.material.getStats(statusType);
            Collection<ITrait> allTraitsForStats = this.material.getAllTraitsForStats(statusType, true);
            if (stats != null) {
                fontRender.drawString(
                    ColorUtil.addUnderLine(stats.getLocalizedName()),
                    pageX + textStartX,
                    pageY + textStartY,
                    0x000000);
                textStartY += fontRender.FONT_HEIGHT;
                for (String line : stats.getLocalizedInfo()) {
                    fontRender.drawString(line, pageX + textStartX, pageY + textStartY, 0x000000);
                    textStartY += fontRender.FONT_HEIGHT;
                }
                textStartY += fontRender.FONT_HEIGHT;

                for (ITrait t : allTraitsForStats) {
                    String traitName = t.getLocalizedName();
                    if (t instanceof AbstractTraitLeveled leveled) {
                        traitName = traitName + " " + TinkersRebornUtils.getRomanNumeral(leveled.getLevels());
                    }
                    fontRender.drawString(
                        ColorUtil.addUnderLine(traitName),
                        pageX + textStartX,
                        pageY + textStartY,
                        t.getColor(),
                        true);
                    String traitColor = ColorUtil.encodeColor(t.getColor());
                    this.renderString.add(
                        new RenderString(
                            textStartX,
                            textStartY,
                            70,
                            fontRender.FONT_HEIGHT,
                            Arrays.asList(
                                t.getLocalizedDesc()
                                    .split("\\\\n"))
                                .stream()
                                .map(s -> traitColor + s)
                                .collect(Collectors.toList())));
                    textStartY += fontRender.FONT_HEIGHT;
                }
                textStartY += fontRender.FONT_HEIGHT;
            }
        }

        textStartX += 85;
        textStartY = 15;
        // extra and extra text
        MaterialStatusType statusType = StatsTypeOrder[2];
        // head and handle
        IMaterialStats stats = this.material.getStats(statusType);
        Collection<ITrait> allTraitsForStats = this.material.getAllTraitsForStats(statusType, true);
        if (stats != null) {
            fontRender.drawString(
                ColorUtil.addUnderLine(stats.getLocalizedName()),
                pageX + textStartX,
                pageY + textStartY,
                0x000000);
            textStartY += fontRender.FONT_HEIGHT;
            for (String line : stats.getLocalizedInfo()) {
                fontRender.drawString(line, pageX + textStartX, pageY + textStartY, 0x000000);
                textStartY += fontRender.FONT_HEIGHT;
            }
            textStartY += fontRender.FONT_HEIGHT;

            for (ITrait t : allTraitsForStats) {
                String traitName = t.getLocalizedName();
                if (t instanceof AbstractTraitLeveled leveled) {
                    traitName = traitName + " " + TinkersRebornUtils.getRomanNumeral(leveled.getLevels());
                }

                fontRender.drawString(
                    ColorUtil.addUnderLine(traitName),
                    pageX + textStartX,
                    pageY + textStartY,
                    t.getColor(),
                    true);
                String traitColor = ColorUtil.encodeColor(t.getColor());
                this.renderString.add(
                    new RenderString(
                        textStartX,
                        textStartY,
                        70,
                        fontRender.FONT_HEIGHT,
                        Arrays.asList(
                            t.getLocalizedDesc()
                                .split("\\\\n"))
                            .stream()
                            .map(s -> traitColor + s)
                            .collect(Collectors.toList())));
                textStartY += fontRender.FONT_HEIGHT;
            }
        }

    }

    @Override
    public void setupTranslate() {
        this.translatedTitle = TinkersRebornUtils.translate(this.title);
        if (TinkersRebornUtils.canTranslate(this.text)) {
            this.translatedText = ColorUtil.addItalic(
                "\"" + TinkersRebornUtils.translate(this.text)
                    .replace("\\n", "\\n\\n") + "\"");
        }
    }

}
