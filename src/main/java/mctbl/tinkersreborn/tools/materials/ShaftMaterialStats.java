package mctbl.tinkersreborn.tools.materials;

import java.util.ArrayList;
import java.util.List;

import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class ShaftMaterialStats extends AbstractMaterialStats {

    public final static String LOC_Multiplier = "tinkersreborn.stat.shaft.modifier.name";
    public final static String LOC_Ammo = "tinkersreborn.stat.shaft.ammo.name";

    public final static String LOC_MultiplierDesc = "tinkersreborn.stat.shaft.modifier.desc";
    public final static String LOC_AmmoDesc = "tinkersreborn.stat.shaft.ammo.desc";

    public final static String COLOR_Ammo = COLOR_Durability;

    public final float modifier;
    public final int bonusAmmo;

    /**
     * @param modifier
     * @param bonusAmmo
     */
    public ShaftMaterialStats(float modifier, int bonusAmmo) {
        this.modifier = modifier;
        this.bonusAmmo = bonusAmmo;
    }

    @Override
    public MaterialStatusType getIdentifier() {
        return MaterialStatusType.SHAFT;
    }

    @Override
    public String getLocalizedName() {
        return TinkersStr.shaftStatsName.toString();
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = new ArrayList<>();

        if (modifier != 0) info.add(formatModifier(this.modifier));
        if (bonusAmmo != 0) info.add(formatAmmo(this.bonusAmmo));

        return info;
    }

    @Override
    public List<String> getLocalizedDesc() {
        List<String> info = new ArrayList<>();

        if (modifier != 0) info.add(TinkersRebornUtils.translate(LOC_MultiplierDesc));
        if (bonusAmmo != 0) info.add(TinkersRebornUtils.translate(LOC_AmmoDesc));

        return info;
    }

    public static String formatModifier(float modifier) {
        return format(LOC_Multiplier, COLOR_Multiplier, modifier);
    }

    public static String formatAmmo(int bonusAmmo) {
        return format(LOC_Ammo, COLOR_Durability, bonusAmmo);
    }

}
