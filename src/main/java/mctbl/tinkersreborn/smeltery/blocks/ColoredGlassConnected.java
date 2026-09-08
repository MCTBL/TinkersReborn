package mctbl.tinkersreborn.smeltery.blocks;

import java.util.ArrayList;
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

    public List<IIcon[]> iconsList;

    public ColoredGlassConnected() {
        super("colored", true);
        this.iconsList = new ArrayList<>();
        this.icons = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        if (meta < iconsList.size()) {
            return getConnectedBlockTexture(blockAccess, x, y, z, side, iconsList.get(meta));
        } else {
            return getConnectedBlockTexture(blockAccess, x, y, z, side, iconsList.get(0));
        }
    }

    @Override
    public boolean shouldConnectToBlock(IBlockAccess blockAccess, int x, int y, int z, Block b, int meta) {
        return b == this && (meta == blockAccess.getBlockMetadata(x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconsList.get(meta)[0];
    }

    @Override
    public void getSubBlocks(Item b, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < ItemDye.field_150921_b.length; i++) {
            list.add(new ItemStack(b, 1, i));
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        for (int i = 0; i < ItemDye.field_150921_b.length; i++) {
            iconsList.add(new IIcon[16]);
            iconsList.get(i)[0] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass");
            iconsList.get(i)[1] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_1_d");
            iconsList.get(i)[2] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_1_u");
            iconsList.get(i)[3] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_1_l");
            iconsList.get(i)[4] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_1_r");
            iconsList.get(i)[5] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_2_h");
            iconsList.get(i)[6] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_2_v");
            iconsList.get(i)[7] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_2_dl");
            iconsList.get(i)[8] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_2_dr");
            iconsList.get(i)[9] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_2_ul");
            iconsList.get(i)[10] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_2_ur");
            iconsList.get(i)[11] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_3_d");
            iconsList.get(i)[12] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_3_u");
            iconsList.get(i)[13] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_3_l");
            iconsList.get(i)[14] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_3_r");
            iconsList.get(i)[15] = register
                .registerIcon("tinkersreborn:glass/" + folder + "/" + ItemDye.field_150921_b[i] + "/glass_4");
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
