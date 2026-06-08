package mctbl.tinkersreborn.util;

import java.awt.Color;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class ColorUtil {

    public static int MARKER = 0xE700;

    private ColorUtil() {}

    public static String addBlack(String str) {
        return EnumChatFormatting.BLACK + str + EnumChatFormatting.RESET;
    }

    public static String addDarkBlue(String str) {
        return EnumChatFormatting.DARK_BLUE + str + EnumChatFormatting.RESET;
    }

    public static String addDarkGreen(String str) {
        return EnumChatFormatting.DARK_GREEN + str + EnumChatFormatting.RESET;
    }

    public static String addDarkAqua(String str) {
        return EnumChatFormatting.DARK_AQUA + str + EnumChatFormatting.RESET;
    }

    public static String addDarkRed(String str) {
        return EnumChatFormatting.DARK_RED + str + EnumChatFormatting.RESET;
    }

    public static String addDarkPurple(String str) {
        return EnumChatFormatting.DARK_PURPLE + str + EnumChatFormatting.RESET;
    }

    public static String addGold(String str) {
        return EnumChatFormatting.GOLD + str + EnumChatFormatting.RESET;
    }

    public static String addGray(String str) {
        return EnumChatFormatting.GRAY + str + EnumChatFormatting.RESET;
    }

    public static String addDarkGray(String str) {
        return EnumChatFormatting.DARK_GRAY + str + EnumChatFormatting.RESET;
    }

    public static String addBlue(String str) {
        return EnumChatFormatting.BLUE + str + EnumChatFormatting.RESET;
    }

    public static String addGreen(String str) {
        return EnumChatFormatting.GREEN + str + EnumChatFormatting.RESET;
    }

    public static String addAqua(String str) {
        return EnumChatFormatting.AQUA + str + EnumChatFormatting.RESET;
    }

    public static String addRed(String str) {
        return EnumChatFormatting.RED + str + EnumChatFormatting.RESET;
    }

    public static String addLightPurple(String str) {
        return EnumChatFormatting.LIGHT_PURPLE + str + EnumChatFormatting.RESET;
    }

    public static String addYellow(String str) {
        return EnumChatFormatting.YELLOW + str + EnumChatFormatting.RESET;
    }

    public static String addWhite(String str) {
        return EnumChatFormatting.WHITE + str + EnumChatFormatting.RESET;
    }

    public static String addObfuscated(String str) {
        return EnumChatFormatting.OBFUSCATED + str + EnumChatFormatting.RESET;
    }

    public static String addBold(String str) {
        return EnumChatFormatting.BOLD + str + EnumChatFormatting.RESET;
    }

    public static String addStrikethrough(String str) {
        return EnumChatFormatting.STRIKETHROUGH + str + EnumChatFormatting.RESET;
    }

    public static String addUnderLine(String str) {
        return EnumChatFormatting.UNDERLINE + str + EnumChatFormatting.RESET;
    }

    public static String addItalic(String str) {
        return EnumChatFormatting.ITALIC + str + EnumChatFormatting.RESET;
    }

    /**
     * Returns the actual color value for a chatformatting
     * 
     * @param color EnumChatFormatting
     * @return RGB color
     */
    public static int enumChatFormattingToColor(EnumChatFormatting color) {
        // only for EnumChatFormatting.BLACK to WHITE
        int i = Integer.parseInt(String.valueOf(color.getFormattingCode()), 16);
        int j = (i >> 3 & 1) * 85;
        int k = (i >> 2 & 1) * 170 + j;
        int l = (i >> 1 & 1) * 170 + j;
        int i1 = (i >> 0 & 1) * 170 + j;
        if (i == 6) {
            k += 85;
        }
        if (i >= 16) {
            k /= 4;
            l /= 4;
            i1 /= 4;
        }

        return (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
    }

    public static int compose(int r, int g, int b, int a) {
        int rgb = a;
        rgb = (rgb << 8) + r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;
        return rgb;
    }

    public static int alpha(int c) {
        return (c >> 24) & 0xFF;
    }

    public static int red(int c) {
        return (c >> 16) & 0xFF;
    }

    public static int green(int c) {
        return (c >> 8) & 0xFF;
    }

    public static int blue(int c) {
        return (c) & 0xFF;
    }

    /**
     * Takes a value between 0.0 and 1.0. Returns a color between red and green,
     * depending on the value. 1.0 is green. If the value goes above 1.0 it
     * continues along the color spectrum.
     */
    public static String valueToColorCode(float v) {
        // 0.0 -> 0 = red
        // 1.0 -> 1/3 = green
        // 1.5 -> 1/2 = aqua
        v /= 3f;
        v = MathHelper.clamp_float(v, 0.01f, 0.5f);
        int color = Color.HSBtoRGB(v, 0.65f, 0.8f);
        return encodeColor(color);
    }

    public static String encodeColor(int color) {
        int r = ((color >> 16) & 255);
        int g = ((color >> 8) & 255);
        int b = ((color >> 0) & 255);
        return encodeColor(r, g, b);
    }

    public static String encodeColor(float r, float g, float b) {
        return encodeColor((int) r * 255, (int) g * 255, (int) b * 255);
    }

    public static String encodeColor(int r, int g, int b) {
        return String.format(
            "%c%c%c",
            ((char) (MARKER + (r & 0xFF))),
            ((char) (MARKER + (g & 0xFF))),
            ((char) (MARKER + (b & 0xFF))));
    }

    public static String formatPartialAmount(int value, int max) {
        return String.format(
            "%s%s" + EnumChatFormatting.GRAY.toString() + "/" + EnumChatFormatting.RESET.toString() + "%s%s",
            valueToColorCode((float) value / (float) max),
            TinkersRebornUtils.df.format(value),
            valueToColorCode(1f),
            TinkersRebornUtils.df.format(max));
    }

}
