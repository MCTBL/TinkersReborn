package mctbl.tinkersreborn.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;

public class TraitPoisonous extends AbstractTrait {

    public TraitPoisonous() {
        super("poisonous", 0x4D4968);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {
        if (wasHit && target.isEntityAlive()) {
            target.addPotionEffect(new PotionEffect(Potion.poison.id, 101));
        }
    }
}
