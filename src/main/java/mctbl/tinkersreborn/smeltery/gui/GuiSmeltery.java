package mctbl.tinkersreborn.smeltery.gui;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.gui.GuiElement;
import mctbl.tinkersreborn.library.gui.GuiHeatingStructureFuelTank;
import mctbl.tinkersreborn.library.gui.GuiSmelterySideInventory;
import mctbl.tinkersreborn.library.inventory.ContainerSideInventory;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.utils.IGuiLiquidTank;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.smeltery.inventory.ContainerSmeltery;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class GuiSmeltery extends GuiHeatingStructureFuelTank implements IGuiLiquidTank {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/smeltery.png");

    protected GuiElement scala = new GuiElement(176, 76, 52, 52, 256, 256);

    protected final GuiSmelterySideInventory sideinventory;
    protected final SmelteryLogic smeltery;

    public GuiSmeltery(ContainerSmeltery container, SmelteryLogic smeltery) {
        super(container);

        this.smeltery = smeltery;

        sideinventory = new GuiSmelterySideInventory(
            this,
            container.getSubContainer(ContainerSideInventory.class),
            smeltery,
            smeltery.getSizeInventory(),
            container.calcColumns());
        addModule(sideinventory);
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
        List<String> tooltip = getTankTooltip(smeltery, mouseX, mouseY, 8, 16, 60, 68);
        if (tooltip != null) {
            this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRendererObj);
        }

        // Fuel tooltips
        if (71 <= mouseX && mouseX < 83 && 16 <= mouseY && mouseY < 68) {
            drawFuelTooltip(mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(BACKGROUND);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // draw liquids
        drawGuiTank(smeltery, 8 + cornerX, 16 + cornerY, scala.w, scala.h, this.zLevel);

        // update fuel info
        fuelInfo = smeltery.getFuelDisplay();
        drawFuel(71, 16, 12, 52);

        // draw the scala
        this.mc.getTextureManager()
            .bindTexture(BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        scala.draw(8 + cornerX, 16 + cornerY);
    }

    private void drawGuiTank(SmelteryLogic liquids, int x, int y, int w, int height, float zLevel) {
        // draw liquids
        if (liquids != null && liquids.getFluidAmount() > 0) {
            int capacity = Math.max(liquids.getFluidAmount(), liquids.getCapacity());
            int[] heights = calcLiquidHeights(liquids.moltenMetal, capacity, height);

            int bottom = y + w;
            for (int i = 0; i < heights.length; i++) {
                int h = heights[i];
                // FluidStack liquid = liquids.getFluid(i);
                // drawFluidIcon(x, bottom - h, w, h, zLevel, liquid);
                //
                // bottom -= h;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            handleTankClick(smeltery, mouseX - cornerX, mouseY - cornerY, 8, 16, 60, 68);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void handleTankClick(SmelteryLogic tank, int mouseX, int mouseY, int xmin, int ymin, int xmax, int ymax) {
        getFluidStackIndexAtPosition(tank, mouseX, mouseY, xmin, ymin, xmax, ymax);
        // .ifPresent(
        // i -> TinkerNetwork.sendToServer(new SmelteryFluidClicked(i))
        // );
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
            int[] heights = calcLiquidHeights(tank.moltenMetal, tank.getCapacity(), ymax - ymin);
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

    private List<String> getTankTooltip(SmelteryLogic tank, int mouseX, int mouseY, int xmin, int ymin, int xmax,
        int ymax) {

        // Liquids
        if (xmin <= mouseX && mouseX < xmax && ymin <= mouseY && mouseY < ymax) {
            FluidStack hovered = getFluidHovered(tank, ymax - mouseY - 1, ymax - ymin);
            List<String> text = Lists.newArrayList();

            Consumer<Integer> stringFn = TinkersRebornUtils.isShiftKeyDown() ? (i) -> amountToString(i, text)
                : (i) -> amountToIngotString(i, text);

            if (hovered == null) {
                int usedCap = tank.getFluidAmount();
                int maxCap = tank.getCapacity();
                text.add(EnumChatFormatting.WHITE + TinkersRebornUtils.translate("gui.smeltery.capacity"));
                stringFn.accept(maxCap);
                text.add(TinkersRebornUtils.translate("gui.smeltery.capacity_available"));
                stringFn.accept(maxCap - usedCap);
                text.add(TinkersRebornUtils.translate("gui.smeltery.capacity_used"));
                stringFn.accept(usedCap);
                if (!TinkersRebornUtils.isShiftKeyDown()) {
                    text.add("");
                    text.add(TinkersRebornUtils.translate("tooltip.tank.holdShift"));
                }
            } else {
                text.add(EnumChatFormatting.WHITE + hovered.getLocalizedName());
                liquidToString(hovered, text);
            }

            return text;
        }

        return null;
    }

    private FluidStack getFluidHovered(SmelteryLogic tank, int y, int height) {
        int[] heights = calcLiquidHeights(tank.moltenMetal, tank.getCapacity(), height);

        // for (int i = 0; i < heights.length; i++) {
        // if (y < heights[i]) {
        // return tank.getFluid(i);
        // }
        // y -= heights[i];
        // }

        return null;
    }

    /**
     * calculate the rendering heights for all the liquids
     *
     * @param liquids  The liquids
     * @param capacity Max capacity of smeltery, to calculate how much height one
     *                 liquid takes up
     * @param height   Maximum height, basically represents how much height full
     *                 capacity is
     * @param min      Minimum amount of height for a fluid. A fluid can never have
     *                 less than this value height returned
     * @return Array with heights corresponding to input-list liquids
     */
    private int[] calcLiquidHeights(List<FluidStack> liquids, int capacity, int height) {
        int fluidHeights[] = new int[liquids.size()];

        int totalFluidAmount = 0;
        int min = 3;

        if (liquids.size() > 0) {

            for (int i = 0; i < liquids.size(); i++) {
                FluidStack liquid = liquids.get(i);

                float h = (float) liquid.amount / (float) capacity;
                totalFluidAmount += liquid.amount;
                fluidHeights[i] = Math.max(min, (int) Math.ceil(h * (float) height));
            }

            // if not completely full, leave a few pixels for the empty tank display
            if (totalFluidAmount < capacity) {
                height -= min;
            }

            // check if we have enough height to render everything, if not remove pixels
            // from the tallest liquid
            int sum;
            do {
                sum = 0;
                int biggest = -1;
                int m = 0;
                for (int i = 0; i < fluidHeights.length; i++) {
                    sum += fluidHeights[i];
                    if (fluidHeights[i] > biggest) {
                        biggest = fluidHeights[i];
                        m = i;
                    }
                }

                // we can't get a result without going negative
                if (fluidHeights[m] == 0) {
                    break;
                }

                // remove a pixel from the biggest one
                if (sum > height) {
                    fluidHeights[m]--;
                }
            } while (sum > height);
        }

        return fluidHeights;
    }

    /**
     * Adds information to the tooltip based on the fluid amount
     * 
     * @param amount Fluid amount
     * @param text   Text to add information to.
     */
    public void amountToString(int amount, List<String> text) {
        amount = calcLiquidText(
            amount,
            1000000,
            String.format(TinkersRebornUtils.translate("gui.smeltery.liquid.kilobucket")),
            text);
        amount = calcLiquidText(
            amount,
            1000,
            String.format(TinkersRebornUtils.translate("gui.smeltery.liquid.bucket")),
            text);
        calcLiquidText(amount, 1, String.format(TinkersRebornUtils.translate("gui.smeltery.liquid.millibucket")), text);
    }

    public void amountToIngotString(int amount, List<String> text) {
        amount = calcLiquidText(
            amount,
            TinkersRebornMaterial.VALUE_Ingot,
            TinkersRebornUtils.translate("gui.smeltery.liquid.ingot"),
            text);
        amountToString(amount, text);
    }

    public int calcLiquidText(int amount, int divider, String unit, List<String> text) {
        int full = amount / divider;
        if (full > 0) {
            text.add(String.format("%s%d %s", EnumChatFormatting.GRAY, full, unit));
        }

        return amount % divider;
    }

    /**
     * Adds information for the tooltip based on the fluid stacks size.
     *
     * @param fluid Input fluid stack
     * @param text  Text to add information to.
     */
    public void liquidToString(FluidStack fluid, List<String> text) {
        int amount = fluid.amount;

        if (!TinkersRebornUtils.isShiftKeyDown()) {
            // List<FluidGuiEntry> entries = fluidGui.get(fluid.getFluid());
            // if(entries == null) {
            // entries = calcFluidGuiEntries(fluid.getFluid());
            // fluidGui.put(fluid.getFluid(), entries);
            // }
            //
            // for(FluidGuiEntry entry : entries) {
            // amount = calcLiquidText(amount, entry.amount, entry.getText(), text);
            // }
        }

        // standard display stuff: bucket amounts
        amountToString(amount, text);
    }
}
