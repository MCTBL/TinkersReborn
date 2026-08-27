package mctbl.tinkersreborn.library.tools.traits;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public abstract class AbstractTraitLeveled extends AbstractTrait {

    protected final String name;
    protected final int levels;

    protected AbstractTraitLeveled(String identifier, int color, int maxLevels, int levels) {
        this(identifier, String.valueOf(levels), color, maxLevels, levels);
    }

    protected AbstractTraitLeveled(String identifier, String suffix, int color, int maxLevels, int levels) {
        super(identifier + suffix, color);
        this.name = identifier;

        this.levels = levels;

        // don't overwrite the modifier alias if one with less levels already is present
        // we basically always want the level1 one to be associated with the modifier used
        IModifier modifier = TinkersRebornRegistry.getModifierAndTrait(name);
        if (modifier != null) {
            if (modifier instanceof AbstractTraitLeveled m && m.levels > this.levels) {
                TinkersRebornRegistry.addModifierAndTraitAlias(this, name);
            }
        } else {
            TinkersRebornRegistry.addModifierAndTraitAlias(this, name);
        }

        aspects.clear();
        this.addAspects(new ModifierAspect.LevelAspect(this, maxLevels), new ModifierAspect.DataAspect(this, color));
    }

    @Override
    public void updateNBTforTrait(NBTTagCompound modifierTag, int newColor) {
        super.updateNBTforTrait(modifierTag, newColor);

        ModifierNBT data = ModifierNBT.readTag(modifierTag);
        data.level = 0; // handled by applyEffect in this case
        data.write(modifierTag);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        super.applyEffect(rootCompound, modifierTag);

        // traits are the only things that can modify here safely, since they're only ever called on tool creation
        NBTTagList tagList = ToolTagsHelper.getModifiersTagList(rootCompound);
        ToolTagsHelper.setModifiersTagList(rootCompound, tagList);

        NBTTagCompound tag = ToolTagsHelper.getModifierTag(rootCompound, getModifierIdentifier());

        if (tag.hasNoTags()) {
            tagList.appendTag(tag);
        }

        if (!tag.getBoolean(getIdentifier())) {
            ModifierNBT data = ModifierNBT.readTag(tag);
            data.level += levels;
            data.write(tag);
            tag.setBoolean(getIdentifier(), true);

            applyModifierEffect(rootCompound);
        }
    }

    @Override
    public String getModifierIdentifier() {
        return name;
    }

    /**
     * Called when the trait gets applied. Called for each application/level of the trait.
     * Only called once per specific trait (e.g. Writable1 and Writable2) but multiple times overall (per specific trait
     * present)
     *
     * Unlike Modifiers that get applied with the total result, you can do things incrementally here.
     */
    public void applyModifierEffect(NBTTagCompound rootCompound) {

    }

    @Override
    public String getLocalizedName() {
        return TinkersRebornUtils.translate(String.format(LOC_Name, name));

    }

    @Override
    public String getLocalizedDesc() {
        return TinkersRebornUtils.translate(String.format(LOC_Desc, name));
    }

    @Override
    public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
        return getLeveledTooltip(modifierTag, detailed);
    }

    public int getLevels() {
        return levels;
    }
}
