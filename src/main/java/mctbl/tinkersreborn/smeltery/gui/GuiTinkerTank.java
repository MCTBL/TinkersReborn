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
import mctbl.tinkersreborn.library.gui.GuiHeatingStructureFuelTank;
import mctbl.tinkersreborn.library.gui.GuiMultiModule;
import mctbl.tinkersreborn.library.utils.IGuiLiquidTank;
import mctbl.tinkersreborn.smeltery.entity.TinkerTankLogic;
import mctbl.tinkersreborn.smeltery.inventory.ContainerTinkerTank;
import mctbl.tinkersreborn.smeltery.network.TinkerTankButtonClicked;
import mctbl.tinkersreborn.smeltery.network.TinkerTankFluidClicked;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

@SideOnly(Side.CLIENT)
@Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class GuiTinkerTank extends GuiMultiModule implements INEIGuiHandler, IGuiLiquidTank {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/tank.png");
    protected GuiElement scala = new GuiElement(176, 107, 80, 80, 256, 256);

    protected final int fluidStartX = 8;
    protected final int fluidStartY = 16;
    protected final int fluidWidth = 80;
    protected final int fluidHeight = 80;

    protected final TinkerTankLogic tank;
    protected GuiButtonItem<ItemStack> fillButton;

    public GuiTinkerTank(ContainerTinkerTank container, TinkerTankLogic tank) {
        super(container);
        this.tank = tank;

        this.ySize = 197;
    }

    // this is the same for both structures, but the superclass does not have (nor
    // need) access to the side inventory
    @Override
    public void updateScreen() {
        super.updateScreen();

        if (tank == null) {
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
            tank,
            mouseX,
            mouseY,
            fluidStartX,
            fluidStartY,
            fluidStartX + fluidWidth,
            fluidStartY + fluidHeight);
        if (tooltip != null) {
            this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRendererObj);
        }

        if (fillButton.func_146115_a()) {
            this.drawHoveringText(
                Arrays.asList(TinkersStr.smtleteryFillOrClear.toString()),
                mouseX,
                mouseY,
                this.fontRendererObj);
        }
    }

    @Nullable
    private List<String> getTankTooltip(TinkerTankLogic tank, int mouseX, int mouseY, int xmin, int ymin, int xmax,
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

    private FluidStack getFluidHovered(TinkerTankLogic tank, int y, int height) {
        int[] heights = TinkersRebornUtils.calcLiquidHeights(tank.fluids, tank.getCapacity(), height);

        for (int i = 0; i < heights.length; i++) {
            if (y < heights[i]) {
                return tank.fluids.get(i);
            }
            y -= heights[i];
        }

        return null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BACKGROUND);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // draw liquids
        drawGuiTank(tank, fluidStartX + cornerX, fluidStartY + cornerY, scala.w, scala.h, this.zLevel);

        // draw the scala
        this.mc.getTextureManager()
            .bindTexture(BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        scala.draw(fluidStartX + cornerX, fluidStartY + cornerY);
        fillButton.drawButton(mc, mouseX, mouseY);
    }

    private void drawGuiTank(TinkerTankLogic tank, int x, int y, int w, int height, float zLevel) {
        // draw liquids
        if (tank != null && tank.getFluidAmount() > 0) {
            int capacity = Math.max(tank.getFluidAmount(), tank.getCapacity());
            int[] heights = TinkersRebornUtils.calcLiquidHeights(tank.fluids, capacity, height);

            int bottom = y + w;
            for (int i = 0; i < heights.length; i++) {
                int h = heights[i];
                FluidStack liquid = tank.fluids.get(i);
                GuiHeatingStructureFuelTank.drawFluidIcon(x, bottom - h, w, h, zLevel, liquid);

                bottom -= h;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            handleTankClick(
                tank,
                mouseX - cornerX,
                mouseY - cornerY,
                fluidStartX,
                fluidStartY,
                fluidStartX + fluidWidth,
                fluidStartY + fluidHeight);
        }
        if (this.fillButton.func_146115_a()) {
            this.fillButton.func_146113_a(this.mc.getSoundHandler());
            TinkerNetwork.sendToServer(new TinkerTankButtonClicked(isShiftKeyDown()));
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void handleTankClick(TinkerTankLogic tank, int mouseX, int mouseY, int xmin, int ymin, int xmax, int ymax) {
        getFluidStackIndexAtPosition(tank, mouseX, mouseY, xmin, ymin, xmax, ymax)
            .ifPresent(i -> TinkerNetwork.sendToServer(new TinkerTankFluidClicked(i)));
    }

    public Optional<Integer> getFluidStackIndexAtPosition(TinkerTankLogic tank, int mouseX, int mouseY, int xmin,
        int ymin, int xmax, int ymax) {
        if (xmin <= mouseX && mouseX < xmax && ymin <= mouseY && mouseY < ymax) {
            int[] heights = TinkersRebornUtils.calcLiquidHeights(tank.fluids, tank.getCapacity(), ymax - ymin);
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

    @Override
    public FluidStack getFluidStackAtPosition(int mouseX, int mouseY) {
        return getFluidStackIndexAtPosition(tank, mouseX - cornerX, mouseY - cornerY, 8, 16, 60, 68)
            .map(tank.fluids::get)
            .orElse(null);
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
        int guiXStart = guiLeft + 4;
        int guiXEnd = guiLeft + xSize - 4;
        int guiYStart = guiTop + 4;
        int guiYEnd = guiTop + ySize - 4;
        return x + w >= guiXStart && x <= guiXEnd && y + h >= guiYStart && y <= guiYEnd;
    }
}
