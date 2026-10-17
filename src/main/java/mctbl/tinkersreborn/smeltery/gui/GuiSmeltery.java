package mctbl.tinkersreborn.smeltery.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.gui.GuiButtonItem;
import mctbl.tinkersreborn.library.gui.GuiElement;
import mctbl.tinkersreborn.library.gui.GuiElementScalable;
import mctbl.tinkersreborn.library.gui.GuiHeatingStructureFuelTank;
import mctbl.tinkersreborn.library.gui.GuiMultiBlockSideInventory;
import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.library.utils.IGuiLiquidTank;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.smeltery.inventory.ContainerSmeltery;
import mctbl.tinkersreborn.smeltery.network.SmelteryButtonClicked;
import mctbl.tinkersreborn.smeltery.network.SmelteryFluidClicked;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

@SideOnly(Side.CLIENT)
@Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class GuiSmeltery extends GuiHeatingStructureFuelTank implements INEIGuiHandler, IGuiLiquidTank {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/smeltery.png");

    protected GuiElement scala = new GuiElement(176, 107, 80, 80, 256, 256);
    protected GuiElementScalable flame = new GuiElementScalable(176, 187, 14, 14, 256, 256);
    protected final int fuelStartX = 116;
    protected final int fuelStartY = 32;
    protected final int fuelWidth = 16;
    protected final int fuelHeight = 64;

    protected final int fluidStartX = 8;
    protected final int fluidStartY = 16;
    protected final int fluidWidth = 80;
    protected final int fluidHeight = 80;

    protected final int fuelProgressStartX = 117;
    protected final int fuelProgressStartY = 29;

    protected final GuiMultiBlockSideInventory sideinventory;
    protected final SmelteryLogic smeltery;
    protected GuiButtonItem<ItemStack> fillButton;

    public GuiSmeltery(ContainerSmeltery container, SmelteryLogic smeltery) {
        super(container);

        this.smeltery = smeltery;

        sideinventory = new GuiMultiBlockSideInventory(
            this,
            container.getSubContainer(ContainerSideInventory.class),
            smeltery,
            smeltery.getSizeInventory(),
            container.calcColumns());
        addModule(sideinventory);

        this.ySize = 197;
    }

    // this is the same for both structures, but the superclass does not have (nor
    // need) access to the side inventory
    @Override
    public void updateScreen() {
        super.updateScreen();

        // smeltery size changed || smeltery.getTank() == null
        if (smeltery == null || smeltery.getSizeInventory() != sideinventory.inventorySlots.inventorySlots.size()) {
            // close screen
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.fillButton = new GuiButtonItem<ItemStack>(
            0,
            93 + cornerX,
            46 + cornerY,
            new ItemStack(Items.bucket),
            null,
            this);
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

        // Liquids
        List<String> tooltip = getTankTooltip(
            smeltery,
            mouseX,
            mouseY,
            fluidStartX,
            fluidStartY,
            fluidStartX + fluidWidth,
            fluidStartY + fluidHeight);
        if (tooltip != null) {
            this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRendererObj);
        }

        // Fuel tooltips
        if (fuelStartX <= mouseX && mouseX < fuelStartX + fuelWidth
            && fuelStartY <= mouseY
            && mouseY < fuelStartY + fuelHeight) {
            this.drawFuelTooltip(mouseX, mouseY);
        }

        if (fillButton.func_146115_a()) {
            this.drawHoveringText(
                Arrays.asList(TinkersStr.smtleteryFillOrClear.toString()),
                mouseX,
                mouseY,
                this.fontRendererObj);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BACKGROUND);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // draw liquids
        drawGuiTank(smeltery, fluidStartX + cornerX, fluidStartY + cornerY, scala.w, scala.h, this.zLevel);

        // update fuel info
        fuelInfo = smeltery.getFuelDisplay();
        drawFuel(fuelStartX, fuelStartY, fuelWidth, fuelHeight);

        // draw the scala
        this.mc.getTextureManager()
            .bindTexture(BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int h = (int) ((flame.h + 1) * (smeltery.fuelReleaseTicks * 1.0F / Math.max(smeltery.fuelTotalTicks, 1)));
        flame.drawScaledYReverse(fuelProgressStartX + cornerX, fuelProgressStartY + cornerY, h);
        scala.draw(fluidStartX + cornerX, fluidStartY + cornerY);
        fillButton.drawButton(mc, mouseX, mouseY);
    }

    private void drawGuiTank(SmelteryLogic liquids, int x, int y, int w, int height, float zLevel) {
        // draw liquids
        if (liquids != null && liquids.getFluidAmount() > 0) {
            int capacity = Math.max(liquids.getFluidAmount(), liquids.getCapacity());
            int[] heights = TinkersRebornUtils.calcLiquidHeights(liquids.moltenMetal, capacity, height);

            int bottom = y + w;
            for (int i = 0; i < heights.length; i++) {
                int h = heights[i];
                FluidStack liquid = liquids.moltenMetal.get(i);
                drawFluidIcon(x, bottom - h, w, h, zLevel, liquid);

                bottom -= h;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            handleTankClick(
                smeltery,
                mouseX - cornerX,
                mouseY - cornerY,
                fluidStartX,
                fluidStartY,
                fluidStartX + fluidWidth,
                fluidStartY + fluidHeight);
        }
        if (this.fillButton.func_146115_a()) {
            this.fillButton.func_146113_a(this.mc.getSoundHandler());
            TinkerNetwork.sendToServer(new SmelteryButtonClicked(isShiftKeyDown()));
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void handleTankClick(SmelteryLogic tank, int mouseX, int mouseY, int xmin, int ymin, int xmax, int ymax) {
        getFluidStackIndexAtPosition(tank, mouseX, mouseY, xmin, ymin, xmax, ymax)
            .ifPresent(i -> TinkerNetwork.sendToServer(new SmelteryFluidClicked(i)));
    }

    @Override
    public FluidStack getFluidStackAtPosition(int mouseX, int mouseY) {
        return getFluidStackIndexAtPosition(smeltery, mouseX - cornerX, mouseY - cornerY, 8, 16, 60, 68)
            .map(smeltery.moltenMetal::get)
            .orElse(null);
    }

    public Optional<Integer> getFluidStackIndexAtPosition(SmelteryLogic tank, int mouseX, int mouseY, int xmin,
        int ymin, int xmax, int ymax) {
        if (xmin <= mouseX && mouseX < xmax && ymin <= mouseY && mouseY < ymax) {
            int[] heights = TinkersRebornUtils.calcLiquidHeights(tank.moltenMetal, tank.getCapacity(), ymax - ymin);
            int y = ymax - mouseY - 1;

            for (int i = 0; i < heights.length; i++) {
                if (y < heights[i]) {
                    return Optional.of(i);
                }
                y -= heights[i];
            }
        }

        return Optional.empty();
    }

    @Nullable
    private List<String> getTankTooltip(SmelteryLogic tank, int mouseX, int mouseY, int xmin, int ymin, int xmax,
        int ymax) {

        // Liquids
        if (xmin <= mouseX && mouseX < xmax && ymin <= mouseY && mouseY < ymax) {
            FluidStack hovered = getFluidHovered(tank, ymax - mouseY - 1, ymax - ymin);
            List<String> text = Lists.newArrayList();

            Consumer<Integer> stringFn = TinkersRebornUtils.isShiftKeyDown()
                ? i -> TinkersRebornUtils.amountToString(i, text)
                : i -> TinkersRebornUtils.amountToIngotString(i, text);

            if (hovered == null) {
                int usedCap = tank.getFluidAmount();
                int maxCap = tank.getCapacity();
                text.add(EnumChatFormatting.WHITE + TinkersStr.smtleteryCapacity.toString());
                stringFn.accept(maxCap);
                text.add(TinkersStr.smtleteryCapacityAvailable.toString());
                stringFn.accept(maxCap - usedCap);
                text.add(TinkersStr.smtleteryCapacityUsed.toString());
                stringFn.accept(usedCap);
                if (!TinkersRebornUtils.isShiftKeyDown()) {
                    text.add("");
                    text.add(TinkersStr.holdShift.toString());
                }
            } else {
                text.add(EnumChatFormatting.WHITE + hovered.getLocalizedName());
                TinkersRebornUtils.liquidToString(hovered, text);
            }

            return text;
        }

        return null;
    }

    private FluidStack getFluidHovered(SmelteryLogic tank, int y, int height) {
        int[] heights = TinkersRebornUtils.calcLiquidHeights(tank.moltenMetal, tank.getCapacity(), height);

        for (int i = 0; i < heights.length; i++) {
            if (y < heights[i]) {
                return tank.moltenMetal.get(i);
            }
            y -= heights[i];
        }

        return null;
    }

    // NEI
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
