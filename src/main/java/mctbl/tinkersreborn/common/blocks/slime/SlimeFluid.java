package mctbl.tinkersreborn.common.blocks.slime;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.entity.SlimeBase;

public class SlimeFluid extends BlockFluidClassic {

    IIcon stillIcon;
    IIcon flowIcon;

    public SlimeFluid(Fluid fluid) {
        super(fluid, Material.water);
        this.setStepSound(TinkersRebornGeneral.slimeStep);
        this.setBlockName("tinkersreborn.liquid.slime");
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.liquid.slime";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        stillIcon = iconRegister.registerIcon("tinkersreborn:slime/slime_blue");
        flowIcon = iconRegister.registerIcon("tinkersreborn:slime/slime_blue_flow");
        this.definedFluid.setIcons(stillIcon, flowIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) return stillIcon;
        return flowIcon;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        super.updateTick(world, x, y, z, rand);
        if (rand.nextInt(100) == 0 && isSourceBlock(world, x, y, z)
            && world.checkNoEntityCollision(AxisAlignedBB.getBoundingBox(x - 1, y - 1, z - 1, x + 2, y + 2, z + 2))) {
            SlimeBase slime;
            // TODO
            // if (rand.nextInt(300) == 0) slime = new KingBlueSlime(world);
            // else slime = new BlueSlime(world);
            // slime.setPosition((double) x + 0.5D, (double) y + 1.5D, (double) z + 0.5D);
            // world.spawnEntityInWorld(slime);
        }
    }

}
