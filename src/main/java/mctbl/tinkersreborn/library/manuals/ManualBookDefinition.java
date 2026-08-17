package mctbl.tinkersreborn.library.manuals;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import mctbl.tinkersreborn.util.ColorUtil;

public class ManualBookDefinition {

    private final String title;
    private final String tooltip;
    private final int color;
    private final List<ManualPageDefinition> pages;

    public ManualBookDefinition(JsonObject data) {
        this.title = data.has("title") ? data.get("title")
            .getAsString() : "";
        this.tooltip = data.has("tooltip") ? data.get("tooltip")
            .getAsString() : "";
        this.color = data.has("color") ? ColorUtil.fromHexString(
            data.get("color")
                .getAsString())
            : 0xFFFFFF;
        this.pages = this.loadPages(
            data.get("pages")
                .getAsJsonArray());
    }

    private List<ManualPageDefinition> loadPages(JsonArray arrays) {
        List<ManualPageDefinition> pageList = new ArrayList<>();
        arrays.forEach(p -> pageList.add(new ManualPageDefinition(p.getAsJsonObject())));
        return ImmutableList.copyOf(pageList);
    }

    public String getTitle() {
        return this.title;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public int getColor() {
        return this.color;
    }

    public List<ManualPageDefinition> getPages() {
        return this.pages;
    }

}
