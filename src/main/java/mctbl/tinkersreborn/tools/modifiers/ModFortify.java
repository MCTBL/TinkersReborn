package mctbl.tinkersreborn.tools.modifiers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierAspect;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.modifiers.ToolModifier;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModFortify extends ToolModifier {

    public final TinkersRebornMaterial material;

    public ModFortify(TinkersRebornMaterial material) {
        super("fortify" + material.identifier, material.materialTextColor);

        if (!material.hasStats(MaterialStatusType.HEAD)) {
            // throw new TinkerAPIException(String.format("Trying to add a fortify-modifier for a material without tool
            // stats: %s", material.getIdentifier()));
        }

        this.material = material;
        addAspects(
            new ModifierAspect.SingleAspect(this),
            new ModifierAspect.DataAspect(this),
            ModifierAspect.harvestOnly);

        ItemStack kit = new ItemStack(TinkersRebornTools.sharpeningKit, 1, this.material.materialId);
        ItemStack flint = new ItemStack(Items.flint);
        addRecipeMatch(new RecipeMatch.ItemCombination(1, kit, flint));
    }

    @Override
    public String getLocalizedName() {
        return TinkersRebornUtils.translate(String.format(LOC_Name, "fortify")) + " (" + material.localizedName() + ")";
    }

    @Override
    public String getLocalizedDesc() {
        return String
            .format(TinkersRebornUtils.translate(String.format(LOC_Desc, "fortify")), material.localizedName());
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        HeadMaterialStats stats = material.getStats(MaterialStatusType.HEAD);
        ToolTagsHelper.setHarvestLevelStat(rootCompound, stats.harvestLevel);

        // Remove other fortify modifiers, only the last one applies
        NBTTagList tagList = ToolTagsHelper.getModifiersTagList(rootCompound);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound mod = tagList.getCompoundTagAt(i);
            ModifierNBT data = ModifierNBT.readTag(mod);

            // only up to ourselves
            if (data.identifier.equals(this.identifier)) {
                break;
            }

            // remove other fortify occurences
            if (data.identifier.startsWith("fortify")) {
                tagList.removeTag(i);
                i--; // adjust counter
            }
        }

        ToolTagsHelper.setModifiersTagList(rootCompound, tagList);
    }

    @Override
    public boolean hasTexturePerMaterial() {
        return true;
    }
}
