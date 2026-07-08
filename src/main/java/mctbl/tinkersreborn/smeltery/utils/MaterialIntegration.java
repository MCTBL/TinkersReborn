package mctbl.tinkersreborn.smeltery.utils;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class MaterialIntegration {

    public TinkersRebornMaterial material;
    public Fluid fluid;
    public String oreSuffix; // oredict suffix, e.g. "Iron" -> "ingotIron", "blockIron",...
    public String[] oreRequirement; // required oredict entry for this integration
    public String representativeItem; // oredict entry for the representative item
    private boolean integrated;
    private boolean preInit;
    private boolean addedFluidBlock;

    public MaterialIntegration(TinkersRebornMaterial material) {
        this(material, null);
    }

    public MaterialIntegration(TinkersRebornMaterial material, Fluid fluid) {
        this(null, material, fluid, null);
    }

    public MaterialIntegration(TinkersRebornMaterial material, Fluid fluid, String oreSuffix) {
        this("ingot" + oreSuffix, material, fluid, oreSuffix);
    }

    public MaterialIntegration(String oreRequirement, TinkersRebornMaterial material, Fluid fluid, String oreSuffix) {
        this(material, fluid, oreSuffix, oreRequirement);
    }

    public MaterialIntegration(TinkersRebornMaterial material, Fluid fluid, String oreSuffix,
        String... oreRequirement) {
        this.material = material;
        this.fluid = fluid;
        this.oreSuffix = oreSuffix;
        this.representativeItem = "ingot" + oreSuffix;
        this.oreRequirement = oreRequirement[0] == null ? new String[0] : oreRequirement; // API backwards compatibility

        this.integrated = false;
        this.preInit = false;
        this.addedFluidBlock = false;
    }

    public MaterialIntegration setRepresentativeItem(String representativeItem) {
        this.representativeItem = representativeItem;
        return this;
    }

    public boolean isIntegrated() {
        return integrated;
    }

    /**
     * Contains all registration as those need to be done regardless of oredict
     * entries
     */
    public void preInit() {
        // prevent running twice, mainly to prevent it from not getting registered as
        // the mod may load after we register
        if (preInit) {
            return;
        }

        preInit = true;

        // fluid first.
        if (fluid != null) {
            FluidRegistry.registerFluid(fluid);
            FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                    new FluidStack(fluid, 1000),
                    new ItemStack(TinkersRebornGeneral.tinkersBucket, 1),
                    new ItemStack(Items.bucket)));
        }

        // register material
        if (material != null) {
            TinkersRebornRegistry.addMaterialToMap(material);
            if (fluid != null) {
                material.setFluid(fluid);
                material.setCastable(true);
            } else {
                material.setCraftable(true);
            }
        }
    }

    /**
     * Called to integrate the recipes based on oredictionary recipes
     */
    public void integrate() {
        if (integrated) {
            return;
        }

        if (oreRequirement != null && oreRequirement.length > 0) {
            // loop through each ore string ensuring it is used
            for (String ore : oreRequirement) {
                // this is much more efficient then iterating through all entries and ensures we
                // do not create entries
                if (OreDictionary.getOres(ore, false)
                    .isEmpty()) {
                    return;
                }
            }
        }

        integrated = true;

        // register melting and casting
        if (fluid != null && oreSuffix != null) {
            TinkersRebornRegistry.registerOredictMeltingCasting(fluid, oreSuffix);
        }
        if (material != null) {
            TinkersRebornRegistry.registerToolpartMeltingCasting(material);
            registerRepresentativeItem();
        }
    }

    private void registerRepresentativeItem() {
        // also set the representative item
        if (TinkersRebornUtils.isStackEmpty(material.getRepresentativeItem()) && representativeItem != null
            && !representativeItem.isEmpty()) {
            material.setRepresentativeItem(representativeItem);
        }
    }
}
