package mctbl.tinkersreborn.library.manuals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.manuals.TinkersRebornManualDataBase;

public class ManualBookData {

    private ManualBookDefinition definition;
    private Map<String, Integer> indexMap;
    private List<AbstractManualPage> pages;

    public ManualBookData(ManualBookDefinition definition) {
        this.definition = definition;
    }

    public void processBook() {
        List<ManualPageDefinition> pageDefinitions = this.definition.getPages();
        this.pages = new ArrayList<>();
        for (ManualPageDefinition def : pageDefinitions) {
            String pageType = def.getType();
            if (TinkersRebornManualDataBase.getPageProcessor()
                .containsKey(pageType)) {
                this.pages.addAll(
                    TinkersRebornManualDataBase.getPageProcessor()
                        .get(pageType)
                        .process(def));
            } else {
                TinkersReborn.LOG.error("There's not page processor for type {}", pageType);
            }
        }
        for (int i = 0; i < this.pages.size(); i++) {
            AbstractManualPage p = this.pages.get(i);
            if (p.name != null && !p.name.isEmpty()) {
                indexMap.put(p.name, i);
            }
        }
    }

    public ManualBookDefinition getDefinition() {
        return definition;
    }

    public Map<String, Integer> getIndexMap() {
        return indexMap;
    }

    public List<AbstractManualPage> getPages() {
        return pages;
    }

}
