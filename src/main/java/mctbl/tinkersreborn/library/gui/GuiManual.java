package mctbl.tinkersreborn.library.gui;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiManual extends GuiScreen {

    public static final int TEX_SIZE = 512;

    public static int PAGE_MARGIN = 8;

    public static int PAGE_PADDING_TOP = 4;
    public static int PAGE_PADDING_BOT = 4;
    public static int PAGE_PADDING_LEFT = 8;
    public static int PAGE_PADDING_RIGHT = 0;

    public static float PAGE_SCALE = 1f;
    public static int PAGE_WIDTH_UNSCALED = 206;
    public static int PAGE_HEIGHT_UNSCALED = 200;

    // For best results, make sure both PAGE_WIDTH_UNSCALED - (PAGE_PADDING + PAGE_MARGIN) * 2 and PAGE_HEIGHT_UNSCALED
    // - (PAGE_PADDING + PAGE_MARGIN) * 2 divide evenly into PAGE_SCALE (without remainder)
    public static int PAGE_WIDTH;
    public static int PAGE_HEIGHT;

    static {
        init(); // initializes page width and height
    }

    public static void init() {
        PAGE_WIDTH = (int) ((PAGE_WIDTH_UNSCALED - (PAGE_PADDING_LEFT + PAGE_PADDING_RIGHT + PAGE_MARGIN + PAGE_MARGIN))
            / PAGE_SCALE);
        PAGE_HEIGHT = (int) ((PAGE_HEIGHT_UNSCALED - (PAGE_PADDING_TOP + PAGE_PADDING_BOT + PAGE_MARGIN + PAGE_MARGIN))
            / PAGE_SCALE);
    }

}
