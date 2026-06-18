package mctbl.tinkersreborn.tools.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.gui.GuiButtonItem;
import mctbl.tinkersreborn.library.gui.GuiSideButtons;
import mctbl.tinkersreborn.library.gui.Icons;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class GuiButtonsToolStation extends GuiSideButtons {

    protected final GuiToolStation parent;

    public GuiButtonsToolStation(GuiToolStation parent, Container container) {
        super(parent, container, 5);

        this.parent = parent;
    }

    protected int selected = 0;

    private int style = 0;

    @Override
    public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
        super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

        int index = 0;
        buttonCount = 0;

        {
            GuiButtonItem<ToolBuildGuiInfo> button = new GuiButtonItem<>(
                index++,
                -1,
                -1,
                TinkersRebornUtils.translate("gui.repair"),
                ToolBuildGuiInfo.repairInfo,
                parent);
            shiftButton(button, 0, -18 * style);
            addSideButton(button);
        }

        for (ToolCore item : parent.getBuildableItems()) {
            ToolBuildGuiInfo info = item.getToolBuildGuiInfo();
            if (info != null) {
                GuiButtonItem<ToolBuildGuiInfo> button = new GuiButtonItem<>(index++, -1, -1, info.tool, info, parent);
                shiftButton(button, 0, -18 * style);
                addSideButton(button);

                if (index - 1 == selected) {
                    button.pressed = true;
                }
            }
        }

        super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

        // activate currently selected/default
        parent.updateGUI();
    }

    public void setSelectedButtonByTool(ItemStack stack) {
        for (Object o : buttonList) {
            if (o instanceof GuiButtonItem) {
                @SuppressWarnings("unchecked")
                GuiButtonItem<ToolBuildGuiInfo> btn = (GuiButtonItem<ToolBuildGuiInfo>) o;
                btn.pressed = ItemStack.areItemStacksEqual(btn.data.tool, stack);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof GuiButtonItem && ((GuiButtonItem<ToolBuildGuiInfo>) button).data.tool != null) {
            return;
        }

        for (Object o : buttonList) {
            if (o instanceof GuiButtonItem) {
                ((GuiButtonItem<ToolBuildGuiInfo>) o).pressed = false;
            }
        }
        if (button instanceof GuiButtonItem b) {
            b.pressed = true;
            selected = button.id;

            parent.onToolSelection(((GuiButtonItem<ToolBuildGuiInfo>) b).data);
        }
    }

    @SuppressWarnings("unchecked")
    public void wood() {
        for (Object o : buttonList) {
            shiftButton((GuiButtonItem<ToolBuildGuiInfo>) o, 0, -36);
        }

        style = 2;
    }

    @SuppressWarnings("unchecked")
    public void metal() {
        for (Object o : buttonList) {
            shiftButton((GuiButtonItem<ToolBuildGuiInfo>) o, 0, -18);
        }

        style = 1;
    }

    protected void shiftButton(GuiButtonItem<ToolBuildGuiInfo> button, int xd, int yd) {
        button.setGraphics(
            Icons.ICON_Button.shift(xd, yd),
            Icons.ICON_ButtonHover.shift(xd, yd),
            Icons.ICON_ButtonPressed.shift(xd, yd),
            Icons.ICON);
    }

    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }
}
