package mctbl.tinkersreborn.tools.traits;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TraitHellish extends AbstractTrait {

    private static final float bonusDamage = 4f;

    public TraitHellish() {
        super("hellish", 0xff0000);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        if (!target.isImmuneToFire()) {
            newDamage += bonusDamage;
        }
        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        String loc = TinkersRebornUtils.translate(String.format(LOC_Extra, identifier));

        return ImmutableList.of(String.format(loc, TinkersRebornUtils.df.format(bonusDamage)));
    }
}
