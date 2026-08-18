package mctbl.tinkersreborn.common.manuals.pages;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class CoverPage extends AbstractManualPage {

    protected final String text;
    protected String[] translatedText;

    public CoverPage(JsonObject json) {
        super(json);
        this.text = json.has("text") ? json.get("text")
            .getAsString() : "";
    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks) {
        int cousorX = contentWidth / 2;
        int cousorY = contentHeight * 3 / 7;

        float titleScale = 2.5F;
        GL11.glScalef(titleScale, titleScale, 1.0f);
        this.drawStrCenterAt(translatedText[0], pageX + cousorX, pageY + cousorY, titleScale, 0x000000);
        cousorY += fontRender.FONT_HEIGHT * titleScale;
        GL11.glScalef(1 / titleScale, 1 / titleScale, 1.0f);
        for (int idx = 1; idx < translatedText.length; idx++) {
            this.drawStrCenterAt(translatedText[idx], pageX + cousorX, pageY + cousorY, 1.0F, 0x000000);
            cousorY += fontRender.FONT_HEIGHT;
        }

    }

    @Override
    public void setupTranslate() {
        this.translatedText = TinkersRebornUtils.translate(this.text)
            .split("\\\\n");
    }

    public static class CoverPageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            return Arrays.asList(new CoverPage(definition.getData()));
        }

    }

}
