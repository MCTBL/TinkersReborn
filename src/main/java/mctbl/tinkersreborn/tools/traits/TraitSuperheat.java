package mctbl.tinkersreborn.tools.traits;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TraitSuperheat extends AbstractTrait {

    protected float bonus = 0.35f;

    public TraitSuperheat() {
        super("superheat", 0xFF960D);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        if (target.isBurning()) {
            newDamage += damage * bonus;
        }

        return newDamage;
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        String loc = TinkersRebornUtils.translate(String.format(LOC_Extra, identifier));
        return ImmutableList.of(String.format(loc, TinkersRebornUtils.df.format(bonus)));
    }

}
