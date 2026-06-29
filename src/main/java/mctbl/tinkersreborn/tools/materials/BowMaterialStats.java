package mctbl.tinkersreborn.tools.materials;

import java.util.ArrayList;
import java.util.List;

import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class BowMaterialStats extends AbstractMaterialStats {

    public final static String LOC_Drawspeed = "tinkersreborn.stat.bow.drawspeed.name";
    public final static String LOC_Range = "tinkersreborn.stat.bow.range.name";
    public final static String LOC_Damage = "tinkersreborn.stat.bow.damage.name";

    public final static String LOC_DrawspeedDesc = "tinkersreborn.stat.bow.drawspeed.desc";
    public final static String LOC_RangeDesc = "tinkersreborn.stat.bow.range.desc";
    public final static String LOC_DamageDesc = "tinkersreborn.stat.bow.damage.desc";

    /**
     * Ok, here is where things get complicated. Think about the bonus damage as the
     * extra damage the arrow has because the force he was shot with was so great
     * Usually this is higher, the higher the range. But it can't scale directly
     * because that leads to problems with how it interacts. Think of the bonus
     * damage as a flat damage-reward for using materials that are slower, but
     * flexible, like metals.
     */
    public final float bonusDamage;
    public final float drawspeed;
    public final float range;

    /**
     * @param bonusDamage
     * @param drawspeed
     * @param range
     */
    public BowMaterialStats(float bonusDamage, float drawspeed, float range) {
        this.bonusDamage = bonusDamage;
        this.drawspeed = drawspeed;
        this.range = range;
    }

    @Override
    public MaterialStatusType getIdentifier() {
        return MaterialStatusType.BOW;
    }

    @Override
    public String getLocalizedName() {
        return TinkersStr.bowStatsName.toString();
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = new ArrayList<>();

        if (1f / drawspeed != 0) info.add(formatDrawspeed(this.drawspeed));
        if (range != 0) info.add(formatRange(this.range));
        if (bonusDamage != 0) info.add(formatDamage(this.bonusDamage));

        return info;
    }

    @Override
    public List<String> getLocalizedDesc() {
        List<String> info = new ArrayList<>();

        if (1f / drawspeed != 0) info.add(TinkersRebornUtils.translate(LOC_DrawspeedDesc));
        if (range != 0) info.add(TinkersRebornUtils.translate(LOC_RangeDesc));
        if (bonusDamage != 0) info.add(TinkersRebornUtils.translate(LOC_DamageDesc));

        return info;
    }

    public static String formatDrawspeed(float drawspeed) {
        return format(LOC_Drawspeed, COLOR_Drawspeed, 1f / drawspeed);
    }

    public static String formatRange(float range) {
        return format(LOC_Range, COLOR_Range, range);
    }

    public static String formatDamage(float bonusDamage) {
        return format(LOC_Damage, COLOR_Damage, bonusDamage);
    }

}
