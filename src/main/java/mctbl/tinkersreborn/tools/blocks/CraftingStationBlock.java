package mctbl.tinkersreborn.tools.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornInventoryBlock;
import mctbl.tinkersreborn.tools.TinkersRebornToolsProxyCommon;
import mctbl.tinkersreborn.tools.entity.CraftingStationLogic;
import mctbl.tinkersreborn.tools.model.TableRender;

public class CraftingStationBlock extends TinkersRebornInventoryBlock {

    public CraftingStationBlock() {
        super(Material.wood);
        this.setHardness(2f);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockName("tinkersreborn.CraftingStation");
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
        this.TEXTURENAMES = new String[] { "tools/craftingstation_top", "tools/craftingstation_bottom",
            "tools/craftingstation_side" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.CraftingStation";
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
    public int getRenderType() {
        return TableRender.model;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return true;
    }

    @Override
    public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
        return TinkersRebornToolsProxyCommon.craftingStationID;
    }

    @Override
    public TileEntity createNewTileEntity(World arg0, int arg1) {
        return new CraftingStationLogic();
    }
}
