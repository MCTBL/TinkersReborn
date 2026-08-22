package mctbl.tinkersreborn.common.manuals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mctbl.tinkersreborn.library.gui.GuiManualButton;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TinkersRebornNavigationButton extends GuiManualButton {

    public enum ButtonSize {

        SMALL(0.8f),
        LARGE(2f),
        MEDIUM(1f);

        protected float multi;

        ButtonSize(float multi) {
            this.multi = multi;
        }

        public static ButtonSize getSize(String s) {
            switch (s) {
                case "large":
                    return LARGE;
                case "small":
                    return SMALL;
                case "medium":
                    return MEDIUM;
                default:
                    return LARGE;
            }
        }

        public float getMulti() {
            return multi;
        }
    }

    public static final int defaultHeight = 20;
    public static final int defaultWidth = 20;
    RenderItem itemRender = new RenderItem();
    ButtonSize bs;
    public ItemStack[] renderStack;
    public String target;
    public String buttonStrKey;
    public int color;

    public TinkersRebornNavigationButton(int id, ButtonSize bs, ItemStack s, String buttonStr, String target) {
        this(
            id,
            bs,
            new ItemStack[] { s },
            buttonStr,
            target,
            !buttonStr.isEmpty() ? Arrays.asList(buttonStr) : new ArrayList<>(),
            0x000000);
    }

    public TinkersRebornNavigationButton(int id, ButtonSize bs, ItemStack s, String target) {
        this(id, bs, s, "", target);
    }

    public TinkersRebornNavigationButton(int id, ButtonSize bs, ItemStack[] s, String target, String tooltips,
        int color) {
        this(id, bs, s, "", target, Arrays.asList(tooltips), color);
    }

    public TinkersRebornNavigationButton(int id, ButtonSize bs, ItemStack[] s, String buttonStr, String target,
        List<String> tooltips, int color) {
        super(id, 0, 0, defaultHeight, defaultWidth, "");
        this.bs = bs;
        this.renderStack = s;
        this.buttonStrKey = buttonStr;
        this.target = target;
        this.toolTips = tooltips;
        this.color = color;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, int manualTicks, int pageX, int pageY) {
        if (this.visible) {
            this.height = (int) (defaultHeight * this.bs.multi);
            this.width = (int) (defaultWidth * this.bs.multi);

            boolean isMouseInButton = this.isHover(mouseX, mouseY);

            if (isMouseInButton) {
                Gui.drawRect(
                    this.xPosition + pageX,
                    this.yPosition + pageY,
                    (int) (this.xPosition + pageX + defaultWidth * this.bs.multi),
                    (int) (this.yPosition + pageY + defaultHeight * this.bs.multi),
                    0xAAAAAAAA);
            }

            String boldButtonStr = ColorUtil.addBold(TinkersRebornUtils.translate(this.buttonStrKey));

            // let string a half size of multi
            GL11.glScalef(this.bs.multi / 2, this.bs.multi / 2, 1.0f);
            if (fontRender.getStringWidth(boldButtonStr) <= this.width / this.bs.multi * 2) {
                fontRender.drawString(
                    boldButtonStr,
                    (int) ((this.xPosition + pageX + this.width * 1.0F / 2) / this.bs.multi * 2
                        - fontRender.getStringWidth(boldButtonStr) * 1.0F / 2),
                    (int) ((this.yPosition + pageY + this.height) / this.bs.multi * 2 - fontRender.FONT_HEIGHT),
                    this.color);
            } else {
                // much smaller
                GL11.glScalef(0.8f, 0.8f, 1.0f);
                fontRender.drawString(
                    boldButtonStr,
                    (int) ((this.xPosition + pageX + this.width * 1.0F / 2) / this.bs.multi * 2.5
                        - fontRender.getStringWidth(boldButtonStr) * 1.0F / 2),
                    (int) ((this.yPosition + pageY + this.height) / this.bs.multi * 2.5 - fontRender.FONT_HEIGHT),
                    this.color);
                // resize back
                GL11.glScalef(1.25f, 1.25f, 1.0f);
            }
            // resize back and reset color
            GL11.glScalef(2.0f, 2.0f, 1.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            this.drawItem(mc, manualTicks, pageX, pageY);
            GL11.glScalef(1.0f / this.bs.multi, 1.0f / this.bs.multi, 1.0f);
        }
    }

    private void drawItem(final Minecraft mc, int manualTicks, int pageX, int pageY) {
        int length = renderStack.length;
        if (length == 0) {
            return;
        }
        int counter = manualTicks / 20;
        ItemStack stackToRender = this.renderStack[counter % length];
        if (stackToRender == null) {
            return;
        }

        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        this.itemRender.renderItemAndEffectIntoGUI(
            mc.fontRenderer,
            mc.renderEngine,
            stackToRender,
            (int) ((this.xPosition + pageX) / this.bs.multi + 2),
            (int) ((this.yPosition + pageY) / this.bs.multi + (this.buttonStrKey.isEmpty() ? 2 : 0)));
        this.itemRender.renderItemOverlayIntoGUI(
            mc.fontRenderer,
            mc.renderEngine,
            stackToRender,
            (int) ((this.xPosition + pageX) / this.bs.multi + 2),
            (int) ((this.yPosition + pageY) / this.bs.multi + (this.buttonStrKey.isEmpty() ? 2 : 0)));
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
    }
}
