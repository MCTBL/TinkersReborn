package mctbl.tinkersreborn.common.manuals.pages;

import com.google.gson.JsonObject;

import mctbl.tinkersreborn.library.manuals.AbstractManualPage;

public class TextPage extends AbstractManualPage {

    private final String title;
    private final String text;
    private final String align;

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
    public void renderContentLayer() {
        // TODO Auto-generated method stub

    }

}
