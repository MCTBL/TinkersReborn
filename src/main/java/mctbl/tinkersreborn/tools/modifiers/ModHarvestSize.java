package mctbl.tinkersreborn.tools.modifiers;

import java.util.Locale;

import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ToolModifier;

public class ModHarvestSize extends ToolModifier {

    public ModHarvestSize(String name) {
        super("harvest" + name.toLowerCase(Locale.US), 0xCAF6A2);
        addAspects(
            new ModifierAspect.SingleAspect(this),
            new ModifierAspect.DataAspect(this),
            ModifierAspect.aoeOnly,
            ModifierAspect.freeModifier);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        // no extra data needed
    }

}
