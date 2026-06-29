package mctbl.tinkersreborn.tools.materials;

import java.util.ArrayList;
import java.util.List;

import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class StringMaterialStats extends AbstractMaterialStats {

    public final static String LOC_Multiplier = "tinkersreborn.stat.string.modifier.name";

    public final static String LOC_MultiplierDesc = "tinkersreborn.stat.string.modifier.desc";

    public final float modifier; // around 1.0

    /**
     * @param modifier
     */
    public StringMaterialStats(float modifier) {
        this.modifier = modifier;
    }

    @Override
    public MaterialStatusType getIdentifier() {
        return MaterialStatusType.STRING;
    }

    @Override
    public String getLocalizedName() {
        return TinkersStr.stringStatsName.toString();
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = new ArrayList<>();

        if (modifier != 0) info.add(formatModifier(this.modifier));

        return info;
    }

    @Override
    public List<String> getLocalizedDesc() {
        List<String> info = new ArrayList<>();

        if (modifier != 0) info.add(TinkersRebornUtils.translate(LOC_MultiplierDesc));

        return info;
    }

    public static String formatModifier(float modifier) {
        return format(LOC_Multiplier, COLOR_Modifier, modifier);
    }

}
