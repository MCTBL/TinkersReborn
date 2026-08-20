package mctbl.tinkersreborn.common.manuals;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import mctbl.tinkersreborn.library.gui.GuiManualButton;

public class TinkersRebornTurnPageButton extends GuiManualButton {

    public enum ButtonType {

        nextPage(412, 0, 18, 10, "nextPage"),
        previousPage(412, 10, 18, 10, "previousPage"),
        backToJumpFrom(412, 20, 18, 10, "backToJumpFrom"),
        homePage(412, 40, 14, 17, "homePage");

        int textureX;
        int textureY;
        int textureWidth;
        int textureHeight;
        List<String> tooltips;

        ButtonType(int textureX, int textureY, int textureWidth, int textureHeight, String tooltips) {
            this.textureX = textureX;
            this.textureY = textureY;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.tooltips = Arrays
                .asList(StatCollector.translateToLocal("tinkersreborn.manuals.button.tooltip." + tooltips));
        }

        public int getTextureX() {
            return textureX;
        }

        public int getTextureY() {
            return textureY;
        }

        public int getTextureWidth() {
            return textureWidth;
        }

        public int getTextureHeight() {
            return textureHeight;
        }

        public List<String> getTooltips() {
            return tooltips;
        }

    }

    public static int ARROWCOLOR = 0xFFFFD3;
    public static int ARROWCOLORHOVER = 0xFF541C;

    private final ButtonType buttonType;
    private static final ResourceLocation background = new ResourceLocation(
        "tinkersreborn",
        "textures/gui/book/book.png");

    public TinkersRebornTurnPageButton(int id, int xPosition, int yPosition, ButtonType buttonType) {
        super(id, xPosition, yPosition, buttonType.textureWidth, buttonType.textureHeight, "");
        this.buttonType = buttonType;
    }

    public int getWidth() {
        return this.buttonType.textureWidth;
    }

    public int getHeight() {
        return this.buttonType.textureHeight;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        this.drawButton(mc, mouseX, mouseY, 0, 0);
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, int pageX, int pageY) {
        if (this.visible) {
            boolean isMouseInButton = this.isHover(mouseX, mouseY);

            mc.getTextureManager()
                .bindTexture(background);

            if (isMouseInButton) {
                GL11.glColor4f(255f / 255, 84f / 255, 28f / 255, 1.0F);
            } else {
                GL11.glColor4f(255f / 255, 255f / 255, 221f / 255, 1.0F);
            }

            func_146110_a(
                this.xPosition + pageX,
                this.yPosition + pageY,
                this.buttonType.textureX,
                this.buttonType.textureY,
                this.buttonType.textureWidth,
                this.buttonType.textureHeight,
                512.0F,
                512.0F);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public List<String> getTooltips() {
        return this.buttonType.tooltips;
    }
}
