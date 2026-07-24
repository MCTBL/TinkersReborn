package mctbl.tinkersreborn.library.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.gui.GuiGeneric;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.GuiPartBuilder;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;

public class GuiDynButtons extends GuiModule {

    // Graphic Resources
    protected static final GuiElement sliderNormal = GuiGeneric.sliderNormal;
    protected static final GuiElement sliderLow = GuiGeneric.sliderLow;
    protected static final GuiElement sliderHigh = GuiGeneric.sliderHigh;
    protected static final GuiElement sliderTop = GuiGeneric.sliderTop;
    protected static final GuiElement sliderBottom = GuiGeneric.sliderBottom;
    protected static final GuiElementScalable sliderBackground = GuiGeneric.sliderBackground;

    private static final ResourceLocation BACKGROUND = new ResourceLocation(
        TinkersReborn.MODID,
        "textures/gui/partbuilder.png");

    private static final GuiElement ButtonBackGroundNormal = new GuiElement(177, 16, 18, 18, 256, 256);
    private static final GuiElement ButtonBackGroundPredded = new GuiElement(177, 34, 18, 18, 256, 256);
    private static final GuiElement ButtonBackGroundHover = new GuiElement(177, 52, 18, 18, 256, 256);

    protected GuiWidgetSlider slider = new GuiWidgetSlider(
        sliderNormal,
        sliderHigh,
        sliderLow,
        sliderTop,
        sliderBottom,
        sliderBackground);

    // Logic
    protected int columns; // columns displayed
    protected int rows; // rows displayed
    protected int buttonCount;
    protected boolean sliderActive;

    protected int firstButtonId;
    protected int lastButtonId;
    protected List<GuiButtonItem<?>> buttonList;

    private GuiButtonItem<?> clickedButton;

    public GuiDynButtons(GuiMultiModule parent) {
        super(parent, null, false, false);

        this.xOffset = 52;
        this.yOffset = 17;
        this.xSize = 87;
        this.ySize = 54;

        this.buttonList = new ArrayList<>();

        List<TinkersRebornToolPart> list = TinkersRebornTools.patternAndCast.getAllPatternType()
            .stream()
            .map(TinkersRebornRegistry::getToolPartByPartName)
            .collect(Collectors.toList());
        for (int idx = 0; idx < list.size(); idx++) {
            TinkersRebornToolPart toolPart = list.get(idx);
            if (toolPart == TinkersRebornTools.boltCore || toolPart == TinkersRebornTools.shard) {
                // huh
                continue;
            }
            this.buttonList.add(
                new GuiButtonItem<TinkersRebornToolPart>(
                    this.buttonList.size(),
                    0,
                    0,
                    new ItemStack(toolPart),
                    toolPart,
                    parent).setGraphics(
                        ButtonBackGroundNormal,
                        ButtonBackGroundHover,
                        ButtonBackGroundPredded,
                        BACKGROUND));
        }
        this.resetButtonsVisible();
        this.buttonCount = buttonList.size();
        this.firstButtonId = 0;
        this.lastButtonId = this.buttonCount;
    }

    public void resetButtonsVisible(TinkersRebornToolPart part) {
        for (GuiButtonItem<?> b : this.buttonList) {
            b.visible = b.data.equals(part);
        }
    }

    public void resetButtonsVisible() {
        for (GuiButtonItem<?> b : this.buttonList) {
            b.visible = true;
        }
    }

    @Override
    public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
        this.guiLeft = parentX + xOffset;
        this.guiTop = parentY + yOffset;

        // calculate rows and columns from space
        columns = xSize / ButtonBackGroundNormal.w;
        rows = ySize / ButtonBackGroundNormal.h;

        sliderActive = buttonCount > columns * rows;

        updateSlider();

        // recalculate columns with slider
        if (sliderActive) {
            columns = (xSize - slider.width) / ButtonBackGroundNormal.w;
            updateSlider();
        }

        updateButtons();
    }

    protected void updateSlider() {
        int max = 0;
        if (sliderActive) {
            slider.show();
            max = buttonCount / columns - rows + 1; // the assumption here is that for an active slider this always is
            // >0
        } else {
            slider.hide();
        }

        slider.setPosition(guiLeft + xSize - slider.width, guiTop);
        slider.setSize(ySize);
        slider.setSliderParameters(0, max, 1);
    }

    public void update(int mouseX, int mouseY) {
        if (!sliderActive) {
            return;
        }

        slider.update(mouseX, mouseY, isMouseInModule(mouseX, mouseY));
        updateButtons();
    }

    public boolean shouldDrawButon(GuiButtonItem<?> button) {
        return (firstButtonId <= button.id && lastButtonId > button.id);
    }

    // updates slot visibility
    public void updateButtons() {
        // calculate displayed slots
        firstButtonId = slider.getValue() * columns;
        lastButtonId = Math.min(buttonCount, firstButtonId + rows * columns);

        for (GuiButtonItem<?> b : this.buttonList) {
            if (shouldDrawButon(b)) {
                int offset = b.id - firstButtonId;
                int x = (offset % columns) * ButtonBackGroundNormal.w;
                int y = (offset / columns) * ButtonBackGroundNormal.h;

                b.xPosition = this.guiLeft + x;
                b.yPosition = this.guiTop + y;
            } else {
                b.xPosition = 0;
                b.yPosition = 0;
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button instanceof GuiButtonItem<?>gbi && gbi.data instanceof TinkersRebornToolPart toolPart
            && parent instanceof GuiPartBuilder builder) {
            builder.onPartSelection(toolPart);
        }
        super.actionPerformed(button);
    }

    @Override
    public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            // for (GuiButtonItem<?> guibutton : this.buttonList) {
            // or just loop button inside render, but will casue some other issue
            for (int idx = this.firstButtonId; idx < this.lastButtonId && idx < this.buttonList.size(); idx++) {
                GuiButtonItem<?> guibutton = this.buttonList.get(idx);
                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    this.clickedButton = guibutton;
                    guibutton.func_146113_a(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleMouseReleased(int mouseX, int mouseY, int state) {
        if (clickedButton != null) {
            clickedButton.mouseReleased(mouseX, mouseY);
            clickedButton = null;
            return true;
        }
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager()
            .bindTexture(GuiGeneric.LOCATION);
        if (!slider.isHidden()) {
            slider.draw();
        }

        for (int idx = this.firstButtonId; idx < this.lastButtonId && idx < this.buttonList.size(); idx++) {
            GuiButtonItem<?> button = this.buttonList.get(idx);
            button.pressed = this.clickedButton == button;
            button.drawButton(this.mc, mouseX, mouseY);
        }
    }

    public int getButtonCount() {
        return buttonCount;
    }
}
