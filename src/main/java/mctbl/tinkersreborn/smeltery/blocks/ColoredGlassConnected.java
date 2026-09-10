package mctbl.tinkersreborn.smeltery.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ColoredGlassConnected extends GlassConnected {

    public ColoredGlassConnected() {
        super("colored", true);
    }

    @Override
    public boolean shouldConnectToBlock(IBlockAccess blockAccess, int x, int y, int z, Block b, int meta) {
        return b == this && (meta == blockAccess.getBlockMetadata(x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return ItemDye.field_150922_c[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return this.getRenderColor(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return getConnectedBlockTexture(blockAccess, x, y, z, side, icons);
    }

    @Override
    public void getSubBlocks(Item b, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < ItemDye.field_150921_b.length; i++) {
            list.add(new ItemStack(b, 1, i));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        return true;
    }
}
