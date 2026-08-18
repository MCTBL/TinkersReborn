package mctbl.tinkersreborn.common.manuals.pages;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.library.manuals.AbstractManualPage;
import mctbl.tinkersreborn.library.manuals.ManualPageDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TextPage extends AbstractManualPage {

    protected final String title;
    protected String translatedTitle;
    protected final String text;
    protected String translatedText;

    public TextPage(JsonObject json) {
        super(json);
        this.text = json.has("text") ? json.get("text")
            .getAsString() : "";
        this.title = json.has("title") ? json.get("title")
            .getAsString() : "";
    }

    @Override
    public void renderContentLayer(int pageX, int pageY, int manualMouseX, int manualMouseY, float partialTicks,
        int manualTicks) {
        fontRender.drawSplitString(translatedText, pageX, pageY, 178, 0x000000);
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
