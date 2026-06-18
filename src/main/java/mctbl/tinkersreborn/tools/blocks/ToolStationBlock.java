package mctbl.tinkersreborn.tools.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
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
import mctbl.tinkersreborn.tools.entity.ToolStationLogic;
import mctbl.tinkersreborn.tools.model.TableRender;

public class ToolStationBlock extends TinkersRebornInventoryBlock implements ITinkersToolStationBlock {

    public ToolStationBlock() {
        super(Material.wood);
        this.setHardness(2f);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockName("tinkersreborn.ToolStation");
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.TEXTURENAMES = new String[] { "tools/toolstation_top", "tools/toolstation_bottom",
            "tools/toolstation_side" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.ToolStation";
    }

    public ToolStationBlock(Material m) {
        // for tool forge
        super(m);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return switch (ForgeDirection.getOrientation(side)) {
            case UP -> icons[0];
            case DOWN -> icons[1];
            default -> icons[2];
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
    public int getRenderType() {
        return TableRender.model;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new ToolStationLogic();
    }

    /* Keep pattern chest inventory */
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addExhaustion(0.025F);
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public int getGuiNumber(Block block) {
        return 0;
    }

}
