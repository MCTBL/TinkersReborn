package mctbl.tinkersreborn.smeltery.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornInventoryBlock;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.entity.CastingChannelLogic;
import mctbl.tinkersreborn.smeltery.model.BlockRenderCastingChannel;

public class CastingChannelBlock extends TinkersRebornInventoryBlock {

    public CastingChannelBlock() {
        super(Material.rock);
        this.setHardness(1F);
        this.setResistance(10);
        this.stepSound = soundTypeStone;
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.TEXTURENAMES = new String[] { "smeltery/searedstone" };
        this.setBlockName("tinkersreborn.SearedBlock.CastingChannel");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        ItemStack stack = player.getCurrentEquippedItem();
        CastingChannelLogic tile = (CastingChannelLogic) world.getTileEntity(x, y, z);

        if (stack != null && stack.getItem() == Item.getItemFromBlock(TinkersRebornSmeltery.castingChannel))
            return false;
        else {
            tile.changeOutputs(player, side, hitX, hitY, hitZ);
            return true;
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float minZ = 0F;
        float maxZ = 1F;
        float minX = 0F;
        float maxX = 1F;
        this.setBlockBounds(minX, 0.375F, minZ, maxX, 0.625F, maxZ);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return true;
    }

    @Override
    public int getRenderType() {
        return BlockRenderCastingChannel.renderID;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int metadata) {
        return new CastingChannelLogic();
    }

    @Override
    public void getSubBlocks(Item id, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(id, 1, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        else return icons[1];
    }
}
