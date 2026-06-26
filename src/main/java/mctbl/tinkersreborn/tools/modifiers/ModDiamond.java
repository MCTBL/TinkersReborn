package mctbl.tinkersreborn.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ToolModifier;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModDiamond extends ToolModifier {

    public ModDiamond() {
        super("diamond", 0x8CF4E2);

        addAspects(
            new ModifierAspect.SingleAspect(this),
            new ModifierAspect.DataAspect(this),
            ModifierAspect.freeModifier);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        int durability = ToolTagsHelper.getDurabilityStat(rootCompound);
        int harvestLevel = ToolTagsHelper.getHarvestLevelStat(rootCompound);
        float attack = ToolTagsHelper.getAttackStat(rootCompound);
        float miningSpeed = ToolTagsHelper.getMiningSpeedStat(rootCompound);

        ToolTagsHelper.setDurabilityStat(rootCompound, durability + 500);
        ToolTagsHelper.setHarvestLevelStat(rootCompound, Math.max(harvestLevel, 3)); // deault OBSIDIAN level
        ToolTagsHelper.setAttackStat(rootCompound, attack + 1.0F);
        ToolTagsHelper.setMiningSpeed(rootCompound, miningSpeed + 0.5F);
    }
}
