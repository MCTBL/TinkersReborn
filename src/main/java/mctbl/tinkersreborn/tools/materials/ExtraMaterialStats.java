package mctbl.tinkersreborn.tools.materials;

import java.util.ArrayList;
import java.util.List;

import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class ExtraMaterialStats extends AbstractMaterialStats {

    public final static String LOC_Durability = "tinkersreborn.stat.extra.durability.name";
    public final static String LOC_DurabilityDesc = "tinkersreborn.stat.extra.durability.desc";

    public final static String formatBase = "%s: <#%s>%s</#>";

    public final int extraDurability; // usually between 0 and 500

    /**
     * @param extraDurability
     */
    public ExtraMaterialStats(int extraDurability) {
        this.extraDurability = extraDurability;
    }

    @Override
    public MaterialStatusType getIdentifier() {
        return MaterialStatusType.EXTRA;
    }

    @Override
    public String getLocalizedName() {
        return TinkersStr.extraStatsName.toString();
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = new ArrayList<>();

        if (this.extraDurability != 0) info.add(formatDurability(this.extraDurability));

        return info;
    }

    @Override
    public List<String> getLocalizedDesc() {
        List<String> info = new ArrayList<>();

        if (this.extraDurability != 0) info.add(TinkersRebornUtils.translate(LOC_DurabilityDesc));

        return info;
    }

    public static String formatDurability(int extraDurability) {
        return format(LOC_Durability, COLOR_Durability, extraDurability);
    }

}
