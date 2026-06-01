package mctbl.tinkersreborn.smeltery.blocks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.tools.TinkersRebornTools;

public class TinkersRebornFluid extends Fluid {

    private int materialId;

    public TinkersRebornFluid(String fluidName, int materialId) {
        super(fluidName);
        this.materialId = materialId;
    }

    /**
     * 
     * @param m         TinkersRebornMaterial
     * @param initFluid true if need auto register fluid and add to material
     */
    public TinkersRebornFluid(TinkersRebornMaterial m, boolean initFluid) {
        super("molten." + m.identifier);
        this.materialId = m.materialId;
        if (initFluid) {
            FluidRegistry.registerFluid(this);
            m.setFluidAndCastable(this);
            FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                    new FluidStack(this, 1000),
                    new ItemStack(TinkersRebornTools.tinkersBucket, 1, m.materialId),
                    new ItemStack(Items.bucket)));
        }
    }

    public TinkersRebornFluid(TinkersRebornMaterial m) {
        this(m, false);
    }

    @Override
    public int getColor() {
        TinkersRebornMaterial m;
        if ((m = TinkersRebornRegistry.getMaterialById(materialId)) != null) {
            return m.materialTextColor;
        } else {
            return TinkersRebornMaterial.UNKNOWN.materialTextColor;
        }
    }

    public int getMaterialId() {
        return this.materialId;
    }
}
