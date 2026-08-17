package mctbl.tinkersreborn.library.manuals;

import com.google.gson.JsonObject;

public class ManualPageDefinition {

    private final String type;
    private final String name;
    private final String title;
    private final JsonObject data;

    public ManualPageDefinition(JsonObject data) {
        this.type = data.has("type") ? data.get("type")
            .getAsString() : "";
        this.name = data.has("name") ? data.get("name")
            .getAsString() : "";
        this.title = data.has("title") ? data.get("title")
            .getAsString() : "";
        this.data = data;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public JsonObject getData() {
        return this.data;
    }

}
