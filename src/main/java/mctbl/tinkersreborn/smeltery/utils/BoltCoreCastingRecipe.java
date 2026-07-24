package mctbl.tinkersreborn.smeltery.utils;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.smeltery.ICastingRecipe;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.items.BoltCore;

/**
 * Special casting recipe for BoltCore.
 * Uses an arrowShaft as the cast (provides the shaft material),
 * and the poured fluid provides the head material.
 * The arrowShaft is consumed in the process.
 */
public class BoltCoreCastingRecipe implements ICastingRecipe {

    public static final BoltCoreCastingRecipe INSTANCE = new BoltCoreCastingRecipe();

    private static final int FLUID_AMOUNT = TinkersRebornMaterial.VALUE_Ingot * 2; // 288mB

    private BoltCoreCastingRecipe() {}

    @Override
    public boolean matches(@Nonnull ItemStack cast, Fluid fluid) {
        // Cast must be an arrowShaft with a valid material
        if (cast.getItem() != TinkersRebornTools.arrowShaft) {
            return false;
        }

        TinkersRebornMaterial shaftMat = TinkersRebornTools.arrowShaft.getMaterial(cast);
        if (shaftMat == TinkersRebornMaterial.UNKNOWN) {
            return false;
        }

        // Find the head material for this fluid
        TinkersRebornMaterial headMat = getHeadMaterialForFluid(fluid);
        if (headMat == null) {
            return false;
        }

        // Head material must have HEAD stats and be castable
        return headMat.hasStats(MaterialStatusType.HEAD) && headMat.isCastable();
    }

    @Override
    public ItemStack getResult(@Nonnull ItemStack cast, Fluid fluid) {
        TinkersRebornMaterial shaftMat = TinkersRebornTools.arrowShaft.getMaterial(cast);
        TinkersRebornMaterial headMat = getHeadMaterialForFluid(fluid);

        if (shaftMat == TinkersRebornMaterial.UNKNOWN || headMat == null) {
            return null;
        }

        return ((BoltCore) TinkersRebornTools.boltCore).getNewPartWithMaterial(headMat, shaftMat);
    }

    @Override
    public FluidStack getFluid(@Nonnull ItemStack cast, Fluid fluid) {
        return new FluidStack(fluid, FLUID_AMOUNT);
    }

    @Override
    public boolean consumesCast() {
        return true;
    }

    @Override
    public boolean switchOutputs() {
        return false;
    }

    @Override
    public int getTime() {
        return 120;
    }

    @Override
    public int getFluidAmount() {
        return FLUID_AMOUNT;
    }

    /**
     * Find the head material that corresponds to the given fluid.
     * Iterates all registered materials and returns the first one
     * whose fluid matches and has HEAD stats.
     */
    private static TinkersRebornMaterial getHeadMaterialForFluid(Fluid fluid) {
        for (TinkersRebornMaterial mat : TinkersRebornRegistry.getAllMaterialList()) {
            if (mat.getFluid() != null && mat.getFluid() == fluid
                && mat.hasStats(MaterialStatusType.HEAD)
                && mat.isCastable()) {
                return mat;
            }
        }
        return null;
    }
}
