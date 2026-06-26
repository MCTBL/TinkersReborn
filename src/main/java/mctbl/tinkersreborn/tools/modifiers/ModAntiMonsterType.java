package mctbl.tinkersreborn.tools.modifiers;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierTrait;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModAntiMonsterType extends ModifierTrait {

    protected final EnumCreatureAttribute type;

    private final float dmgPerItem;

    public ModAntiMonsterType(String identifier, int color, int maxLevel, int countPerLevel,
        EnumCreatureAttribute type) {
        super(identifier, color, maxLevel, countPerLevel);
        this.type = type;

        dmgPerItem = 7f / (float) countPerLevel;
    }

    protected float calcIncreasedDamage(NBTTagCompound modifierTag, float baseDamage) {
        ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

        return baseDamage + (float) data.current * dmgPerItem;
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        if (target.getCreatureAttribute() == type) {
            NBTTagCompound tag = ToolTagsHelper.getModifierTag(tool, identifier);
            return calcIncreasedDamage(tag, newDamage);
        }
        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        String loc = String.format(LOC_Extra, getIdentifier());

        if (TinkersRebornUtils.canTranslate(loc)) {
            float dmg = calcIncreasedDamage(modifierTag, 0);
            loc = TinkersRebornUtils.translate(loc);
            return ImmutableList.of(String.format(loc, TinkersRebornUtils.df.format(dmg)));
        }
        return super.getExtraInfo(tool, modifierTag);
    }
}
