package mctbl.tinkersreborn.tools.modifiers;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierTrait;
import mctbl.tinkersreborn.tools.TinkersRebornModifiers;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModAntiMonsterType extends ModifierTrait {

    protected final EnumCreatureAttribute creatureType;

    private final float dmgPerItem;
    private final int countPerLevel;

    public ModAntiMonsterType(String identifier, int color, int maxLevel, int countPerLevel,
        EnumCreatureAttribute type) {
        super(identifier, color, maxLevel, countPerLevel);
        this.creatureType = type;
        this.countPerLevel = countPerLevel;

        dmgPerItem = 7f / countPerLevel;
    }

    protected float calcIncreasedDamage(NBTTagCompound modifierTag, float baseDamage) {
        ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

        return baseDamage + data.current * dmgPerItem;
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage,
        boolean isCritical) {
        if (target.getCreatureAttribute() == creatureType) {
            NBTTagCompound tag = ToolTagsHelper.getModifierTag(tool, identifier);
            return calcIncreasedDamage(tag, newDamage);
        }
        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    @Override
    public void convertTConstractNBT(ItemStack tool, NBTTagCompound tcToolNBTCompound) {
        int[] intArray = null;

        if (this.equals(TinkersRebornModifiers.modBaneOfArthopods) && tcToolNBTCompound.hasKey("ModAntiSpider")) {
            intArray = tcToolNBTCompound.getIntArray("ModAntiSpider");
        } else if (this.equals(TinkersRebornModifiers.modSmite) && tcToolNBTCompound.hasKey("ModSmite")) {
            intArray = tcToolNBTCompound.getIntArray("ModSmite");
        }

        if (intArray != null && intArray.length == 3) {
            setAntiProgress(tool, intArray[0]);
        }
    }

    public void setAntiProgress(ItemStack tool, int progress) {
        // convert by ratio
        progress = (int) ((progress * 1.0F / 240) * maxLevel * countPerLevel);

        final NBTTagCompound root = ToolTagsHelper.getTagSafe(tool);
        final NBTTagCompound modifierTag = ToolTagsHelper.getModifierTag(root, identifier);
        final ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

        final int level = (progress - 1) / countPerLevel + 1;
        final int max = level * countPerLevel;

        data.identifier = identifier;
        data.type = ToolTags.TYPEMODIFIERS;
        data.color = color;
        data.current = progress;
        data.level = level;
        data.max = max;
        data.extraInfo = data.calcInfo();
        data.write(modifierTag);

        ToolTagsHelper.getModifiersTagList(root)
            .appendTag(modifierTag);

        ToolTagsHelper.setUsedModifiers(tool, ToolTagsHelper.getUsedModifiers(tool) + level);

        applyEffect(root, modifierTag);
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
