package mctbl.tinkersreborn.smeltery.blocks;

import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.blocks.TinkersRebornMultiBlock;
import mctbl.tinkersreborn.smeltery.entity.SmelteryDrainLogic;

public class SmelteryDrain extends TinkersRebornMultiBlock {

    public SmelteryDrain() {
        super();
        this.setBlockName("tinkersreborn.Drain");
        this.TEXTURENAMES = new String[] { "smeltery/drain_basin", "smeltery/drain_out" };
    }

    @Override
    public String getUnlocalizedName() {
        return "tinkersreborn.Drain";
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        TileEntity logic = worldIn.getTileEntity(x, y, z);
        ForgeDirection facing = (logic instanceof ITinkersRebornIFacingLogic)
            ? ((ITinkersRebornIFacingLogic) logic).getForgeDirection()
            : ForgeDirection.getOrientation(0);

        ForgeDirection internalDir = facing.getOpposite();

        if (logic instanceof SmelteryDrainLogic drain) {
            BlockPos master = drain.getMasterPosition();
            if (master != null) {
                TileEntity masterTE = worldIn.getTileEntity(master.x, master.y, master.z);
                if (masterTE instanceof SmelteryLogic smeltery) {
                    BlockPos minPos = smeltery.minPos;
                    BlockPos maxPos = smeltery.maxPos;
                    if (minPos != null && maxPos != null) {

                        double cx = (minPos.x + maxPos.x) / 2.0;
                        double cy = (minPos.y + maxPos.y) / 2.0;
                        double cz = (minPos.z + maxPos.z) / 2.0;


                        double dx = cx - (x + 0.5);
                        double dy = cy - (y + 0.5);
                        double dz = cz - (z + 0.5);


                        if (Math.abs(dx) >= Math.abs(dy) && Math.abs(dx) >= Math.abs(dz)) {
                            internalDir = (dx > 0) ? ForgeDirection.EAST : ForgeDirection.WEST;
                        } else if (Math.abs(dy) >= Math.abs(dx) && Math.abs(dy) >= Math.abs(dz)) {
                            internalDir = (dy > 0) ? ForgeDirection.UP : ForgeDirection.DOWN;
                        } else {
                            internalDir = (dz > 0) ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
                        }
                    }
                }
            }
        }


        if (facing == ForgeDirection.getOrientation(side)) {
            return this.icons[0];
        } else if (internalDir == ForgeDirection.getOrientation(side)) {
            return this.icons[1];
        } else {
            return this.sideIcon;
        }
    }
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 3) {
            return this.icons[0];
        } else {
            return super.sideIcon;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new SmelteryDrainLogic();
    }

}
