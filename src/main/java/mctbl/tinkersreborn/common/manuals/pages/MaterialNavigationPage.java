package mctbl.tinkersreborn.common.manuals.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.common.manuals.TinkersRebornNavigationButton;
import mctbl.tinkersreborn.common.manuals.TinkersRebornNavigationButton.ButtonSize;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class MaterialNavigationPage extends AbstractManualPage {

    protected final List<TinkersRebornNavigationButton> buttons = new ArrayList<>();

    protected final String title;
    protected String translatedTitle;

    protected int buttonEachRow;
    protected ButtonSize buttonSize;

    public MaterialNavigationPage(JsonObject json, List<TinkersRebornMaterial> materials) {
        super(json);
        String buttonSizeStr = json.has("size") ? json.get("size")
            .getAsString() : "medium";
        this.buttonEachRow = json.has("capacity") ? json.get("capacity")
            .getAsInt() : 7;
        this.buttonSize = ButtonSize.getSize(buttonSizeStr);
        this.title = json.has("title") ? json.get("title")
            .getAsString() : "";

        int middleX = contentWidth / 2;
        int middleY = contentHeight / 2;
        int buttonGap = 5;
        int buttonWidth = (int) (TinkersRebornNavigationButton.defaultWidth * buttonSize.getMulti());
        int buttonHeight = (int) (TinkersRebornNavigationButton.defaultHeight * buttonSize.getMulti());
        int buttonRows = TinkersRebornUtils.ceilDiv(materials.size(), this.buttonEachRow);

        int buttonsGroupHeight = buttonRows * buttonHeight + (buttonRows - 1) * buttonGap;
        int buttonsGroupWidth = this.buttonEachRow * buttonWidth + (this.buttonEachRow - 1) * buttonGap;

        int buttonsGroupStartX = middleX - buttonsGroupWidth / 2;
        int buttonsGroupStartY = middleY - buttonsGroupHeight / 2;

        for (int idx = 0; idx < materials.size(); idx++) {
            TinkersRebornMaterial material = materials.get(idx);
            String materialName = material.localizedName();
            ItemStack itemStack = material.getRepresentativeItem();

            TinkersRebornNavigationButton b = new TinkersRebornNavigationButton(
                idx,
                buttonSize,
                new ItemStack[] { itemStack },
                material.identifier,
                materialName,
                material.materialTextColor);

            int row = idx / this.buttonEachRow;
            int column = idx % this.buttonEachRow;

            b.xPosition = buttonsGroupStartX + column * (buttonWidth + buttonGap);
            b.yPosition = buttonsGroupStartY + row * (buttonHeight + buttonGap);

            buttons.add(b);
        }
    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        if (this.translatedTitle != null && !this.translatedTitle.isEmpty())
            this.drawStrCenterAt(ColorUtil.addUnderLine(translatedTitle), pageX + contentWidth / 2, pageY);

        this.buttons.forEach(
            b -> b.drawButton(Minecraft.getMinecraft(), manualMouseX, manualMouseY, manualTicks, pageX, pageY));
    }

    @Override
    public void setupTranslate() {
        this.translatedTitle = TinkersRebornUtils.translate(this.title);
    }

    @Override
    public void drawToolTips(int mouseX, int mouseY, int manualX, int manualY, GuiManual manual) {
        super.drawToolTips(mouseX, mouseY, manualX, manualY, manual);
        this.buttons.forEach(b -> {
            if (b.contains(manualX, manualY)) {
                manual.drawHoveringText(b.getTooltips(), mouseX, mouseY, fontRender);
            }
        });
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton, GuiManual manual) {
        this.buttons.forEach(b -> {
            if (b.contains(mouseX, mouseY)) {
                manual.tryToJumpToPage(b.target);
            }
        });
        super.mouseClicked(mouseX, mouseY, mouseButton, manual);
    }

    public static class MaterialNavigationPageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            List<TinkersRebornMaterial> allMaterials = TinkersRebornRegistry.getAllMaterialList()
                .stream()
                .filter(m -> m.hasStats(MaterialStatusType.HEAD))
                .collect(Collectors.toList());

            List<AbstractManualPage> list = new ArrayList<>();
            list.add(new MaterialNavigationPage(definition.getData(), allMaterials));
            allMaterials.forEach(m -> list.add(new MaterialPage(m)));

            return list;
        }
    }

}
