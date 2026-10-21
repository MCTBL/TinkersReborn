package mctbl.tinkersreborn.common.blocks.slime;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.world.gen.SlimeTreeGen;

public class SlimeSapling extends BlockSapling {

    public IIcon[] icons;

    public SlimeSapling() {
        super();
        float f = 0.4F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setHardness(0.0F);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.setStepSound(TinkersRebornGeneral.slimeStep);
        this.setBlockName("tinkersreborn.slime.sapling");
    }

    @Override
    public String getUnlocalizedName() {
        return TinkersReborn.MODID + ".slime.sapling";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[TinkersRebornConfig.slimeTypes.length];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister
                .registerIcon("tinkersreborn:slime/slimesapling_" + TinkersRebornConfig.slimeTypes[i]);
        }
    }

    @Override
    public boolean canPlaceBlockOn(Block id) {
        return id == Blocks.grass || id == Blocks.dirt || id == TinkersRebornGeneral.slimeGrass;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.isRemote) {
            return;
        }
        super.updateTick(world, x, y, z, random);
        int md = world.getBlockMetadata(x, y, z);
        if (random.nextInt(10) == 0 && world.getBlockLightValue(x, y + 1, z) >= 9) {
            if ((md & 8) == 0) {
                world.setBlockMetadataWithNotify(x, y, z, md | 8, 4);
            } else {
                growTree(world, x, y, z, random);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icons[meta % icons.length];
    }

    @Override
    public void func_149879_c(World world, int x, int y, int z, Random random) {
        boneFertilize(world, x, y, z, random);
    }

    public boolean boneFertilize(World world, int x, int y, int z, Random random) {
        int meta = world.getBlockMetadata(x, y, z);

        if ((meta & 8) == 0) {
            world.setBlockMetadataWithNotify(x, y, z, meta | 8, 4);
        } else {
            this.growTree(world, x, y, z, random);
        }

        return true;
    }

    public void growTree(World world, int x, int y, int z, Random random) {
        int md = world.getBlockMetadata(x, y, z) % 8;
        world.setBlock(x, y, z, Blocks.air);
        int woodMeta = switch (md) {
            case 0, 1 -> 1;
            case 2 -> 1;
            default -> md;
        };

        int leavesMeta = switch (md) {
            case 0, 1 -> 2;
            case 2 -> 1;
            default -> md;
        };

        WorldGenerator obj = new SlimeTreeGen(true, 5, 4, woodMeta, leavesMeta);

        if (!(obj.generate(world, random, x, y, z))) {
            world.setBlock(x, y, z, this, md + 8, 3);
        }
    }

    @Override
    public int damageDropped(int i) {
        return i % 8;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> list) {
        for (int i = 0; i < this.icons.length; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
}
