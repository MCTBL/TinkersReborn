package mctbl.tinkersreborn.common.manuals.pages;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TextPage extends AbstractManualPage {

    protected final String title;
    protected String translatedTitle;
    protected final String text;
    protected String translatedText;
    protected final String align;

    public TextPage(JsonObject json) {
        super(json);
        this.text = json.has("text") ? json.get("text")
            .getAsString() : "";
        this.title = json.has("title") ? json.get("title")
            .getAsString() : "";
        this.align = json.has("align") ? json.get("align")
            .getAsString() : "left";
    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks) {
        boolean haveTitle = false;
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
            haveTitle = true;
        }
        fontRender
            .drawSplitString(translatedText, pageX, pageY + (haveTitle ? fontRender.FONT_HEIGHT : 0), 178, 0x000000);
    }

    @Override
    public void setupTranslate() {
        this.translatedTitle = TinkersRebornUtils.translate(this.title);
        this.translatedText = TinkersRebornUtils.translate(this.text);
    }

    public static class TextPageProcessor implements ManualPageProcessor {

        @Override
        public List<AbstractManualPage> process(ManualPageDefinition definition) {
            return Arrays.asList(new TextPage(definition.getData()));
        }

    }
}
