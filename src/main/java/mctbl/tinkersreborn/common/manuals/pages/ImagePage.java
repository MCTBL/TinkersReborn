package mctbl.tinkersreborn.common.manuals.pages;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.util.ColorUtil;

public class ImagePage extends TextPage {

    protected final ResourceLocation background;
    protected int imageHeight;
    protected int imageWidth;
    protected float imageScale;

    public ImagePage(JsonObject json) {
        super(json);
        String imgPath = json.has("image") ? json.get("image")
            .getAsString() : "";

        this.background = new ResourceLocation(imgPath);

        try {
            IResource resource = Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(background);
            BufferedImage image = ImageIO.read(resource.getInputStream());
            if (image != null) {
                this.imageHeight = image.getHeight();
                this.imageWidth = image.getWidth();
            }
        } catch (Exception e) {
            this.imageHeight = 170;
            this.imageWidth = 144;
        }
        this.imageScale = Math.min(contentHeight * 1.0F / this.imageHeight, contentWidth * 1.0F / this.imageWidth);
    }

    @Override
    public void renderBackgroundLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        super.renderBackgroundLayer(pageX, pageY, manualMouseX, manualMouseY, partialTicks, manualTicks, manual);
        int drawX = pageX + contentWidth / 2 - (int) (imageWidth * this.imageScale / 2);
        int drawY = pageY + (haveTitle ? fontRender.FONT_HEIGHT : 0);

        GL11.glPushMatrix();
        GL11.glTranslatef(drawX, drawY, 0.0F);
        GL11.glScalef(this.imageScale, this.imageScale, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        manual.mc.getTextureManager()
            .bindTexture(background);
        Gui.func_146110_a(0, 0, 0.0F, 0.0F, imageWidth, imageHeight, imageWidth, imageHeight);
        GL11.glPopMatrix();
    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks, GuiManual manual) {
        if (this.translatedTitle != null && !this.translatedTitle.isEmpty()) {
            String underLineTitle = ColorUtil.addUnderLine(translatedTitle);
            if (this.align.equals("center")) {
                this.drawStrCenterAt(underLineTitle, pageX + contentWidth / 2, pageY);
            } else if (this.align.equals("right")) {
                fontRender.drawString(
                    underLineTitle,
                    pageX + contentWidth - fontRender.getStringWidth(underLineTitle),
                    pageY,
                    0x000000);
            } else {
                fontRender.drawString(underLineTitle, pageX, pageY, 0x000000);
            }
        }
        if (this.translatedText != null) {
            fontRender.drawSplitString(
                this.translatedText,
                pageX,
                pageY + (haveTitle ? fontRender.FONT_HEIGHT : 0) + (int) (this.imageHeight * this.imageScale),
                180,
                0x000000);
        }
    }

    public static class ImagePageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            return Arrays.asList(new ImagePage(definition.getData()));
        }

    }
}
