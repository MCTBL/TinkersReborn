package mctbl.tinkersreborn.library.tools.modifiers;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.tools.traits.AbstractTrait;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.util.ToolTags;
import mctbl.tinkersreborn.util.ToolTagsHelper;

/**
 * Represents a modifier that has trait-logic
 * Modifier can have multiple levels.
 * Since this is intended for modifiers it uses a modifier
 */
public class ModifierTrait extends AbstractTrait implements IModifierDisplay {

    protected final int maxLevel;

    public ModifierTrait(String identifier, int color) {
        this(identifier, color, 0, 0);
    }

    public ModifierTrait(String identifier, int color, int maxLevel, int countPerLevel) {
        super(identifier, color);

        // register the modifier trait
        TinkersRebornRegistry.addModifierAndTrait(this);

        this.maxLevel = maxLevel;
        this.aspects.clear();

        if (maxLevel > 0 && countPerLevel > 0) {
            addAspects(new ModifierAspect.MultiAspect(this, color, maxLevel, countPerLevel, 1));
        } else {
            if (maxLevel > 0) {
                addAspects(new ModifierAspect.LevelAspect(this, maxLevel));
            }
            addAspects(new ModifierAspect.DataAspect(this, color), ModifierAspect.freeModifier);
        }

        this.type = ToolTags.TYPEMODIFIERS;
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        // not present yet, ok
        if (super.canApplyCustom(stack)) {
            return true;
        } else if (maxLevel == 0) {
            // no max level
            return false;
        }

        // already present, limit by level
        NBTTagCompound tag = ToolTagsHelper.getModifierTag(stack, identifier);

        return ModifierNBT.readTag(tag).level <= maxLevel;
    }

    @Override
    public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
        if (maxLevel > 0) {
            return getLeveledTooltip(modifierTag, detailed);
        }
        return super.getTooltip(modifierTag, detailed);
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public List<List<ItemStack>> getItems() {
        ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();

        for (RecipeMatch rm : items) {
            List<ItemStack> in = rm.getInputs();
            if (!in.isEmpty()) {
                builder.add(in);
            }
        }

        return builder.build();
    }

    public ModifierNBT.IntegerNBT getData(ItemStack tool) {
        NBTTagCompound tag = ToolTagsHelper.getModifierTag(tool, identifier);
        return ModifierNBT.readInteger(tag);
    }

}
