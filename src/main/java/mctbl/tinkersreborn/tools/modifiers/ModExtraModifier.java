package mctbl.tinkersreborn.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.modifiers.ToolModifier;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModExtraModifier extends ToolModifier {

    public ModExtraModifier() {
        super("extramodifier", 0xA300CC);

        addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this));
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
