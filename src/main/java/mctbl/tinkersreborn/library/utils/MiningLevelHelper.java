package mctbl.tinkersreborn.library.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public final class MiningLevelHelper {

    private static final String chatFormattingCode = "§";

    private MiningLevelHelper() {}

    public static Map<String, MiningLevel> nameToLevel;
    public static List<MiningLevel> levelList;

    public static void preInit() {
        Map<String, EnumChatFormatting> charToFormatting = new HashMap<>();
        for (EnumChatFormatting e : EnumChatFormatting.values()) charToFormatting.put(e.toString(), e);

        nameToLevel = new HashMap<>();
        levelList = new ArrayList<>();
        int idx = 0;
        for (String s : TinkersRebornConfig.miningLevels) {
            MiningLevel newLevel = null;
            String local = null;
            if (s.startsWith(chatFormattingCode)) {
                String prefix = s.substring(0, 2);
                local = s.substring(2);
                newLevel = new MiningLevel(idx, charToFormatting.get(prefix), prefix, local);
            } else if (s.startsWith("#")) {
                // #5C1BBD
                String prefix = s.substring(1, 7);
                local = s.substring(7);
                newLevel = new MiningLevel(idx, Integer.parseInt(prefix, 16), prefix, local);
            } else if (s.startsWith("0X") || s.startsWith("0x")) {
                // 0X5C1BBD / 0x5C1BBD
                String prefix = s.substring(2, 8);
                local = s.substring(8);
                newLevel = new MiningLevel(idx, Integer.parseInt(prefix, 16), prefix, local);
            }
            if (newLevel != null && local != null) {
                levelList.add(newLevel);
                nameToLevel.put(local, newLevel);
                idx++;
            }
        }

        // Freeze after initialization
        nameToLevel = ImmutableMap.copyOf(nameToLevel);
        levelList = ImmutableList.copyOf(levelList);
    }

    public static MiningLevel getMiningLevel(int level) {
        return levelList.get(MathHelper.clamp_int(level, 0, levelList.size() - 1));
    }

    public static MiningLevel getLastMiningLevel() {
        return getMiningLevel(levelList.size() - 1);
    }

    public static int getVanillaHarvestLevelMapping(int level) {
        if (level >= TinkersRebornConfig.vanillaHarvestLevelMapping.length) {
            // 0 2 5 7
            // 0 1 2 3
            // if last level 3 is map to 7, if trying to map level 4, it will get 4 + (7 - 3)
            int sub = TinkersRebornConfig.vanillaHarvestLevelMapping[TinkersRebornConfig.vanillaHarvestLevelMapping.length
                - 1] - (TinkersRebornConfig.vanillaHarvestLevelMapping.length - 1);
            return level + sub;
        }
        return TinkersRebornConfig.vanillaHarvestLevelMapping[level];
    }

    public static class MiningLevel {

        public int levelIdx;
        public int color;
        public String colorPrefix;
        public String localString;

        private MiningLevel(int levelIdx, EnumChatFormatting formatting, String colorPrefix, String localString) {
            this(levelIdx, ColorUtil.enumChatFormattingToColor(formatting), colorPrefix, localString);
        }

        private MiningLevel(int levelIdx, int color, String colorPrefix, String localString) {
            this.levelIdx = levelIdx;
            this.color = color;
            this.colorPrefix = colorPrefix;
            this.localString = localString;
        }

        public String getLocalization() {
            return TinkersRebornUtils.translate(this.localString);
        }

        public String getColoredLocalization() {
            return ColorUtil.encodeColor(this.color) + TinkersRebornUtils.translate(this.localString);
        }

    }
}
