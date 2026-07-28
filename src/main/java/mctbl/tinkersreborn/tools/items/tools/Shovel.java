package mctbl.tinkersreborn.tools.items.tools;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.tools.HarvestTool;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;

public class Shovel extends HarvestTool {

    public Shovel() {
        super("Shovel", 3);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("shovel", 0);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.shovelHead, MaterialStatusType.HEAD, "_shovel_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_shovel_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.binding, MaterialStatusType.EXTRA, "_shovel_accessory"));

    }

    @Override
    public boolean isEffective(Block block) {
        return shovelEffectiveMaterials.contains(block.getMaterial()) || shovelEffectiveBlocks.contains(block);
    }

    @Override
    public float damagePotential() {
        return 0.9F;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 + 18, 45 - 18) // shovel head
                .addSlotPosition(33, 45) // rod
                .addSlotPosition(33 - 18, 45 + 18); // binding
        }
        return this.toolBuildGuiInfo;
    }
}
