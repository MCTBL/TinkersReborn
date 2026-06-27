package mctbl.tinkersreborn.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.library.tools.modifiers.AbstractModifier;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModCreative extends AbstractModifier {

    public ModCreative() {
        super("creative");
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void updateNBT(NBTTagCompound modifierTag) {
        // same as level aspect, but we don't have a restriction here
        ModifierNBT data = ModifierNBT.readTag(modifierTag);
        data.level++;
        data.write(modifierTag);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        // substract the modifiers
        ModifierNBT data = ModifierNBT.readTag(ToolTagsHelper.getModifierTag(rootCompound, getIdentifier()));
        ToolTagsHelper.setExtraModifier(rootCompound, ToolTagsHelper.getExtraModifier(rootCompound) + data.level);
    }
}
