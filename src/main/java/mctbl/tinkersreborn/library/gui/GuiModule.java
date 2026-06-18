package mctbl.tinkersreborn.library.gui;

import java.awt.Rectangle;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// a sub-gui. Mostly the same as a separate GuiContainer, but doesn't do the calls that affect the game as if this were
// the only gui
@SideOnly(Side.CLIENT)
public abstract class GuiModule extends GuiContainer {

    protected final GuiMultiModule parent;

    // left or right of the parent
    protected final boolean right;
    // top or bottom of the parent
    protected final boolean bottom;

    public int yOffset = 0;
    public int xOffset = 0;

    public GuiModule(GuiMultiModule parent, Container container, boolean right, boolean bottom) {
        super(container);

        this.parent = parent;
        this.right = right;
        this.bottom = bottom;
    }

    public int guiRight() {
        return guiLeft + xSize;
    }

    public int guiBottom() {
        return guiTop + ySize;
    }

    public Rectangle getArea() {
        return new Rectangle(guiLeft, guiTop, xSize, ySize);
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
        if (right) {
            this.guiLeft = parentX + parentSizeX;
        } else {
            this.guiLeft = parentX - this.xSize;
        }

        if (bottom) {
            this.guiTop = parentY + parentSizeY - this.ySize;
        } else {
            this.guiTop = parentY;
        }

        this.guiLeft += xOffset;
        this.guiTop += yOffset;
    }

    public boolean shouldDrawSlot(Slot slot) {
        return true;
    }

    public boolean isMouseInModule(int mouseX, int mouseY) {
        return mouseX >= this.guiLeft && mouseX < this.guiRight() && mouseY >= this.guiTop && mouseY < this.guiBottom();
    }

    public boolean isMouseOverFullSlot(int mouseX, int mouseY) {
        for (Slot slot : inventorySlots.inventorySlots) {
            if (parent.func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY)
                && slot.getHasStack()) {
                return true;
            }
        }
        return false;
    }

    public void handleDrawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    public void handleDrawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    /**
     * Custom mouse click handling.
     *
     * @return True to prevent the main container handling the mouseclick
     */
    public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    /**
     * Custom mouse click handling.
     *
     * @return True to prevent the main container handling the mouseclick
     */
    public boolean handleMouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        return false;
    }

    /**
     * Custom mouse click handling.
     *
     * @return True to prevent the main container handling the mouseclick
     */
    public boolean handleMouseReleased(int mouseX, int mouseY, int state) {
        return false;
    }

    public int guiLeft() {
        return this.guiLeft;
    }

    public void guiLeftBias(int bias) {
        this.guiLeft += bias;
    }

    public int guiTop() {
        return this.guiTop;
    }

    public void guiTopBias(int bias) {
        this.guiTop += bias;
    }

    public int xSize() {
        return this.xSize;
    }

    public void xSizeBias(int bias) {
        this.xSize += bias;
    }

    public int ySize() {
        return this.ySize;
    }

    public void ySizeBias(int bias) {
        this.ySize += bias;
    }

    public RenderItem getRenderItem() {
        return itemRender;
    }

}
