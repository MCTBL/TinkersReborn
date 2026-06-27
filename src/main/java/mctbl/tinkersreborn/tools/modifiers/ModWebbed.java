package mctbl.tinkersreborn.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierTrait;

public class ModWebbed extends ModifierTrait {

    public ModWebbed() {
        super("webbed", 0xFFFFFF, 3, 0);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage,
        boolean isCritical) {
        int duration = getData(tool).level * 60;
        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), duration, 1));
    }

}
