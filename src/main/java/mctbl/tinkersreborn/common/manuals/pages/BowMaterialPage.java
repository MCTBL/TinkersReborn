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
import mctbl.tinkersreborn.library.tools.traits.AbstractTraitLeveled;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class BowMaterialPage extends AbstractManualPage {

    protected final String title;
    protected String translatedTitle;
    protected final List<ItemStack> staticStacks;
    protected final List<ItemStack> loopToolStacks;
    protected static final int stackInOnePage = 8;
    protected int toolStackStartIdx = 0;
    protected int counter = 0;

    protected final TinkersRebornMaterial material;

    private static final String pattern = "{'name':bow%s}";

    private static final MaterialStatusType[] StatsTypeOrder = new MaterialStatusType[] { MaterialStatusType.BOW,
        MaterialStatusType.SHAFT, MaterialStatusType.FLETCHING, MaterialStatusType.STRING };

    protected BowMaterialPage(TinkersRebornMaterial material) {
        super(
            TinkersRebornUtils.jsonParser.parse(String.format(pattern, material.identifier))
                .getAsJsonObject());
        this.title = String.format("material.%s.name", material.identifier);
        this.material = material;
        this.staticStacks = new ArrayList<>();
        this.staticStacks.add(material.getRepresentativeItem());

        if (material.isCraftable()) {
            this.staticStacks
                .add(TinkersRebornRegistry.getOrRegisterManualIcon("tinkersreborn:tinkersreborn.PartBuilder"));
        }
        if (material.isCastable()) {
            this.staticStacks
                .add(TinkersRebornRegistry.getOrRegisterManualIcon("tinkersreborn:tile.tinkersreborn.SearedBlock:2"));
        }
        this.loopToolStacks = new ArrayList<>();
        for (TinkersRebornToolPart part : TinkersRebornRegistry.getAllToolParts()) {
            ItemStack newPart = part.getNewPartWithMaterial(material);
            if (newPart != null) {
                TinkersRebornRegistry.getOrRegisterManualIcon(part.partName + "." + material.identifier, newPart);
                loopToolStacks.add(newPart);
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
    public void setupTranslate() {
        this.translatedTitle = TinkersRebornUtils.translate(this.title);
    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        this.drawStrCenterAt(
            ColorUtil.addUnderLine(translatedTitle),
            pageX + contentWidth / 2,
            pageY,
            this.material.materialTextColor,
            true);

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

        int statsIndex = 0;
        int textStartX = isLeft ? 28 : 12;
        int textStartY = 15;

        for (int i = 0; i < 2 && statsIndex < StatsTypeOrder.length; i++) {
            MaterialStatusType statusType = null;
            IMaterialStats stats = null;
            Collection<ITrait> allTraitsForStats = null;
            do {
                statusType = StatsTypeOrder[statsIndex++];
                // head and handle
                stats = this.material.getStats(statusType);
            } while (stats == null && statsIndex < StatsTypeOrder.length);
            if (stats != null) {
                allTraitsForStats = this.material.getAllTraitsForStats(statusType);
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

        for (int i = 0; i < 2 && statsIndex < StatsTypeOrder.length; i++) {
            MaterialStatusType statusType = null;
            IMaterialStats stats = null;
            Collection<ITrait> allTraitsForStats = null;
            do {
                statusType = StatsTypeOrder[statsIndex++];
                // head and handle
                stats = this.material.getStats(statusType);
            } while (stats == null && statsIndex < StatsTypeOrder.length);
            if (stats != null) {
                allTraitsForStats = this.material.getAllTraitsForStats(statusType);
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
    }

}
