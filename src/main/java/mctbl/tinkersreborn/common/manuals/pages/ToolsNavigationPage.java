package mctbl.tinkersreborn.common.manuals.pages;

import java.util.ArrayList;
import java.util.List;

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
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class ToolsNavigationPage extends AbstractManualPage {

    protected final List<TinkersRebornNavigationButton> buttons = new ArrayList<>();

    protected final String title;
    protected String translatedTitle;

    protected int buttonEachRow;
    protected ButtonSize buttonSize;

    public ToolsNavigationPage(JsonObject json) {
        super(json);
        String buttonSizeStr = json.has("size") ? json.get("size")
            .getAsString() : "medium";
        this.buttonEachRow = json.has("capacity") ? json.get("capacity")
            .getAsInt() : 7;
        this.buttonSize = ButtonSize.getSize(buttonSizeStr);
        this.title = json.has("title") ? json.get("title")
            .getAsString() : "";

        List<ToolCore> allTools = TinkersRebornRegistry.getAllTools();
        int middleX = contentWidth / 2;
        int middleY = contentHeight / 2;
        int buttonGap = 5;
        int buttonWidth = (int) (TinkersRebornNavigationButton.defaultWidth * buttonSize.getMulti());
        int buttonHeight = (int) (TinkersRebornNavigationButton.defaultHeight * buttonSize.getMulti());
        int buttonRows = TinkersRebornUtils.ceilDiv(allTools.size(), this.buttonEachRow);

        int buttonsGroupHeight = buttonRows * buttonHeight + (buttonRows - 1) * buttonGap;
        int buttonsGroupWidth = this.buttonEachRow * buttonWidth + (this.buttonEachRow - 1) * buttonGap;

        int buttonsGroupStartX = middleX - buttonsGroupWidth / 2;
        int buttonsGroupStartY = middleY - buttonsGroupHeight / 2;

        for (int idx = 0; idx < allTools.size(); idx++) {
            ToolCore toolcore = allTools.get(idx);
            String toolTypeName = toolcore.toolTypeName;
            ItemStack itemStack = TinkersRebornRegistry
                .getOrRegisterManualIcon(toolTypeName, toolcore.getToolForRender());

            TinkersRebornNavigationButton b = new TinkersRebornNavigationButton(
                idx,
                buttonSize,
                itemStack,
                toolTypeName);

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

    public static class ToolsNavigationPageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            List<AbstractManualPage> list = new ArrayList<>();

            list.add(new ToolsNavigationPage(definition.getData()));
            TinkersRebornRegistry.getAllTools()
                .forEach(t -> list.add(new ToolPage(t)));

            return list;
        }

    }

}
