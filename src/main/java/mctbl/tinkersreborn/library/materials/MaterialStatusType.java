package mctbl.tinkersreborn.library.materials;

import mctbl.tinkersreborn.tools.materials.BowMaterialStats;
import mctbl.tinkersreborn.tools.materials.ExtraMaterialStats;
import mctbl.tinkersreborn.tools.materials.FletchingMaterialStats;
import mctbl.tinkersreborn.tools.materials.HandleMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.materials.ShaftMaterialStats;
import mctbl.tinkersreborn.tools.materials.StringMaterialStats;

public enum MaterialStatusType {

    HEAD(HeadMaterialStats.class),
    HANDLE(HandleMaterialStats.class),
    EXTRA(ExtraMaterialStats.class),

    BOW(BowMaterialStats.class),
    STRING(StringMaterialStats.class),
    SHAFT(ShaftMaterialStats.class),
    FLETCHING(FletchingMaterialStats.class);

    private final Class<? extends IMaterialStats> statsClass;

    private MaterialStatusType(Class<? extends IMaterialStats> c) {
        this.statsClass = c;
    }

    private MaterialStatusType() {
        this.statsClass = null;
    }

    public Class<? extends IMaterialStats> getStatusClass() {
        return this.statsClass;
    }

    public boolean hasStatsClass() {
        return this.statsClass != null;
    }
}
