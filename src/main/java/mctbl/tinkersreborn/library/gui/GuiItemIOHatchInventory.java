package mctbl.tinkersreborn.library.gui;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import mctbl.tinkersreborn.smeltery.entity.ItemIOHatchLogic;
import mctbl.tinkersreborn.smeltery.gui.GuiFurnace;

public class GuiItemIOHatchInventory extends GuiSideInventory {

    public static final ResourceLocation SLOT_LOCATION = GuiFurnace.BACKGROUND;
    private final ItemIOHatchLogic itemIOHatchLogic;

    public GuiItemIOHatchInventory(GuiMultiModule parent, Container container, ItemIOHatchLogic logic, int slotCount,
        int columns) {
        super(parent, container, slotCount, columns);
        GuiElement.defaultTexH = 256;
        GuiElement.defaultTexW = 256;
        itemIOHatchLogic = logic;
        slot = new GuiElementScalable(0, 197, 22, 18);
        slotEmpty = new GuiElementScalable(22, 197, 22, 18);
        yOffset = 0;
    }

    @Override
    protected boolean shouldDrawName() {
        return false;
    }
}
