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
    String unlocalizedName;

    public TinkersRebornFluid(String fluidName, int color, boolean initFluid, boolean needFluidBlock) {
        super(fluidName);
        this.unlocalizedName = fluidName;
        this.identifier = fluidName;
        this.color = color;
        if (initFluid) {
            FluidRegistry.registerFluid(this);
            FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                    new FluidStack(this, 1000),
                    new ItemStack(TinkersRebornGeneral.tinkersBucket, 1, TinkersRebornRegistry.allTinkersFluid.size()),
                    new ItemStack(Items.bucket)));
            TinkersRebornRegistry.allTinkersFluid.add(this);
        }

        if (needFluidBlock) {
            Block fluidBlock = new TinkersRebornFluidBlock(this, Material.water, this.unlocalizedName);
            GameRegistry.registerBlock(fluidBlock, fluidBlock.getUnlocalizedName());
        }
    }

    /**
     * 
     * @param m         TinkersRebornMaterial
     * @param initFluid true if need auto register fluid and add to material
     */
    public TinkersRebornFluid(TinkersRebornMaterial m, boolean initFluid) {
        super("molten_" + m.identifier);
        this.unlocalizedName = "molten_" + m.identifier;
        this.setDensity(3000)
            .setViscosity(6000)
            .setTemperature(1300)
            .setLuminosity(12);
        this.identifier = m.identifier;
        this.materialId = m.materialId;
        if (initFluid) {
            FluidRegistry.registerFluid(this);
            m.setFluidAndCastable(this);

            Block fluidBlock = new TinkersRebornFluidBlock(this, Material.lava, this.unlocalizedName);
            GameRegistry.registerBlock(fluidBlock, fluidBlock.getUnlocalizedName());

            FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                    new FluidStack(this, 1000),
                    new ItemStack(TinkersRebornGeneral.tinkersBucket, 1, TinkersRebornRegistry.allTinkersFluid.size()),
                    new ItemStack(Items.bucket)));
            TinkersRebornRegistry.allTinkersFluid.add(this);
        }

    }

    @Override
    public String getUnlocalizedName() {
        return this.unlocalizedName;
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
