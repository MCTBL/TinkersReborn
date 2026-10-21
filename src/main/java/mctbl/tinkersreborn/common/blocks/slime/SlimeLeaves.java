package mctbl.tinkersreborn.common.blocks.slime;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;

public class SlimeLeaves extends BlockLeaves {

    public SlimeLeaves() {
        super();
        this.setStepSound(TinkersRebornGeneral.slimeStep);
        this.setBlockName("tinkersreborn.slime.leaves");
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setHardness(0.3f);
    }

    @Override
    public String getUnlocalizedName() {
        return TinkersReborn.MODID + ".slime.leaves";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return TinkersRebornConfig.slimeColors[meta & 7];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return this.getRenderColor(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("tinkersreborn:slime/slimeleaves");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return Blocks.leaves.shouldSideBeRendered(worldIn, x, y, z, side);
    }

    @Override
    public boolean isOpaqueCube() {
        return Blocks.leaves.isOpaqueCube();
    }

    @Override
    public void getSubBlocks(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int iter = 0; iter < TinkersRebornConfig.slimeTypes.length; iter++) {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    /* Drops */

    /**
     * Returns the ID of the items to drop on destruction.
     */
    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(TinkersRebornGeneral.slimeSapling);
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
        if (!world.isRemote) {
            int dropChance = 35;

            if (fortune > 0) {
                dropChance -= 2 << fortune;

                if (dropChance < 15) {
                    dropChance = 15;
                }
            }

            if (world.rand.nextInt(dropChance) == 0) {
                Item k1 = this.getItemDropped(meta, world.rand, fortune);
                this.dropBlockAsItem(world, x, y, z, new ItemStack(k1, 1, this.damageDropped(meta)));
            }

            dropChance = 80;

            if (fortune > 0) {
                dropChance -= 10 << fortune;

                if (dropChance < 20) {
                    // TODO WTH?
                    dropChance = 20;
                }
            }
        }
    }

    @Override
    public String[] func_150125_e() {
        return new String[] { "slime" };
    }
}
