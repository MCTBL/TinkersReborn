package mctbl.tinkersreborn.smeltery.gui;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.gui.GuiElementScalable;
import mctbl.tinkersreborn.library.gui.GuiFurnaceSideInventory;
import mctbl.tinkersreborn.library.gui.GuiHeatingStructureFuelTank;
import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.smeltery.entity.FurnaceLogic;
import mctbl.tinkersreborn.smeltery.inventory.ContainerFurnace;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class GuiFurnace extends GuiHeatingStructureFuelTank implements INEIGuiHandler {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/furnace.png");
    protected GuiElementScalable flame = new GuiElementScalable(176, 173, 28, 28, 256, 256);
    protected final int fuelStartX = 116;
    protected final int fuelStartY = 32;
    protected final int fuelWidth = 16;
    protected final int fuelHeight = 64;

    protected final int fuelProgressStartX = 50;
    protected final int fuelProgressStartY = 97;

    protected final GuiFurnaceSideInventory sideinventory;
    protected final FurnaceLogic furnace;

    public GuiFurnace(ContainerFurnace container, FurnaceLogic furnace) {
        super(container);
        this.sideinventory = new GuiFurnaceSideInventory(
            this,
            container.getSubContainer(ContainerSideInventory.class),
            furnace,
            furnace.getSizeInventory(),
            4);
        this.furnace = furnace;
        addModule(sideinventory);

        this.ySize = 197;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (furnace == null || furnace.getSizeInventory() != sideinventory.inventorySlots.inventorySlots.size()) {
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        mouseX -= cornerX;
        mouseY -= cornerY;

        if (fuelStartX <= mouseX && mouseX < fuelStartX + fuelWidth
            && fuelStartY <= mouseY
            && mouseY < fuelStartY + fuelHeight) {
            this.drawFuelTooltip(mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BACKGROUND);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        fuelInfo = furnace.getFuelDisplay();
        drawFuel(fuelStartX, fuelStartY, fuelWidth, fuelHeight);

        this.mc.getTextureManager()
            .bindTexture(BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int h = (int) ((flame.h + 1) * (furnace.fuelReleaseTicks * 1.0F / Math.max(furnace.fuelTotalTicks, 1)));
        flame.drawScaledYReverse(fuelProgressStartX + cornerX, fuelProgressStartY + cornerY, h);
    }

    @Override
    public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
        return currentVisibility;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return Collections.emptyList();
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        int guiXStart = guiLeft - sideinventory.xSize + 4;
        int guiXEnd = guiLeft + xSize - 4;
        int guiYStart = guiTop + 4;
        int guiYEnd = guiTop + ySize - 4;
        return x + w >= guiXStart && x <= guiXEnd && y + h >= guiYStart && y <= guiYEnd;
    }
}
