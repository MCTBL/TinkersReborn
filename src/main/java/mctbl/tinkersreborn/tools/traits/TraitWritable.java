package mctbl.tinkersreborn.tools.traits;

import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.library.tools.traits.AbstractTraitLeveled;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitWritable extends AbstractTraitLeveled {

    public TraitWritable(int levels) {
        super("writable", String.valueOf(levels), 0xffffff, 3, levels);
    }

    @Override
    public void applyModifierEffect(NBTTagCompound rootCompound) {
        // yaaay, modifiers
        ToolTagsHelper.setModifierSlots(rootCompound, ToolTagsHelper.getModifierSlots(rootCompound) + 1);
    }
}
