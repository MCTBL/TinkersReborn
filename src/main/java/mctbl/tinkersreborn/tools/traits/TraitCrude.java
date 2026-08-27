package mctbl.tinkersreborn.tools.traits;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.traits.AbstractTraitLeveled;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitCrude extends AbstractTraitLeveled {

    public TraitCrude(int levels) {
        super("crude", 0x424242, 3, levels);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        boolean hasArmor = target.getTotalArmorValue() > 0;
        if (!hasArmor) {
            NBTTagCompound modifierTag = ToolTagsHelper.getModifierTag(tool, name);
            // 5% *BASE* damage boost against unarmed targets!
            newDamage += damage * bonusModifier(modifierTag);
        }
        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    private float bonusModifier(NBTTagCompound modifierNBT) {
        ModifierNBT data = new ModifierNBT(modifierNBT);
        return 0.05f * data.level;
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        String loc = String.format(LOC_Extra, this.name);
        float bonus = bonusModifier(modifierTag);

        return ImmutableList.of(TinkersRebornUtils.translate(loc, TinkersRebornUtils.dfPercent.format(bonus)));
    }
}
