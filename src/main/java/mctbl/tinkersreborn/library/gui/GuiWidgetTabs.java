package mctbl.tinkersreborn.library.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWidgetTabs extends GuiWidget {

    private static final ResourceLocation creativeInventoryTabs = new ResourceLocation(
        "textures/gui/container/creative_inventory/tabs.png");

    private final GuiElement[] tabActive = new GuiElement[3];
    private final GuiElement[] tab = new GuiElement[3];

    // change this if you need different tabs
    public ResourceLocation tabsResource = creativeInventoryTabs;
    // changes the y-offset of the tab row
    public int yOffset = 4;
    // space between 2 tabs
    public int spacing = 2;

    public int selected;
    public int highlighted;
    protected List<ItemStack> icons = Lists.newArrayList();

    private final GuiMultiModule parent;
    private boolean clicked = false;

    public GuiWidgetTabs(GuiMultiModule parent, GuiElement tabLeft, GuiElement tabCenter, GuiElement tabRight,
        GuiElement activeLeft, GuiElement activeCenter, GuiElement activeRight) {
        this.parent = parent;

        this.tab[0] = tabLeft;
        this.tab[1] = tabCenter;
        this.tab[2] = tabRight;
        this.tabActive[0] = activeLeft;
        this.tabActive[1] = activeCenter;
        this.tabActive[2] = activeRight;

        selected = 0;
    }

    public void addTab(ItemStack icon) {
        icons.add(icon);
    }

    public void clear() {
        selected = 0;
        icons.clear();
    }

    public void update(int mouseX, int mouseY) {
        boolean mouseDown = Mouse.isButtonDown(0); // left mouse button

        // did we click on a tab?
        mouseX -= this.xPos;
        mouseY -= this.yPos;

        // update highlighted
        highlighted = -1;
        if (mouseY >= 0 && mouseY <= tab[1].h) {
            // which one did we click?
            int x = 0;
            for (int i = 0; i < icons.size(); i++) {
                // clicking on spacing has no effect
                if (mouseX >= x && mouseX < x + tab[1].w) {
                    highlighted = i;
                    break;
                }
                x += tab[1].w;
                x += spacing;
            }
        }

        // already clicked
        if (clicked) {
            // still clicking
            if (mouseDown) {
                return;
            }
            // release click
            else {
                clicked = false;
                return;
            }
        }
        // new click
        else if (mouseDown) {
            clicked = true;
        }
        // no click - do nothing
        else {
            return;
        }

        // was new click, select highlighted
        if (highlighted > -1) {
            selected = highlighted;
        }
    }

    @Override
    public void draw() {
        int y = yPos + yOffset;
        for (int i = 0; i < icons.size(); i++) {
            int x = xPos + i * tab[0].w;

            if (i > 0) {
                x += i * spacing;
            }

            GuiElement[] toDraw;
            if (i == selected) {
                toDraw = tabActive;
            } else {
                toDraw = tab;
            }

            GuiElement actualTab;
            if (i == 0 && x == parent.cornerX) {
                actualTab = toDraw[0];
            } else if (x == parent.cornerX + parent.width) {
                actualTab = toDraw[2];
            } else {
                actualTab = toDraw[1];
            }

            // rebind texture from drawing an itemstack
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(tabsResource);
            actualTab.draw(x, y);

            ItemStack icon = icons.get(i);
            if (icon != null) {
                int iconX = x + (actualTab.w - 16) / 2;
                int iconY = y + (actualTab.h - 16) / 2;

                GL11.glPushMatrix();
                GL11.glPushAttrib(
                    GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT
                        | GL11.GL_CURRENT_BIT
                        | GL11.GL_COLOR_BUFFER_BIT
                        | GL11.GL_DEPTH_BUFFER_BIT);

                float previousLightmapX = OpenGlHelper.lastBrightnessX;
                float previousLightmapY = OpenGlHelper.lastBrightnessY;
                float previousItemZLevel = parent.getRenderItem().zLevel;

                try {
                    // Match the state used by GuiContainer when drawing inventory slots.
                    RenderHelper.enableGUIStandardItemLighting();
                    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(true);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

                    parent.getRenderItem().zLevel = 0.0F;
                    parent.renderItemIntoGui(icon, iconX, iconY);
                } finally {
                    parent.getRenderItem().zLevel = previousItemZLevel;

                    OpenGlHelper
                        .setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightmapX, previousLightmapY);

                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
            }
        }
    }
}
