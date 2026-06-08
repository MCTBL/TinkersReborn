package mctbl.tinkersreborn.tools.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.ITinkersToolStationBlock;
import mctbl.tinkersreborn.library.blocks.TinkersRebornInventoryBlock;
import mctbl.tinkersreborn.tools.TinkersRebornToolsProxyCommon;
import mctbl.tinkersreborn.tools.entity.TinkersRebornPartBuilderLogic;
import mctbl.tinkersreborn.tools.model.TableRender;

public class PartBuilderBlock extends TinkersRebornInventoryBlock implements ITinkersToolStationBlock {

    public static final String[] materials = new String[] { "oak", "birch", "jungle", "spruce" };

    public PartBuilderBlock() {
        super(Material.wood);
        this.setHardness(2f);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockName("tinkersreborn.PartBuilder");
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.TEXTURENAMES = new String[] { "tools/partbuilder_top", "tools/partbuilder_%s_bottom",
            "tools/partbuilder_%s_side" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.PartBuilder";
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        List<IIcon> l = new ArrayList<>();
        l.add(iconRegister.registerIcon(getTextureDomain(0) + ":" + TEXTURENAMES[0]));

        for (int i = 1; i < this.TEXTURENAMES.length; i++) {
            for (String m : materials) {
                l.add(iconRegister.registerIcon(getTextureDomain(0) + ":" + String.format(TEXTURENAMES[i], m)));
            }
        }

        // top + each bottom + each side
        this.icons = l.toArray(new IIcon[0]);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return switch (ForgeDirection.getOrientation(side)) {
            case UP -> icons[0];
            case DOWN -> icons[meta + 1];
            default -> icons[meta + 5];
        };
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
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public int getRenderType() {
        return TableRender.model;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(
            (double) x + this.minX,
            (double) y + this.minY,
            (double) z + this.minZ,
            (double) x + this.maxX,
            (double) y + this.maxY,
            (double) z + this.maxZ);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TinkersRebornPartBuilderLogic();
    }

    @Override
    public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
        return TinkersRebornToolsProxyCommon.partBuilderID;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < materials.length; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    /* Keep pattern chest inventory */
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addExhaustion(0.025F);
        return world.setBlockToAir(x, y, z);
    }

}
