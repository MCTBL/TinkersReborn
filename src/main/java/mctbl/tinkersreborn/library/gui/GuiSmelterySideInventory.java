package mctbl.tinkersreborn.library.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.smeltery.gui.GuiSmeltery;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class GuiSmelterySideInventory extends GuiSideInventory {

    public static final ResourceLocation SLOT_LOCATION = GuiSmeltery.BACKGROUND;

    protected final SmelteryLogic smeltery;

    protected GuiElement progressBar = new GuiElementScalable(176, 150, 3, 16, 256, 256);
    protected GuiElement unprogressBar = new GuiElementScalable(179, 150, 3, 16);
    protected GuiElement uberHeatBar = new GuiElementScalable(182, 150, 3, 16);
    protected GuiElement noMeltBar = new GuiElementScalable(185, 150, 3, 16);

    public GuiSmelterySideInventory(GuiMultiModule parent, Container container, SmelteryLogic smeltery, int slotCount,
        int columns) {
        super(parent, container, slotCount, columns, false, true);

        this.smeltery = smeltery;

        GuiElement.defaultTexH = 256;
        GuiElement.defaultTexW = 256;
        slot = new GuiElementScalable(0, 166, 22, 18);
        slotEmpty = new GuiElementScalable(22, 166, 22, 18);
        yOffset = 0;
    }

    @Override
    protected boolean shouldDrawName() {
        return false;
    }

    @Override
    protected void updateSlots() {
        // adjust for the heat bar
        xOffset += 4;
        super.updateSlots();
        xOffset -= 4;
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
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        this.mc.getTextureManager()
            .bindTexture(SLOT_LOCATION);
        RenderHelper.disableStandardItemLighting();

        String tooltipText = null;

        // draw the "heat" bars for each slot
        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot.getHasStack() && shouldDrawSlot(slot)) {
                float progress = smeltery.getTemperature(slot.getSlotIndex());
                String tooltip = null;
                GuiElement bar = progressBar;

                if (Float.isNaN(progress)) {
                    progress = 1f;
                    bar = noMeltBar;
                    tooltip = "gui.smeltery.progress.no_recipe";
                } else if (smeltery.fuelReleaseTicks == 0) {
                    bar = unprogressBar;
                    progress = MathHelper.clamp_float(progress, 0, 1);
                    tooltip = "gui.smeltery.progress.no_fuel";
                } else if (progress < 0) {
                    bar = unprogressBar;
                    progress = 1f;
                    tooltip = "gui.smeltery.progress.no_heat";
                } else if ((progress > 1f && progress < 2f) || progress == Float.POSITIVE_INFINITY) {
                    progress = 1f;
                } else if (progress > 2f) {
                    bar = uberHeatBar;
                    progress = 1f;
                    tooltip = "gui.smeltery.progress.no_space";
                }

                int height = 1 + Math.round(progress * (bar.h - 1));
                int x = slot.xDisplayPosition - 10 + this.xSize;
                int y = slot.yDisplayPosition;

                if (tooltip != null && x + guiLeft <= mouseX
                    && x + guiLeft + bar.w > mouseX
                    && y + guiTop <= mouseY
                    && y + guiTop + bar.h > mouseY) {
                    tooltipText = tooltip;
                }

                drawTexturedModalRect(x, y + bar.h - height, 256, 256, bar.w, height);
            }
        }

        if (tooltipText != null) {
            drawHoveringText(
                this.fontRendererObj.listFormattedStringToWidth(TinkersRebornUtils.translate(tooltipText), 100),
                mouseX - guiLeft,
                mouseY - guiTop,
                this.fontRendererObj);
        }
    }

}
