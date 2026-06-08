package mctbl.tinkersreborn.library.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.util.ColorUtil;

public final class MiningLevelHelper {

    private MiningLevelHelper() {}

    public static Map<String, MiningLevel> nameToLevel;
    public static List<MiningLevel> levelList;

    public static void init() {
        Map<String, EnumChatFormatting> charToFormatting = new HashMap<>();
        for (EnumChatFormatting e : EnumChatFormatting.values()) charToFormatting.put(e.toString(), e);

        nameToLevel = new HashMap<>();
        levelList = new ArrayList<>();
        int idx = 0;
        for (String s : TinkersRebornConfig.miningLevels) {
            String prefix = s.substring(0, 2);
            String local = s.substring(2);

            MiningLevel newLevel = new MiningLevel(idx, charToFormatting.get(prefix), prefix, local);
            levelList.add(newLevel);
            nameToLevel.put(local, newLevel);
            idx++;
        }
    }

    public static MiningLevel getMiningLevel(int level) {
        if (level < 0 || level > levelList.size()) return levelList.get(0);

        return levelList.get(level);
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
            return StatCollector.translateToLocal(this.localString);
        }

        public String getColorHex() {
            return Integer.toHexString(this.color)
                .toUpperCase();
        }
    }
}
