package mctbl.tinkersreborn.common.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSand;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;

public class GravelOre extends BlockSand {

    public String[] textureNames;
    public IIcon[] icons;

    public GravelOre() {
        this.textureNames = TinkersRebornConfig.gravelOreTypes;
        this.setStepSound(soundTypeGravel);
        this.setBlockName("tinkersreborn.GravelOre");

        this.setHarvestLevel("shovel", 1, 0);// iron
        this.setHarvestLevel("shovel", 2, 1);// gold
        this.setHarvestLevel("shovel", 1, 2);// copper
        this.setHarvestLevel("shovel", 1, 3);// tin
        this.setHarvestLevel("shovel", 1, 4);// aluminum
        this.setHarvestLevel("shovel", 4, 5);// cobalt

        this.setCreativeTab(TinkersRebornRegistry.blockTab);
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.GravelOre";
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon("tinkersreborn:ore_" + textureNames[i] + "_gravel");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta >= textureNames.length) return icons[0];
        return icons[meta];
    }

    public float getBlockHardness(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 5) return 10f;
        else return 3f;
    }

    @Override
    public int damageDropped(int meta) {
        /*
         * if (meta == 1) return 0;
         */
        return meta;
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3) {
        /*
         * if (par1 == 1) return Item.goldNugget.itemID;
         */
        return Item.getItemFromBlock(TinkersRebornGeneral.oreGravel);
    }

    @Override
    public void getSubBlocks(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int iter = 0; iter < textureNames.length; iter++) {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
