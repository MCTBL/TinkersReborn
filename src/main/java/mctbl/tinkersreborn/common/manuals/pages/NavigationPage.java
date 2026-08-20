package mctbl.tinkersreborn.common.manuals.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import mctbl.tinkersreborn.common.manuals.TinkersRebornNavigationButton;
import mctbl.tinkersreborn.common.manuals.TinkersRebornNavigationButton.ButtonSize;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class NavigationPage extends AbstractManualPage {

    private final List<TinkersRebornNavigationButton> buttons = new ArrayList<>();

    private int buttonEachRow;
    private ButtonSize buttonSize;
    private String titleKey;
    private String titleStr;

    public NavigationPage(JsonObject json) {
        super(json);
        JsonArray buttonArrays = json.get("buttons")
            .getAsJsonArray();
        String buttonSizeStr = json.has("size") ? json.get("size")
            .getAsString() : "large";
        this.buttonEachRow = json.has("capacity") ? json.get("capacity")
            .getAsInt() : 5;
        this.buttonSize = ButtonSize.getSize(buttonSizeStr);
        this.titleKey = json.has("title") ? json.get("title")
            .getAsString() : null;

        int middleX = contentWidth / 2;
        int middleY = contentHeight / 2;
        int buttonGap = 5;
        int buttonWidth = (int) (TinkersRebornNavigationButton.defaultWidth * buttonSize.getMulti());
        int buttonHeight = (int) (TinkersRebornNavigationButton.defaultHeight * buttonSize.getMulti());
        int buttonRows = ceilDiv(buttonArrays.size(), this.buttonEachRow);

        int buttonsGroupHeight = buttonRows * buttonHeight + (buttonRows - 1) * buttonGap;
        int buttonsGroupWidth = this.buttonEachRow * buttonWidth + (this.buttonEachRow - 1) * buttonGap;

        int buttonsGroupStartX = middleX - buttonsGroupWidth / 2;
        int buttonsGroupStartY = middleY - buttonsGroupHeight / 2;

        for (int idx = 0; idx < buttonArrays.size(); idx++) {
            JsonObject buttonJsonObject = buttonArrays.get(idx)
                .getAsJsonObject();
            String target = buttonJsonObject.has("to") ? buttonJsonObject.get("to")
                .getAsString() : null;
            String iconStr = buttonJsonObject.has("icon") ? buttonJsonObject.get("icon")
                .getAsString() : null;
            String text = buttonJsonObject.has("text") ? buttonJsonObject.get("text")
                .getAsString() : null;
            ItemStack itemStack = TinkersRebornRegistry.getOrRegisterManualIcon(iconStr);

            TinkersRebornNavigationButton b = new TinkersRebornNavigationButton(
                idx,
                buttonSize,
                itemStack,
                text,
                target);

            int row = idx / this.buttonEachRow;
            int column = idx % this.buttonEachRow;

            b.xPosition = buttonsGroupStartX + column * (buttonWidth + buttonGap);
            b.yPosition = buttonsGroupStartY + row * (buttonHeight + buttonGap);

            buttons.add(b);
        }
    }

    private static int ceilDiv(int x, int y) {
        final int q = x / y;
        // if the signs are the same and modulo not zero, round up
        if ((x ^ y) >= 0 && (q * y != x)) {
            return q + 1;
        }
        return q;
    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        if (titleStr != null && !titleStr.isEmpty())
            this.drawStrCenterAt(ColorUtil.addUnderLine(titleStr), pageX + contentWidth / 2, pageY + 4, 1.0f, 0x000000);

        this.buttons.forEach(
            b -> b.drawButton(Minecraft.getMinecraft(), manualMouseX, manualMouseY, manualTicks, pageX, pageY));
    }

    @Override
    public void setupTranslate() {
        this.titleStr = TinkersRebornUtils.translate(this.titleKey);
    }

    public static class NavigationPageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            return Arrays.asList(new NavigationPage(definition.getData()));
        }

    }

}
