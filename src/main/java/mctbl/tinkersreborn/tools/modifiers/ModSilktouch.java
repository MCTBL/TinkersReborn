package mctbl.tinkersreborn.tools.modifiers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.library.crafting.ToolBuilderHelper;
import mctbl.tinkersreborn.library.tools.IToolMod;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ToolModifier;
import mctbl.tinkersreborn.tools.TinkersRebornModifiers;

public class ModSilktouch extends ToolModifier {

    public ModSilktouch() {
        super("silktouch", 0xfbe28b);

        addAspects(
            new ModifierAspect.SingleAspect(this),
            new ModifierAspect.DataAspect(this),
            ModifierAspect.freeModifier);
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return enchantment != Enchantment.looting && enchantment != Enchantment.fortune;
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return !otherModifier.getIdentifier()
            .equals(TinkersRebornModifiers.modLuck.getIdentifier());
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        ToolBuilderHelper.addEnchantment(rootCompound, Enchantment.silkTouch);

        // ToolNBT toolData = ToolTagsHelper.getToolStats(rootCompound);
        //
        // ToolTagsHelper.setToolTag(rootCompound, toolData.get());
    }
}
