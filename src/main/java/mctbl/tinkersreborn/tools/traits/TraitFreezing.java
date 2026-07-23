package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;

public class TraitFreezing extends AbstractTrait {

    public TraitFreezing() {
        super("freezing", 0x97D7E0);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage,
        boolean isCritical) {
        int level = -1;
        PotionEffect potionEffect = target.getActivePotionEffect(Potion.moveSlowdown);
        if (potionEffect != null) {
            level = potionEffect.getAmplifier();
        }

        level = Math.min(4, level + 1);

        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 30, level));
    }
}
