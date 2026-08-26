package mctbl.tinkersreborn.tools.materials;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.library.materials.IMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class FletchingMaterialStats extends AbstractMaterialStats {

    public final static String LOC_Accuracy = "tinkersreborn.stat.fletching.accuracy.name";
    public final static String LOC_Multiplier = "tinkersreborn.stat.fletching.modifier.name";

    public final static String LOC_AccuracyDesc = "tinkersreborn.stat.fletching.accuracy.desc";
    public final static String LOC_MultiplierDesc = "tinkersreborn.stat.fletching.modifier.desc";

    private float modifier;
    private float accuracy;

    /**
     * @param modifier
     * @param accuracy
     */
    public FletchingMaterialStats(float accuracy, float modifier) {
        this.modifier = modifier;
        this.accuracy = accuracy;
    }

    public float getModifier() {
        return modifier;
    }

    public float getAccuracy() {
        return accuracy;
    }

    @Override
    public MaterialStatusType getIdentifier() {
        return MaterialStatusType.FLETCHING;
    }

    @Override
    public String getLocalizedName() {
        return TinkersStr.fletchingStatsName.toString();
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = new ArrayList<>();

        if (modifier != 0) info.add(formatModifier(this.modifier));
        if (accuracy != 0) info.add(formatAccuracy(this.accuracy));

        return info;
    }

    @Override
    public List<String> getLocalizedDesc() {
        List<String> info = new ArrayList<>();

        if (modifier != 0) info.add(TinkersRebornUtils.translate(LOC_MultiplierDesc));
        if (accuracy != 0) info.add(TinkersRebornUtils.translate(LOC_AccuracyDesc));

        return info;
    }

    public static String formatModifier(float modifier) {
        return format(LOC_Multiplier, COLOR_Multiplier, modifier);
    }

    public static String formatAccuracy(float accuracy) {
        return formatNumberPercent(LOC_Accuracy, COLOR_Accuracy, accuracy);
    }

    @Override
    public void writeToCfg(Configuration cfg, String categotry) {
        String key = categotry + "."
            + this.getIdentifier()
                .toString()
                .toLowerCase();
        cfg.get(key, "modifier", this.modifier)
            .getDouble();
        cfg.get(key, "accuracy", this.accuracy)
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
        this.accuracy = (float) cfg.get(key, "accuracy", this.accuracy)
            .getDouble();
        return this;
    }

    @Override
    public IMaterialStats getNewStatsFromCfg(Configuration cfg, String categotry) {
        return new FletchingMaterialStats(0, 0).readFromCfg(cfg, categotry);
    }

}
