package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ShieldCore;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;

public class RoundShield extends ShieldCore {

    public static final float DURABILITY_MODIFIER = 1.25f;

    public RoundShield() {
        super("RoundShield", 2);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.shieldCore, MaterialStatusType.HEAD, "_roundshield_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_roundshield_handle"));
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(29, 60 - 30) // core
                .addSlotPosition(29, 60); // rod

        }
        return this.toolBuildGuiInfo;
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    public float blockingAngel() {
        return 110.0F;
    }

    @Override
    public float damageReduction() {
        return 0.55F;
    }

    @Override
    public float walkingSpeedReduction() {
        return 0.7F;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT toolTag = super.buildToolTag(materials);
        toolTag.durability *= DURABILITY_MODIFIER;
        return toolTag;
    }

}
