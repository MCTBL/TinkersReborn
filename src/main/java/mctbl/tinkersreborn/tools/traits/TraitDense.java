package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.ToolTagsHelper;

// chance to use less durability if pretty damaged.
// chance scales quadratically with how much is missing
public class TraitDense extends AbstractTrait {

    public TraitDense() {
        super("dense", 0xCA9956);
    }

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
        float durability = ToolTagsHelper.getCurrentDurability(tool);
        float maxDurability = ToolTagsHelper.getMaxDurability(tool);

        float chance = 0.75f * (1f - durability / maxDurability);
        chance = chance * chance * chance;

        if (chance > random.nextFloat()) {
            // LOG.info("WOW, TraitDense makes less damge to your tools {}", Math.max(damage / 2, 1));
            newDamage -= Math.max(damage / 2, 1);
        }

        return super.onToolDamage(tool, damage, newDamage, entity);
    }
}
