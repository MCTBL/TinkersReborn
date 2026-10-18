package mctbl.tinkersreborn.smeltery.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.util.TextureHelper;

public class TinkersRebornFluidBlock extends BlockFluidClassic {

    public IIcon stillIcon;
    public IIcon flowIcon;
    private TinkersRebornFluid fluid = null;

    public TinkersRebornFluidBlock(TinkersRebornFluid fluid) {
        this(fluid, fluid.getTemperature() > 300 ? Material.lava : Material.water, fluid.getUnlocalizedName());
    }

    public TinkersRebornFluidBlock(TinkersRebornFluid fluid, String unlocalizedName) {
        this(fluid, fluid.getTemperature() > 300 ? Material.lava : Material.water, unlocalizedName);
    }

    public TinkersRebornFluidBlock(TinkersRebornFluid fluid, Material material, String unlocalizedName) {
        super(fluid, material);
        this.fluid = fluid;
        this.setBlockName(unlocalizedName);
        this.lightOpacity = this.fluid.getTemperature() > 300 ? 255 : 0;
        this.lightValue = this.fluid.getTemperature() > 300 ? 15 : 0;
    }

    @Override
    public String getLocalizedName() {
        return this.fluid.getLocalizedName();
    }

    @Override
    public String getUnlocalizedName() {
        return this.fluid.getUnlocalizedName();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        String preFix = TinkersReborn.MODID + ":";

        if (TextureHelper.itemTextureExists(preFix + this.fluid.identifier)) {
            stillIcon = iconRegister.registerIcon(preFix + this.fluid.identifier);
        } else {
            if (this.fluid.getTemperature() <= 300) {
                stillIcon = iconRegister.registerIcon(preFix + "liquid");
            } else {
                stillIcon = iconRegister.registerIcon(preFix + "liquid_molten");
            }
        }
        if (TextureHelper.itemTextureExists(preFix + this.fluid.identifier + "_flow")) {
            flowIcon = iconRegister.registerIcon(preFix + this.fluid.identifier + "_flow");
        } else {
            if (this.fluid.getTemperature() <= 300) {
                flowIcon = iconRegister.registerIcon(preFix + "liquid_flow");
            } else {
                flowIcon = iconRegister.registerIcon(preFix + "liquid_molten_flow");
            }
        }
        this.fluid.setIcons(stillIcon, flowIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return this.fluid.getColor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockColor() {
        return this.fluid.getColor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return this.fluid.getColor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) return stillIcon;
        return flowIcon;
    }

    @Override
    public Fluid getFluid() {
        return this.fluid;
    }
}
