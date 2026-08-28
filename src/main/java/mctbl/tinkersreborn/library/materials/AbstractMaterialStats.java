package mctbl.tinkersreborn.library.materials;

import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public abstract class AbstractMaterialStats implements IMaterialStats {

    public static final String COLOR_Durability = ColorUtil.encodeColor(0X55FF55);
    public static final String COLOR_Speed = ColorUtil.encodeColor(0X78A0CD);
    public static final String COLOR_Attack = ColorUtil.encodeColor(0XD76464);
    public static final String COLOR_Multiplier = ColorUtil.encodeColor(0XB9B95A);
    public static final String COLOR_Modifier = COLOR_Multiplier;

    public static final String COLOR_Drawspeed = ColorUtil.encodeColor(0X808080);
    public static final String COLOR_Range = ColorUtil.encodeColor(0X8CAFAF);
    public static final String COLOR_Damage = ColorUtil.encodeColor(0X9B5041);

    public static final String COLOR_Accuracy = ColorUtil.encodeColor(0XCDAACD);

    public static final String formatBase = "%s: %s%s";

    public static String format(String s, String color, int data) {
        return format(s, color, String.valueOf(data));
    }

    public static String format(String s, String color, float data) {
        return format(s, color, TinkersRebornUtils.df.format(data));
    }

    public static String formatNumberPercent(String s, String color, float data) {
        return format(s, color, TinkersRebornUtils.dfPercent.format(data));
    }

    public static String format(String s, String color, String data) {
        return String.format(formatBase, TinkersRebornUtils.translate(s), color, data);
    }
}
