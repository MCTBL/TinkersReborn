package mctbl.tinkersreborn.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import mctbl.tinkersreborn.util.ColorUtil;

public class TinkersRebornFontRender extends FontRenderer {

    protected int state = 0;
    protected int red;
    protected int green;
    protected int blue;
    protected boolean needShadow;
    private static final String NEWLINE_REGEX_SEQUENCE = "\\\\n";

    protected static final Map<Character, String> COLOR_MAP = new HashMap<Character, String>();

    public TinkersRebornFontRender(GameSettings gameSettingsIn, ResourceLocation location,
        TextureManager textureManagerIn) {
        super(gameSettingsIn, location, textureManagerIn, true);
        COLOR_MAP.put('0', ColorUtil.encodeColor(0x000000));
        COLOR_MAP.put('1', ColorUtil.encodeColor(0x0000AA));
        COLOR_MAP.put('2', ColorUtil.encodeColor(0x00AA00));
        COLOR_MAP.put('3', ColorUtil.encodeColor(0x00AAAA));
        COLOR_MAP.put('4', ColorUtil.encodeColor(0xAA0000));
        COLOR_MAP.put('5', ColorUtil.encodeColor(0xAA00AA));
        COLOR_MAP.put('6', ColorUtil.encodeColor(0xFFAA00));
        COLOR_MAP.put('7', ColorUtil.encodeColor(0xAAAAAA));
        COLOR_MAP.put('8', ColorUtil.encodeColor(0x555555));
        COLOR_MAP.put('9', ColorUtil.encodeColor(0x5555FF));
        COLOR_MAP.put('a', ColorUtil.encodeColor(0x55FF55));
        COLOR_MAP.put('b', ColorUtil.encodeColor(0x55FFFF));
        COLOR_MAP.put('c', ColorUtil.encodeColor(0xFF5555));
        COLOR_MAP.put('d', ColorUtil.encodeColor(0xFF55FF));
        COLOR_MAP.put('e', ColorUtil.encodeColor(0xFFFF55));
        COLOR_MAP.put('f', ColorUtil.encodeColor(0xFFFFFF));
    }

    @Override
    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        return Arrays.stream(str.split(NEWLINE_REGEX_SEQUENCE))
            .flatMap(
                line -> Arrays.stream(
                    this.wrapFormattedStringToWidth(line, wrapWidth)
                        .split("\n")))
            .collect(Collectors.toList());
    }

    @Override
    public int drawStringWithShadow(String text, int x, int y, int color) {
        this.needShadow = true;
        int l = super.drawString(
            this.changeMCFormatToTinkersColor(text),
            x + 1,
            y + 1,
            (color & 16579836) >> 2 | color & -16777216,
            false);
        this.needShadow = false;
        return Math.max(super.drawString(text, x, y, color, false), l);
    }

    protected String changeMCFormatToTinkersColor(String input) {
        if (input == null || input.isEmpty() || !input.contains("§")) {
            return input;
        }

        StringBuilder sb = new StringBuilder(input.length() + 8);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '§' && i + 1 < input.length()) {
                char next = Character.toLowerCase(input.charAt(i + 1));
                String replacement = COLOR_MAP.get(next);
                if (replacement != null) {
                    sb.append(replacement);
                    i += 1;
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    protected float renderUnicodeChar(char letter, boolean italic) {
        // special color settings through char code
        // we use \u2700 to \u27FF, where the lower byte represents the Hue of the color
        if ((int) letter >= ColorUtil.MARKER && (int) letter <= ColorUtil.MARKER + 0xFF) {
            int value = letter & 0xFF;
            switch (state) {
                case 0:
                    red = value >> (this.needShadow ? 2 : 0);
                    break;
                case 1:
                    green = value >> (this.needShadow ? 2 : 0);
                    break;
                case 2:
                    blue = value >> (this.needShadow ? 2 : 0);
                    break;
                default:
                    this.setColor(1f, 1f, 1f, 1f);
                    return 0;
            }

            state = ++state % 3;

            int color = (red << 16) | (green << 8) | blue | (0xff << 24);
            if ((color & 0XFC000000) == 0) {
                color |= 0xFF000000;
            }

            this.setColor(
                ((color >> 16) & 255) / 255f,
                ((color >> 8) & 255) / 255f,
                ((color >> 0) & 255) / 255f,
                ((color >> 24) & 255) / 255f);
            return 0;
        }

        // invalid sequence encountered
        if (state != 0) {
            state = 0;
            this.setColor(1f, 1f, 1f, 1f);
        }

        return super.renderUnicodeChar(letter, italic);
    }

    @SuppressWarnings("all")
    protected String wrapFormattedStringToWidth(String str, int wrapWidth) {
        int i = this.sizeStringToWidth(str, wrapWidth);

        if (str.length() <= i) {
            return str;
        } else {
            String s = str.substring(0, i);
            char c0 = str.charAt(i);
            boolean flag = c0 == 32 || c0 == 10;
            String s1 = getCustomFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
            return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
        }
    }

    public static String getCustomFormatFromString(String text) {
        String s = "";
        int i = 0;
        int j = text.length();

        while ((i < j - 1)) {
            char c = text.charAt(i);
            // vanilla formatting
            if (c == 167) {

                char c0 = text.charAt(i + 1);

                if (c0 >= 48 && c0 <= 57 || c0 >= 97 && c0 <= 102 || c0 >= 65 && c0 <= 70) {
                    s = "\u00a7" + c0;
                    i++;
                } else if (c0 >= 107 && c0 <= 111 || c0 >= 75 && c0 <= 79 || c0 == 114 || c0 == 82) {
                    s = s + "\u00a7" + c0;
                    i++;
                }
            }
            // custom formatting
            else if ((int) c >= ColorUtil.MARKER && (int) c <= ColorUtil.MARKER + 0xFF) {
                s = String.format("%s%s%s", c, text.charAt(i + 1), text.charAt(i + 2));
                i += 2;
            }
            i++;
        }

        return s;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        super.onResourceManagerReload(resourceManager);
        setUnicodeFlag(
            Minecraft.getMinecraft()
                .getLanguageManager()
                .isCurrentLocaleUnicode() || Minecraft.getMinecraft().gameSettings.forceUnicodeFont);
        setBidiFlag(
            Minecraft.getMinecraft()
                .getLanguageManager()
                .isCurrentLanguageBidirectional());
    }

    /**
     * Determines how many characters from the string will fit into the specified
     * width.
     */
    public int sizeStringToWidth(String str, int wrapWidth) {
        int i = str.length();
        int j = 0;
        int k = 0;
        int l = -1;

        for (boolean flag = false; k < i; ++k) {
            char c0 = str.charAt(k);

            switch (c0) {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += getCharWidth(c0);

                    if (flag) {
                        ++j;
                    }

                    break;
                case '\u00a7':

                    if (k < i - 1) {
                        ++k;
                        char c1 = str.charAt(k);

                        if (c1 != 'l' && c1 != 'L') {
                            if (c1 == 'r' || c1 == 'R' || isFormatColor(c1)) {
                                flag = false;
                            }
                        } else {
                            flag = true;
                        }
                    }
            }

            if (c0 == '\n') {
                ++k;
                l = k;
                break;
            }

            if (j > wrapWidth) {
                break;
            }
        }

        return k != i && l != -1 && l < k ? l : k;
    }

    /**
     * Checks if the char code is a hexadecimal character, used to set colour.
     */
    protected static boolean isFormatColor(char colorChar) {
        return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102
            || colorChar >= 65 && colorChar <= 70;
    }

}
