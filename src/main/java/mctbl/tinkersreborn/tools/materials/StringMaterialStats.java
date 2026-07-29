package mctbl.tinkersreborn.tools.materials;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.library.materials.IMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class StringMaterialStats extends AbstractMaterialStats {

    public final static String LOC_Multiplier = "tinkersreborn.stat.string.modifier.name";

    public final static String LOC_MultiplierDesc = "tinkersreborn.stat.string.modifier.desc";

    private float modifier; // around 1.0

    /**
     * @param modifier
     */
    public StringMaterialStats(float modifier) {
        this.modifier = modifier;
    }

    public float getModifier() {
        return modifier;
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

    @Override
    public void writeToCfg(Configuration cfg, String categotry) {
        String key = categotry + "."
            + this.getIdentifier()
                .toString()
                .toLowerCase();
        cfg.get(key, "modifier", this.modifier)
            .getDouble();
    }

    @Override
    public IMaterialStats readFromCfg(Configuration cfg, String categotry) {
        String key = categotry + "."
            + this.getIdentifier()
                .toString()
                .toLowerCase();
        this.modifier = (float) cfg.get(key, "modifier", this.modifier)
            .getDouble();
        return this;
    }

    @Override
    public IMaterialStats getNewStatsFromCfg(Configuration cfg, String categotry) {
        return new StringMaterialStats(0).readFromCfg(cfg, categotry);
    }
}
