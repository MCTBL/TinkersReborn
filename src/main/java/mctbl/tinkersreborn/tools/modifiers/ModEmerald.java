package mctbl.tinkersreborn.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ToolModifier;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModEmerald extends ToolModifier {

    public ModEmerald() {
        super("emerald", 0x41F384);

        addAspects(
            new ModifierAspect.SingleAspect(this),
            new ModifierAspect.DataAspect(this),
            ModifierAspect.freeModifier);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        int durability = ToolTagsHelper.getDurabilityStat(rootCompound);
        int baseDurability = ToolTagsHelper.getOriginalDurability(rootCompound);
        int harvestLevel = ToolTagsHelper.getHarvestLevelStat(rootCompound);

        ToolTagsHelper.setDurabilityStat(rootCompound, durability + baseDurability / 2);
        ToolTagsHelper.setHarvestLevelStat(rootCompound, Math.max(harvestLevel, 4)); // cobalt?
    }
}
