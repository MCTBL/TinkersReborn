package mctbl.tinkersreborn.tools.materials;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.library.materials.IMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class HandleMaterialStats extends AbstractMaterialStats {

    public final static String LOC_Multiplier = "tinkersreborn.stat.handle.modifier.name";
    public final static String LOC_Durability = "tinkersreborn.stat.handle.durability.name";

    public final static String LOC_MultiplierDesc = "tinkersreborn.stat.handle.modifier.desc";
    public final static String LOC_DurabilityDesc = "tinkersreborn.stat.handle.durability.desc";

    private float multiplier; // how good the material is for handles. 0.0 - 1.0
    private int durability; // usually between -500 and 500

    /**
     * @param multiplier
     * @param durability
     */
    public HandleMaterialStats(float modifier, int durability) {
        this.multiplier = modifier;
        this.durability = durability;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public int getDurability() {
        return durability;
    }

    @Override
    public MaterialStatusType getIdentifier() {
        return MaterialStatusType.HANDLE;
    }

    @Override
    public String getLocalizedName() {
        return TinkersStr.handleStatsName.toString();
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = new ArrayList<>();

        if (this.multiplier != 0) info.add(formatMultiplier(this.multiplier));
        if (this.durability != 0) info.add(formatDurability(this.durability));

        return info;
    }

    @Override
    public List<String> getLocalizedDesc() {
        List<String> info = new ArrayList<>();

        if (this.multiplier != 0) info.add(TinkersRebornUtils.translate(LOC_MultiplierDesc));
        if (this.durability != 0) info.add(TinkersRebornUtils.translate(LOC_DurabilityDesc));

        return info;
    }

    public static String formatMultiplier(float multiplier) {
        return format(LOC_Multiplier, COLOR_Multiplier, multiplier);
    }

    public static String formatDurability(int durability) {
        return format(LOC_Durability, COLOR_Durability, durability);
    }

    @Override
    public void writeToCfg(Configuration cfg, String categotry) {
        String key = categotry + "."
            + this.getIdentifier()
                .toString()
                .toLowerCase();
        cfg.get(key, "multiplier", this.multiplier)
            .getDouble();
        cfg.get(key, "durability", this.durability)
            .getInt();
    }

    @Override
    public IMaterialStats readFromCfg(Configuration cfg, String categotry) {
        String key = categotry + "."
            + this.getIdentifier()
                .toString()
                .toLowerCase();
        this.multiplier = (float) cfg.get(key, "multiplier", this.multiplier)
            .getDouble();
        this.durability = cfg.get(key, "durability", this.durability)
            .getInt();
        return this;
    }

    @Override
    public IMaterialStats getNewStatsFromCfg(Configuration cfg, String categotry) {
        return new HandleMaterialStats(0, 0).readFromCfg(cfg, categotry);
    }
}
