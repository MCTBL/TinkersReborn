package mctbl.tinkersreborn.library.tools.modifiers;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.ImmutableList;

import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.util.ToolTags;

/** A modifier that gets applied manually rather than being a trait */
public abstract class ToolModifier extends AbstractModifier implements IModifierDisplay {

    protected int color;

    public ToolModifier(String identifier, int color) {
        super(identifier);

        this.color = color;
    }

    @Override
    public void updateNBT(NBTTagCompound modifierTag) {
        ModifierNBT data = ModifierNBT.readTag(modifierTag);
        data.identifier = this.getIdentifier();
        data.color = this.color;
        // we ensure at least lvl1 for compatibility with the level-aspect
        if (data.level == 0) {
            data.level = 1;
        }
        data.type = ToolTags.TYPEMODIFIERS;
        data.write(modifierTag);
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
}
