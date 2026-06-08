package mctbl.tinkersreborn.smeltery.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.util.TextureHelper;

public class TinkersRebornFluidBlock extends BlockFluidClassic {

    public IIcon stillIcon;
    public IIcon flowIcon;
    boolean overwriteFluidIcons = true;
    private TinkersRebornFluid fluid = null;
    String unlocalizedName;

    public TinkersRebornFluidBlock(TinkersRebornFluid fluid, Material material, String unlocalizedName) {
        super(fluid, material);
        this.fluid = fluid;
        this.unlocalizedName = "fluid." + unlocalizedName;
    }

    @Override
    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        if (TextureHelper.itemTextureExists("tinkersreborn:" + this.fluid.identifier)) {
            stillIcon = iconRegister.registerIcon("tinkersreborn:" + this.fluid.identifier);
        } else {
            stillIcon = iconRegister.registerIcon("tinkersreborn:liquid");
        }
        if (TextureHelper.itemTextureExists("tinkersreborn:" + this.fluid.identifier + "_flow")) {
            flowIcon = iconRegister.registerIcon("tinkersreborn:" + this.fluid.identifier + "_flow");
        } else {
            flowIcon = iconRegister.registerIcon("tinkersreborn:liquid_flow");
        }
        if (this.fluid != null) this.fluid.setIcons(stillIcon, flowIcon);
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
