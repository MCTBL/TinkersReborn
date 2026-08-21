package mctbl.tinkersreborn.library.manuals;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.common.TinkersRebornGeneralProxyClient;
import mctbl.tinkersreborn.library.gui.GuiManual;

public abstract class AbstractManualPage {

    public static class RenderStack {

        private final ItemStack stack;
        private final int x;
        private final int y;

        public RenderStack(ItemStack stack, int x, int y) {
            this.stack = stack;
            this.x = x;
            this.y = y;
        }

        public ItemStack getStack() {
            return stack;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

    }

    public static FontRenderer fontRender = TinkersRebornGeneralProxyClient.manualFontRender;

    public static final int contentWidth = 180;
    public static final int contentHeight = 165;

    protected final String name;
    protected final List<RenderStack> renderStacks = new ArrayList<>();

    protected AbstractManualPage(JsonObject json) {
        this.name = json.has("name") ? json.get("name")
            .getAsString() : null;
    }

    public void renderPage(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        this.renderStacks.clear();
        this.renderBackgroundLayer(pageX, pageY, manualMouseX, manualMouseY, partialTicks, manualTicks, manual);
        this.renderContentLayer(pageX, pageY, manualMouseX, manualMouseY, partialTicks, manualTicks, manual);
    }

    public void renderBackgroundLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {}

    public abstract void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY,
        float partialTicks, int manualTicks, GuiManual manual);

    public abstract void setupTranslate();

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}

    public void drawToolTips(int mouseX, int mouseY, int manualX, int manualY, GuiManual manual) {}

    protected void drawStrCenterAt(String str, int x, int y) {
        this.drawStrCenterAt(str, x, y, 1.0F, 0x000000);
    }

    protected void drawStrCenterAt(String str, int x, int y, int color) {
        this.drawStrCenterAt(str, x, y, 1.0F, color);
    }

    protected void drawStrCenterAt(String str, int x, int y, float scale) {
        this.drawStrCenterAt(str, x, y, scale, 0x000000);
    }

    protected void drawStrCenterAt(String str, int x, int y, float scale, int color) {
        fontRender.drawString(
            str,
            (int) (x / scale - fontRender.getStringWidth(str) * 1.0F / 2),
            (int) ((y - fontRender.FONT_HEIGHT * 1.0F / 2) / scale),
            color);
    }

    protected void setUpForRenderItem() {
        this.setUpForRenderItem(2.0F);
    }

    protected void setUpForRenderItem(float scale) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glScalef(scale, scale, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
    }

    protected void backUpForRenderItem() {
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    protected void renderItemStackIntoPage(ItemStack stack, int x, int y, GuiManual manual) {
        FontRenderer fontRenderer = fontRender;
        TextureManager renderEngine = manual.mc.getTextureManager();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        manual.renderItem.zLevel = 100;
        manual.renderItem.renderItemAndEffectIntoGUI(fontRenderer, renderEngine, stack, x, y);
        if (stack.stackSize > 1) manual.renderItem
            .renderItemOverlayIntoGUI(fontRenderer, renderEngine, stack, x, y, String.valueOf(stack.stackSize));
        manual.renderItem.zLevel = 0;
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
