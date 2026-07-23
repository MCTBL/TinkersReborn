package mctbl.tinkersreborn.tools.traits;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TraitHoly extends AbstractTrait {

    private static float bonusDamage = 5f;

    public TraitHoly() {
        super("holy", 0xD1ECF6);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        if (target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            // LOG.info("TraitHoly gives extra damage {}", bonusDamage);
            newDamage += bonusDamage;
        }

        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt,
        boolean wasCritical, boolean wasHit) {
        if (wasHit && !target.isDead && target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            target.addPotionEffect(new PotionEffect(Potion.weakness.id, 50, 0, false));
        }
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        String loc = TinkersRebornUtils.translate(String.format(LOC_Extra, getIdentifier()));

        return ImmutableList.of(String.format(loc, TinkersRebornUtils.df.format(bonusDamage)));
    }
}
