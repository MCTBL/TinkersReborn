package mctbl.tinkersreborn.library.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import mctbl.tinkersreborn.common.TinkersRebornGeneralProxyClient;

public abstract class GuiManualButton extends GuiButton {

    public static FontRenderer fontRender = TinkersRebornGeneralProxyClient.manualFontRender;

    public List<String> toolTips;
    public boolean renderTips = true;

    protected GuiManualButton(int id, int xPosition, int yPosition, int width, int height, String displayString) {
        super(id, xPosition, yPosition, width, height, displayString);
    }

    public boolean isHover(int mouseX, int mouseY) {
        return mouseX >= this.xPosition && mouseY >= this.yPosition
            && mouseX < this.xPosition + this.width
            && mouseY < this.yPosition + this.height;
    }

    public boolean contains(int mouseX, int mouseY) {
        return this.isHover(mouseX, mouseY) && this.visible;
    }

    public List<String> getTooltips() {
        return this.toolTips;
    }

    public boolean needRenderTips() {
        return this.renderTips;
    }
}
