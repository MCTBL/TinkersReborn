package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.AoeHarvestTool;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class Excavator extends AoeHarvestTool {

    public static final float DURABILITY_MODIFIER = 1.75f;

    public Excavator() {
        super("Excavator", 4);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("shovel", 0);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.excavatorHead, MaterialStatusType.HEAD, "_excavator_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.largeplate, MaterialStatusType.HEAD, "_excavator_grip"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughrod, MaterialStatusType.HANDLE, "_excavator_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughbind, MaterialStatusType.EXTRA, "_excavator_binding"));
    }

    @Override
    public boolean isEffective(Block block) {
        return shovelEffectiveMaterials.contains(block.getMaterial()) || shovelEffectiveBlocks.contains(block);
    }

    @Override
    public float miningSpeedModifier() {
        return 0.28f;
    }

    @Override
    public float damagePotential() {
        return 1.25f;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        return ToolTagsHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 1, -1);
    }

    @Override
    public float getRepairModifierForPart(int index) {
        // 1.75 or 1.3125
        return index == 0 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT toolTag = super.buildToolTag(materials);
        toolTag.durability *= DURABILITY_MODIFIER;
        return toolTag;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 + 12, 42 - 16) // head
                .addSlotPosition(33 - 8, 42 - 16) // plate
                .addSlotPosition(33 - 10 + 2, 42 + 4) // handle
                .addSlotPosition(33 - 10 - 16, 42 + 20); // binding

        }
        return this.toolBuildGuiInfo;
    }

}
