package mctbl.tinkersreborn.library.manuals;

import com.google.gson.JsonObject;

public abstract class AbstractManualPage {

    protected final String name;

    protected AbstractManualPage(JsonObject json) {
        this.name = json.has("name") ? json.get("name")
            .getAsString() : null;
    }

    public void renderBackgroundLayer() {}

    public abstract void renderContentLayer();

}
