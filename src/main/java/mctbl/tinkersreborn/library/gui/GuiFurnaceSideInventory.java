package mctbl.tinkersreborn.library.gui;

import mctbl.tinkersreborn.smeltery.entity.FurnaceLogic;
import mctbl.tinkersreborn.smeltery.gui.GuiFurnace;
import mctbl.tinkersreborn.util.TinkersStr;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiFurnaceSideInventory extends GuiSideInventory{
    public static final ResourceLocation SLOT_LOCATION = GuiFurnace.BACKGROUND;

    protected final FurnaceLogic furnace;

    protected GuiElement progressBar = new GuiElementScalable(176, 201, 3, 16, 256, 256);
    protected GuiElement unprogressBar = new GuiElementScalable(179, 201, 3, 16);
    protected GuiElement uberHeatBar = new GuiElementScalable(182, 201, 3, 16);
    protected GuiElement noMeltBar = new GuiElementScalable(185, 201, 3, 16);
    public GuiFurnaceSideInventory(GuiMultiModule parent, Container container, FurnaceLogic furnace, int slotCount,
                                   int columns) {
        super(parent, container, slotCount, columns, false, false);
        this.furnace = furnace;

        GuiElement.defaultTexH = 256;
        GuiElement.defaultTexW = 256;
        slot = new GuiElementScalable(0, 197, 22, 18);
        slotEmpty = new GuiElementScalable(22, 197, 22, 18);
        yOffset = 0;
    }

    @Override
    protected boolean shouldDrawName() {
        return false;
    }

    @Override
    public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
        // at most as big as the parent
        this.ySize = calcCappedYSize(parentSizeY / 2 + slot.w) ;
        // slider needed?
        if (getDisplayedRows() < getTotalRows()) {
            slider.enable();
            this.xSize = columns * slot.w + slider.width + 2 * border.w;
        } else {
            slider.disable();
            this.xSize = columns * slot.w + border.w * 2;
        }

        // update position
        super.updatePosition(parentX + 118, parentY + 18, this.xSize, this.ySize);

        // connected needs to move to the side
        if (connected) {
            if (yOffset == 0) {
                if (right) {
                    border.cornerTopLeft = overlapTop;
                } else {
                    border.cornerTopRight = overlapTop;
                }
            }

            xOffset = (border.w - 1) * (right ? -1 : 1);
            guiLeft += xOffset;
        } else {
            xOffset = 0;
        }

        // move it a bit
        this.guiTop += yOffset;

        border.setPosition(guiLeft, guiTop);
        border.setSize(xSize, ySize);

        int y = guiTop + border.h;
        int h = ySize - border.h * 2;

        if (shouldDrawName()) {
            y += textBackground.h;
            h -= textBackground.h;
        }
        slider.setPosition(guiLeft + columns * slot.w + border.w, y);
        slider.setSize(h);
        slider.setSliderParameters(0, getTotalRows() - getDisplayedRows(), 1);

        updateSlots();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        guiLeft += border.w;
        guiTop += border.h;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(GUI_INVENTORY);

        int x = guiLeft;// + border.w;
        int y = guiTop;// + border.h;
        int midW = xSize - border.w * 2;
        int midH = ySize - border.h * 2;

        //border.draw();

        if (shouldDrawName()) {
            textBackground.drawScaledX(x, y, midW);
            y += textBackground.h;
        }

        this.mc.getTextureManager()
            .bindTexture(GUI_INVENTORY);
        drawSlots(x, y);

        if (slider.isEnabled()) {
            slider.update(mouseX, mouseY, !isMouseOverFullSlot(mouseX, mouseY) && isMouseInModule(mouseX, mouseY));
            slider.draw();

            updateSlots();
        }

        guiLeft -= border.w;
        guiTop -= border.h;
    }

    @Override
    protected void updateSlots() {
        // adjust for the heat bar
        xOffset += 4 + 118 ;
        yOffset += 18;
        super.updateSlots();
        xOffset -= 4 + 118;
        yOffset -= 18;
    }

    @Override
    protected int drawSlots(int xPos, int yPos) {
        this.mc.getTextureManager()
            .bindTexture(SLOT_LOCATION);
        int ret = super.drawSlots(xPos, yPos);
        this.mc.getTextureManager()
            .bindTexture(GUI_INVENTORY);
        return ret;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (this.fontRendererObj == null) {
            this.fontRendererObj = this.parent.getFontRender();
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        this.mc.getTextureManager()
            .bindTexture(SLOT_LOCATION);
        RenderHelper.disableStandardItemLighting();

        String tooltipText = null;

        // draw the "heat" bars for each slot
        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot.getHasStack() && shouldDrawSlot(slot)) {
                float progress = furnace.getHeatingProgress(slot.getSlotIndex());
                String tooltip = null;
                GuiElement bar = progressBar;

                if (Float.isNaN(progress)) {
                    progress = 1f;
                    bar = noMeltBar;
                    tooltip = TinkersStr.smtleteryNoRecipe.toString();
                } else if (furnace.fuelReleaseTicks == 0) {
                    bar = unprogressBar;
                    progress = 1;
                    tooltip = TinkersStr.smtleteryNoFuel.toString();
                } else if ((progress > 1f && progress < 2f) || progress == Float.POSITIVE_INFINITY) {
                    progress = 1f;
                } else if (progress > 2f) {
                    bar = uberHeatBar;
                    progress = 1f;
                    tooltip = TinkersStr.smtleteryNoSpace.toString();
                }

                float x = slot.xDisplayPosition - 8;
                float y = slot.yDisplayPosition - 2 - bar.h;

                if (tooltip != null && x + guiLeft <= mouseX
                    && x + guiLeft + bar.w > mouseX
                    && y + guiTop <= mouseY
                    && y + guiTop + bar.h > mouseY) {
                    tooltipText = tooltip;
                }

                drawTexturedModalRect(x, y, bar.x, bar.y, bar.w, bar.h,progress);
            }
        }

        if (tooltipText != null) {
            drawHoveringText(
                this.fontRendererObj.listFormattedStringToWidth(tooltipText, 100),
                mouseX - guiLeft,
                mouseY - guiTop,
                this.fontRendererObj);
        }

        RenderHelper.enableStandardItemLighting();
    }

    public void drawTexturedModalRect(float x, float y, float textureX, float textureY, float width, float height,
        float progress) {
        if (progress < 0f) progress = 0f;
        if (progress > 1f) progress = 1f;

        float visibleHeight = height * progress;
        if (visibleHeight <= 0f) return;

        float f = 0.00390625F;

        float u1 = textureX * f;
        float u2 = (textureX + width) * f;
        float v1 = (textureY + height - visibleHeight) * f;
        float v2 = (textureY + height) * f;

        float screenTop = y + height - visibleHeight;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, screenTop + visibleHeight, this.zLevel, u1, v2);
        tessellator.addVertexWithUV(x + width, screenTop + visibleHeight, this.zLevel, u2, v2);
        tessellator.addVertexWithUV(x + width, screenTop, this.zLevel, u2, v1);
        tessellator.addVertexWithUV(x, screenTop, this.zLevel, u1, v1);
        tessellator.draw();
    }

}
