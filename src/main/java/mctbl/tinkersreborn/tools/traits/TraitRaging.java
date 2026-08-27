package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.traits.AbstractTraitLeveled;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TraitRaging extends AbstractTraitLeveled {

    protected final float leveledDamage = 2.5f;

    public TraitRaging(int levels) {
        super("raging", 0xc70000, 3, levels);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        ModifierNBT data = new ModifierNBT(ToolTagsHelper.getModifierTag(tool, name));
        float healthRatio = player.getHealth() / player.getMaxHealth();
        newDamage += (leveledDamage * data.level) * (1 - healthRatio);
        // LOG.info(
        // "Your health ratio is {}, TraitRaging gives you extra damage {}",
        // healthRatio,
        // (leveledDamage * data.level) * (1 - healthRatio));
        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }
}
