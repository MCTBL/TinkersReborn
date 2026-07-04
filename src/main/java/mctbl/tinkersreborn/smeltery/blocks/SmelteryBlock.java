package mctbl.tinkersreborn.smeltery.blocks;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.blocks.TinkersRebornMultiBlock;

public class SmelteryBlock extends TinkersRebornMultiBlock {

    public SmelteryBlock() {
        super();
        this.setBlockName("tinkersreborn.SmelteryBlock");
        this.TEXTURENAMES = new String[] { "smeltery/searedbrick", "smeltery/searedcobble", "smeltery/seared_stone" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.SmelteryBlock";
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        return this.getIcon(side, worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.icons[MathHelper.clamp_int(meta, 0, this.TEXTURENAMES.length - 1)];
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
        float clickY, float clickZ) {
        return false;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < this.TEXTURENAMES.length; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

}
