package mctbl.tinkersreborn.library.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

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

            // todo: draw all the tabs first and then all the itemstacks so it doesn't have
            // to switch texture in between all the time

            // rebind texture from drawing an itemstack
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(tabsResource);
            actualTab.draw(x, y);

            ItemStack icon = icons.get(i);
            if (icon != null) {
                drawItemStack(icon, x + (actualTab.w - 16) / 2, y + (actualTab.h - 16) / 2);
            }
        }
    }

    // guiContainer.drawItemStack
    private void drawItemStack(ItemStack stack, int x, int y) {
        RenderItem itemRender = this.parent.getRenderItem();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);

        RenderHelper.enableGUIStandardItemLighting();

        itemRender.zLevel = 200;
        itemRender.renderItemAndEffectIntoGUI(
            stack.getItem()
                .getFontRenderer(stack),
            Minecraft.getMinecraft()
                .getTextureManager(),
            stack,
            x,
            y);
        itemRender.zLevel = 0.0F;

        RenderHelper.disableStandardItemLighting();

        GL11.glPopMatrix();
    }
}
