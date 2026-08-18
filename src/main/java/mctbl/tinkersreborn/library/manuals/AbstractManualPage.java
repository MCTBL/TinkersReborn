package mctbl.tinkersreborn.library.manuals;

import net.minecraft.client.gui.FontRenderer;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.common.TinkersRebornGeneralProxyClient;

public abstract class AbstractManualPage {

    public static FontRenderer fontRender = TinkersRebornGeneralProxyClient.manualFontRender;

    public static final int contentWidth = 180;
    public static final int contentHeight = 165;

    protected final String name;

    protected AbstractManualPage(JsonObject json) {
        this.name = json.has("name") ? json.get("name")
            .getAsString() : null;
    }

    public void renderPage(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks) {
        this.renderBackgroundLayer(pageX, pageY, manualMouseX, manualMouseY, partialTicks, manualTicks);
        this.renderContentLayer(pageX, pageY, manualMouseX, manualMouseY, partialTicks, manualTicks);
    }

    public void renderBackgroundLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks) {}

    public abstract void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY,
        float partialTicks, int manualTicks);

    public abstract void setupTranslate();

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}

    protected void drawStrCenterAt(String str, int x, int y, float scale, int color) {
        fontRender.drawString(
            str,
            (int) (x / scale - fontRender.getStringWidth(str) * 1.0F / 2),
            (int) ((y - fontRender.FONT_HEIGHT * 1.0F / 2) / scale),
            color);
    }
}
