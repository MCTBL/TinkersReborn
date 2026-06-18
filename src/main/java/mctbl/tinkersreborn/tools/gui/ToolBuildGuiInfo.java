package mctbl.tinkersreborn.tools.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import org.lwjgl.util.Point;

import mctbl.tinkersreborn.library.tools.ToolCore;

/**
 * description the position of slot in tool station
 */
public class ToolBuildGuiInfo {

    @Nonnull
    public final ItemStack tool;
    // the positions where the slots are located
    public final List<Point> positions = new ArrayList<>();

    public static ToolBuildGuiInfo repairInfo;

    static {
        int x = 7 + 80 / 2 - 8 - 6;
        int y = 18 + 64 / 2 - 8;

        repairInfo = new ToolBuildGuiInfo();

        repairInfo.addSlotPosition(x, y);

        repairInfo.addSlotPosition(x - 18, y + 20); // -20,+20
        repairInfo.addSlotPosition(x - 22, y - 5); // -22, -7
        repairInfo.addSlotPosition(x, y - 23); // +-0, -21
        repairInfo.addSlotPosition(x + 22, y - 5); // +22, -7
        repairInfo.addSlotPosition(x + 18, y + 20); // +20,+20
    }

    public ToolBuildGuiInfo() {
        // for repairing
        this.tool = null;
    }

    public ToolBuildGuiInfo(@Nonnull ToolCore tool) {
        this.tool = tool.getToolForRender();
    }

    public static ToolBuildGuiInfo default3Part(@Nonnull ToolCore tool) {
        ToolBuildGuiInfo info = new ToolBuildGuiInfo(tool);
        info.addSlotPosition(33 - 20, 42 + 20);
        info.addSlotPosition(33 + 20, 42 - 20);
        info.addSlotPosition(33, 42);
        return info;
    }

    /**
     * Add another slot at the specified position for the tool. The positions are
     * usually located between: X: 7 - 69 Y: 18 - 64
     */
    public ToolBuildGuiInfo addSlotPosition(int x, int y) {
        positions.add(new Point(x, y));
        return this;
    }

}
