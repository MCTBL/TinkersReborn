package mctbl.tinkersreborn.library.gui;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;

public class GuiButtonItem<T> extends GuiButton {

    // Positions from generic.png
    protected static final GuiElement GUI_Button_pressed = new GuiElement(144, 216, 18, 18, 256, 256);
    protected static final GuiElement GUI_Button_normal = new GuiElement(144 + 18 * 2, 216, 18, 18, 256, 256);
    protected static final GuiElement GUI_Button_hover = new GuiElement(144 + 18 * 4, 216, 18, 18, 256, 256);

    private final ItemStack icon;
    public final T data;
    public boolean pressed;

    private GuiElement guiPressed = GUI_Button_pressed;
    private GuiElement guiNormal = GUI_Button_normal;
    private GuiElement guiHover = GUI_Button_hover;
    private ResourceLocation locBackground = Icons.ICON;

    private GuiMultiModule parent;

    public GuiButtonItem(int buttonId, int x, int y, String displayName, @Nonnull T data, GuiMultiModule parent) {
        super(buttonId, x, y, 18, 18, displayName);

        this.icon = null;
        this.data = data;
        this.parent = parent;
    }

    public GuiButtonItem(int buttonId, int x, int y, ItemStack icon, @Nonnull T data, GuiMultiModule parent) {
        super(buttonId, x, y, 18, 18, icon.getDisplayName());

        this.icon = icon;
        this.data = data;
        this.parent = parent;
    }

    public GuiButtonItem<T> setGraphics(GuiElement normal, GuiElement hover, GuiElement pressed,
        ResourceLocation background) {
        guiPressed = pressed;
        guiNormal = normal;
        guiHover = hover;
        locBackground = background;

        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager()
            .bindTexture(locBackground);

        if (this.visible) {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;

            if (pressed) {
                guiPressed.draw(xPosition, yPosition);
            } else if (field_146123_n) {
                guiHover.draw(xPosition, yPosition);
            } else {
                guiNormal.draw(xPosition, yPosition);
            }

            drawIcon(mc);
        }
    }

    protected void drawIcon(Minecraft mc) {
        if (icon != null) {
            if (icon.getItem() instanceof TinkersRebornToolPart tp) {
                mc.getTextureManager()
                    .bindTexture(TextureMap.locationItemsTexture);
                this.drawTexturedModelRectFromIcon(xPosition, yPosition, tp.outlineIcon, 16, 16);
                mc.getTextureManager()
                    .bindTexture(locBackground);
            } else {
                parent.renderItemIntoGui(icon, xPosition + 1, yPosition + 1);
            }
        }
    }
}
