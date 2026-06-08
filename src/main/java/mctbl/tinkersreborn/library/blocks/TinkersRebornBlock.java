package mctbl.tinkersreborn.library.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TinkersRebornBlock extends Block {

    public String[] textureNames;
    public IIcon[] icons;

    public TinkersRebornBlock(Material material) {
        super(material);
    }

    public TinkersRebornBlock(Material material, String unlocalizedName, float hardness, String[] tex) {
        super(material);
        setHardness(hardness);
        textureNames = tex;
        this.unlocalizedName = unlocalizedName;
    }

    private String unlocalizedName;

    @Override
    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon("tinkersreborn:" + textureNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return meta < icons.length ? icons[meta] : icons[0];
    }

    @SideOnly(Side.CLIENT)
    public int getSideTextureIndex(int side) {
        if (side == 0) return 2;
        if (side == 1) return 0;

        return 1;
    }

    @Override
    public void getSubBlocks(Item block, CreativeTabs tab, List<ItemStack> list) {
        if (icons != null) {
            for (int iter = 0; iter < icons.length; iter++) {
                list.add(new ItemStack(block, 1, iter));
            }
        }
    }

}
