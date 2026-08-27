package mctbl.tinkersreborn.smeltery.gui;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.gui.*;
import mctbl.tinkersreborn.library.gui.container.ContainerMultiModule;
import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.library.utils.IGuiLiquidTank;
import mctbl.tinkersreborn.smeltery.entity.FurnaceLogic;
import mctbl.tinkersreborn.smeltery.inventory.ContainerFurnace;
import mctbl.tinkersreborn.smeltery.inventory.ContainerSmeltery;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class GuiFurnace extends GuiHeatingStructureFuelTank implements INEIGuiHandler, IGuiLiquidTank {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/furnace.png");
    protected GuiElementScalable flame = new GuiElementScalable(176, 187, 14, 14, 256, 256);
    protected final int fuelStartX = 116;
    protected final int fuelStartY = 32;
    protected final int fuelWidth = 16;
    protected final int fuelHeight = 64;

    protected final int fuelProgressStartX = 117;
    protected final int fuelProgressStartY = 29;

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

        // furnace size changed || smeltery.getTank() == null
        if (furnace == null || furnace.getSizeInventory() != sideinventory.inventorySlots.inventorySlots.size()) {
            // close screen
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // we don't need to add the corner since the mouse is already reletive to the
        // corner
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        // draw the tooltips, if any
        // subtract the corner of the main module so the mouse location is relative to
        // just the center, rather than the side inventory
        mouseX -= cornerX;
        mouseY -= cornerY;

        // Fuel tooltips
        if (fuelStartX <= mouseX && mouseX < fuelStartX + fuelWidth
            && fuelStartY <= mouseY
            && mouseY < fuelStartY + fuelHeight) {
            this.drawFuelTooltip(mouseX, mouseY);
        }

    }

    @Override
    protected void drawPlayerInventoryName() {
        super.drawPlayerInventoryName();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BACKGROUND);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // update fuel info
        fuelInfo = furnace.getFuelDisplay();
        drawFuel(fuelStartX, fuelStartY, fuelWidth, fuelHeight);

        // draw the scala
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

    /**
     * NEI will give the specified item to the InventoryRange returned if the player's inventory is full. Should not
     * return null, just an empty list
     *
     * @param gui
     * @param item
     */
    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return Collections.emptyList();
    }

    /**
     * @param gui
     * @return A list of TaggedInventoryAreas that will be used with the savestates.
     */
    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
        return Collections.emptyList();
    }

    /**
     * Handles clicks while an itemstack has been dragged from the item panel. Use this to set configurable slots and
     * the like. Changes made to the stackSize of the dragged stack will be kept
     *
     * @param gui          The current gui instance
     * @param mousex       The x position of the mouse
     * @param mousey       The y position of the mouse
     * @param draggedStack The stack being dragged from the item panel
     * @param button       The button presed
     * @return True if the drag n drop was handled. False to resume processing through other routes. The held stack will
     * be deleted if draggedStack.stackSize == 0
     */
    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
        return false;
    }

    /**
     * Used to prevent the item panel from drawing on top of other gui elements. This function will also be called with
     * a 1x1 size rectangle on the mouse position for determining if the given coordinate should override item panel
     * functions such as scrolling
     *
     * @param gui
     * @param x   The x coordinate of the rectangle bounding the slot
     * @param y   The y coordinate of the rectangle bounding the slot
     * @param w   The w coordinate of the rectangle bounding the slot
     * @param h   The h coordinate of the rectangle bounding the slot
     * @return true if the item panel slot within the specified rectangle should not be rendered.
     */
    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        int guiXStart = guiLeft - sideinventory.xSize + 4;
        int guiXEnd = guiLeft + xSize - 4;
        int guiYStart = guiTop + 4;
        int guiYEnd = guiTop + ySize - 4;
        return x + w >= guiXStart && x <= guiXEnd && y + h >= guiYStart && y <= guiYEnd;
    }

    @Override
    public FluidStack getFluidStackAtPosition(int mouseX, int mouseY) {
        return null;
    }
}
