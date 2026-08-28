package mctbl.tinkersreborn.tools.traits;

import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.library.tools.traits.AbstractTraitLeveled;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitWritable extends AbstractTraitLeveled {

    public TraitWritable(int levels) {
        super("writable", String.valueOf(levels), 0xffffff, 3, 1);
    }

    @Override
    public void applyModifierEffect(NBTTagCompound rootCompound) {
        int modifiers = ToolTagsHelper.getExtraModifier(rootCompound) + levels;
        ToolTagsHelper.setExtraModifier(rootCompound, Math.max(0, modifiers));
    }
}
