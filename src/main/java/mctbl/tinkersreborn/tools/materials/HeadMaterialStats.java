package mctbl.tinkersreborn.tools.materials;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

import mctbl.tinkersreborn.library.materials.AbstractMaterialStats;
import mctbl.tinkersreborn.library.materials.IMaterialStats;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.utils.MiningLevelHelper;
import mctbl.tinkersreborn.library.utils.MiningLevelHelper.MiningLevel;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersStr;

public class HeadMaterialStats extends AbstractMaterialStats {

    public final static String LOC_Durability = TinkersStr.durability.toString();
    public final static String LOC_MiningSpeed = TinkersStr.miningSpeed.toString();
    public final static String LOC_Attack = TinkersStr.attack.toString();
    public final static String LOC_HarvestLevel = TinkersStr.harvestLevel.toString();

    public final static String LOC_DurabilityDesc = TinkersStr.durabilityDesc.toString();
    public final static String LOC_MiningSpeedDesc = TinkersStr.miningSpeedDesc.toString();
    public final static String LOC_AttackDesc = TinkersStr.attackDesc.toString();
    public final static String LOC_HarvestLevelDesc = TinkersStr.harvestLevelDesc.toString();

    private int durability; // usually between 1 and 1000
    private int harvestLevel; // see MiningLevelHelper class
    private float attack; // usually between 0 and 10 (in 1/2 hearts, so divide by 2 for damage in hearts)
    private float miningspeed; // usually between 1 and 10

    public HeadMaterialStats(int durability, int harvestLevel, float attack, float miningspeed) {
        this.durability = durability;
        this.harvestLevel = harvestLevel;
        this.attack = attack;
        this.miningspeed = miningspeed;
    }

    public int getDurability() {
        return durability;
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }

    public float getAttack() {
        return attack;
    }

    public float getMiningspeed() {
        return miningspeed;
    }

    @Override
    public MaterialStatusType getIdentifier() {
        return MaterialStatusType.HEAD;
    }

    @Override
    public String getLocalizedName() {
        return TinkersStr.headStatsName.toString();
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = new ArrayList<>();

        if (durability != 0) info.add(formatDurability(this.durability));
        info.add(formatHarvestLevel(this.harvestLevel));
        if (miningspeed != 0) info.add(formatMiningSpeed(this.miningspeed));
        if (attack != 0) info.add(formatAttack(this.attack));

        return info;
    }

    @Override
    public List<String> getLocalizedDesc() {
        List<String> info = new ArrayList<>();

        if (durability != 0) info.add(LOC_DurabilityDesc);
        info.add(LOC_HarvestLevelDesc);
        if (miningspeed != 0) info.add(LOC_MiningSpeedDesc);
        if (attack != 0) info.add(LOC_AttackDesc);

        return info;
    }

    public static String formatHarvestLevel(int harvestLevel) {
        MiningLevel miningLevel = MiningLevelHelper.getMiningLevel(harvestLevel);
        return format(LOC_HarvestLevel, ColorUtil.encodeColor(miningLevel.color), miningLevel.getLocalization());
    }

    public static String harvestLevel(int harvestLevel) {
        return MiningLevelHelper.getMiningLevel(harvestLevel)
            .getColoredLocalization();
    }

    public static String formatDurability(int durability) {
        return format(LOC_Durability, COLOR_Durability, durability);
    }

    public static String formatDurability(int durability, int maxDurability) {
        return String.format("%s: %s", LOC_Durability, ColorUtil.formatPartialAmount(durability, maxDurability));
    }

    public static String formatMiningSpeed(float miningspeed) {
        return format(LOC_MiningSpeed, COLOR_Speed, miningspeed);
    }

    public static String formatAttack(float attack) {
        return format(LOC_Attack, COLOR_Attack, attack);
    }

    @Override
    public void writeToCfg(Configuration cfg, String categotry) {
        String key = categotry + "."
            + this.getIdentifier()
                .toString()
                .toLowerCase();
        cfg.get(key, "durability", this.durability)
            .getInt();
        cfg.get(key, "harvestLevel", this.harvestLevel)
            .getInt();
        cfg.get(key, "attack", this.attack)
            .getDouble();
        cfg.get(key, "miningspeed", this.miningspeed)
            .getDouble();
    }

    @Override
    public IMaterialStats readFromCfg(Configuration cfg, String categotry) {
        String key = categotry + "."
            + this.getIdentifier()
                .toString()
                .toLowerCase();
        this.durability = cfg.get(key, "durability", this.durability)
            .getInt();
        this.harvestLevel = cfg.get(key, "harvestLevel", this.harvestLevel)
            .getInt();
        this.attack = (float) cfg.get(key, "attack", this.attack)
            .getDouble();
        this.miningspeed = (float) cfg.get(key, "miningspeed", this.miningspeed)
            .getDouble();
        return this;
    }

    @Override
    public IMaterialStats getNewStatsFromCfg(Configuration cfg, String categotry) {
        return new HeadMaterialStats(0, 0, 0, 0).readFromCfg(cfg, categotry);
    }

}
