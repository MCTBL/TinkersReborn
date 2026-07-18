package mctbl.tinkersreborn.tools.items.tools;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.tools.HarvestTool;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;

public class Pickaxe extends HarvestTool {

    public Pickaxe() {
        super("Pickaxe", 3);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("pickaxe", 0);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.pickaxeHead, MaterialStatusType.HEAD, "_pickaxe_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_pickaxe_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.binding, MaterialStatusType.EXTRA, "_pickaxe_accessory"));

    }

    @Override
    public boolean isEffective(Block block) {
        return pickaxeEffectiveMaterials.contains(block.getMaterial()) || pickaxeEffectiveBlocks.contains(block);
    }

    @Override
    public float damagePotential() {
        return 1.0F;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(35 + 18, 40 - 18) // pick head
                .addSlotPosition(35 - 20, 40 + 20) // rod
                .addSlotPosition(35, 40); // binding
        }
        return this.toolBuildGuiInfo;
    }
}
