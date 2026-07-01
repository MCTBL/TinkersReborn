package mctbl.tinkersreborn.library.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.IMasterLogic;
import mctbl.tinkersreborn.library.entity.IServantLogic;
import mctbl.tinkersreborn.smeltery.model.SmelteryRender;

public abstract class TinkersRebornMultiBlock extends TinkersRebornInventoryBlock {

    protected IIcon sideIcon;

    public TinkersRebornMultiBlock(Material m) {
        super(m);
    }

    @Override
    public int getRenderType() {
        return SmelteryRender.smelteryModel;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        this.sideIcon = iconRegister.registerIcon("tinkersreborn:smeltery/searedbrick");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof IServantLogic) {
            ((IServantLogic) logic).notifyMasterOfChange();
        } else if (logic instanceof IMasterLogic) {
            ((IMasterLogic) logic).notifyChange(null, x, y, z);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block blockID, int meta) {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof IServantLogic) {
            ((IServantLogic) logic).notifyMasterOfChange();
        }
        super.breakBlock(world, x, y, z, blockID, meta);
    }

    public abstract TileEntity createNewTileEntity(World world, int metadata);
}
