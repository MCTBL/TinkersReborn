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

        ForgeDirection internalDir = facing.getOpposite(); // 默认值

        if (logic instanceof SmelteryDrainLogic drain) {
            BlockPos master = drain.getMasterPosition();
            if (master != null) {
                TileEntity masterTE = worldIn.getTileEntity(master.x, master.y, master.z);
                if (masterTE instanceof SmelteryLogic smeltery) {
                    BlockPos minPos = smeltery.minPos;
                    BlockPos maxPos = smeltery.maxPos;
                    if (minPos != null && maxPos != null) {
                        int minX = minPos.x, maxX = maxPos.x;
                        int minY = minPos.y, maxY = maxPos.y;
                        int minZ = minPos.z, maxZ = maxPos.z;

                        ForgeDirection bestDir = null;
                        double bestDot = -2.0;
                        ForgeDirection masterBack = facing.getOpposite();

                        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                            int nx = x + dir.offsetX;
                            int ny = y + dir.offsetY;
                            int nz = z + dir.offsetZ;

                            if (nx >= minX && nx <= maxX &&
                                ny >= minY && ny <= maxY &&
                                nz >= minZ && nz <= maxZ) {

                                double dot = dir.offsetX * masterBack.offsetX +
                                    dir.offsetY * masterBack.offsetY +
                                    dir.offsetZ * masterBack.offsetZ;
                                if (dot > bestDot) {
                                    bestDot = dot;
                                    bestDir = dir;
                                }
                            }
                        }
                        if (bestDir != null) {
                            internalDir = bestDir;
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
