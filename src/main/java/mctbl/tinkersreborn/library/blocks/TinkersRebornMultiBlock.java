package mctbl.tinkersreborn.library.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.entity.IMasterLogic;
import mctbl.tinkersreborn.library.entity.IServantLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;

public abstract class TinkersRebornMultiBlock extends TinkersRebornInventoryBlock {

    protected IIcon sideIcon;

    public TinkersRebornMultiBlock() {
        super(Material.rock);
        this.setHardness(3F);
        this.setResistance(20F);
        this.setStepSound(soundTypeMetal);
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
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

    public TileEntity createNewTileEntity(World world, int metadata) {
        return new MultiServantLogic();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            BlockPos offset = BlockPos.of(x, y, z)
                .offset(dir);
            TileEntity te = world.getTileEntity(offset.x, offset.y, offset.z);
            if (te instanceof IMasterLogic) {
                TileEntity servant = world.getTileEntity(x, y, z);
                if (servant instanceof IServantLogic) {
                    ((IMasterLogic) te).notifyChange((IServantLogic) servant, x, y, z);
                    break;
                }
            }
        }
    }
}
