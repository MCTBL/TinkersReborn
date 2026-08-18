package mctbl.tinkersreborn.common.manuals;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.manuals.ManualBookData;
import mctbl.tinkersreborn.library.manuals.ManualBookDefinition;
import mctbl.tinkersreborn.library.manuals.ManualPageProcessor;

public class TinkersRebornManualDataBase {

    protected static final Map<String, ManualPageProcessor> pageProcessors = new HashMap<>();
    protected static final Map<String, ManualBookData> books = new HashMap<>();

    private TinkersRebornManualDataBase() {}

    public static void loadManuals() {
        JsonParser parser = new JsonParser();
        for (String name : TinkersRebornConfig.manualNames) {
            try {
                InputStreamReader stream = new InputStreamReader(
                    TinkersReborn.class.getResourceAsStream("/assets/tinkersreborn/manuals/" + name + ".json"));
                JsonObject jsObj = parser.parse(stream)
                    .getAsJsonObject();
                ManualBookData bookData = new ManualBookData(new ManualBookDefinition(jsObj));
                books.put(name, bookData);
            } catch (Exception e) {
                TinkersReborn.LOG.error("Get some error when loading {}", name);
                TinkersReborn.LOG.error(e.getLocalizedMessage());
            }
        }
    }

    public static void processManuals() {
        books.values()
            .forEach(ManualBookData::processBook);
    }

    public static void registerPageProcessor(String type, ManualPageProcessor processor) {
        pageProcessors.put(type, processor);
    }

    public static Map<String, ManualPageProcessor> getPageProcessor() {
        return pageProcessors;
    }

    public static Map<String, ManualBookData> getBooks() {
        return books;
    }

}
