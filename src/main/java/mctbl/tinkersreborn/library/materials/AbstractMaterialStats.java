package mctbl.tinkersreborn.library.materials;

import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public abstract class AbstractMaterialStats implements IMaterialStats {

    public final static String COLOR_Durability = ColorUtil.encodeColor(0X55FF55);
    public final static String COLOR_Speed = ColorUtil.encodeColor(0X78A0CD);
    public final static String COLOR_Attack = ColorUtil.encodeColor(0XD76464);
    public final static String COLOR_Multiplier = ColorUtil.encodeColor(0XB9B95A);
    public final static String COLOR_Modifier = COLOR_Multiplier;

    public final static String COLOR_Drawspeed = ColorUtil.encodeColor(0X808080);
    public final static String COLOR_Range = ColorUtil.encodeColor(0X8CAFAF);
    public final static String COLOR_Damage = ColorUtil.encodeColor(0X9B5041);

    public final static String COLOR_Accuracy = ColorUtil.encodeColor(0XCDAACD);

    public final static String formatBase = "%s: %s%s";

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
