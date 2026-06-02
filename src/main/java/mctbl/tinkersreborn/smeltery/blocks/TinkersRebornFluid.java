package mctbl.tinkersreborn.smeltery.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;

public class TinkersRebornFluid extends Fluid {

    private Integer materialId;
    private Integer color;
    public String identifier;

    public TinkersRebornFluid(String fluidName, int color) {
        this(fluidName, color, false);
    }

    public TinkersRebornFluid(String fluidName, int color, boolean initFluid) {
        super(fluidName);
        this.identifier = fluidName;
        this.color = color;
        if (initFluid) {
            FluidRegistry.registerFluid(this);

            Block fluidBlock = new TinkersRebornFluidBlock(this, Material.water);
            fluidBlock.setBlockName("fluid." + fluidName);
            GameRegistry.registerBlock(fluidBlock, fluidBlock.getUnlocalizedName());

            FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                    new FluidStack(this, 1000),
                    new ItemStack(TinkersRebornGeneral.tinkersBucket, 1, TinkersRebornRegistry.allTinkersFluid.size()),
                    new ItemStack(Items.bucket)));
            TinkersRebornRegistry.allTinkersFluid.add(this);
        }
    }

    /**
     * 
     * @param m         TinkersRebornMaterial
     * @param initFluid true if need auto register fluid and add to material
     */
    public TinkersRebornFluid(TinkersRebornMaterial m, boolean initFluid) {
        super("molten." + m.identifier);
        this.setDensity(3000)
            .setViscosity(6000)
            .setTemperature(1300)
            .setLuminosity(12);
        this.materialId = m.materialId;
        if (initFluid) {
            FluidRegistry.registerFluid(this);
            m.setFluidAndCastable(this);

            Block fluidBlock = new TinkersRebornFluidBlock(this, Material.lava);
            fluidBlock.setBlockName("fluid.molten." + m.identifier);
            GameRegistry.registerBlock(fluidBlock, fluidBlock.getUnlocalizedName());

            FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                    new FluidStack(this, 1000),
                    new ItemStack(TinkersRebornGeneral.tinkersBucket, 1, TinkersRebornRegistry.allTinkersFluid.size()),
                    new ItemStack(Items.bucket)));
            TinkersRebornRegistry.allTinkersFluid.add(this);
        }

    }

    public TinkersRebornFluid(TinkersRebornMaterial m) {
        this(m, false);
    }

    @Override
    public int getColor() {
        if (this.color != null) {
            return this.color;
        }
        TinkersRebornMaterial m = TinkersRebornRegistry.getMaterialById(materialId);
        if (materialId != null && m != null) {
            return m.materialTextColor;
        }
        return TinkersRebornMaterial.UNKNOWN.materialTextColor;

    }

    public int getMaterialId() {
        return this.materialId;
    }
}
