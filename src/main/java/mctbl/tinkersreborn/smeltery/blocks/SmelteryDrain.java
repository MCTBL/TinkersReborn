package mctbl.tinkersreborn.smeltery.blocks;

import static mctbl.tinkersreborn.util.TinkersRebornUtils.replaceHeldItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import mctbl.tinkersreborn.library.blocks.ITinkersRebornIFacingLogic;
import mctbl.tinkersreborn.library.blocks.TinkersRebornMultiBlock;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.entity.SmelteryDrainLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.smeltery.items.FilledBucket;

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
        ForgeDirection facing = (logic instanceof ITinkersRebornIFacingLogic l) ? l.getForgeDirection()
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

                            if (nx >= minX && nx <= maxX && ny >= minY && ny <= maxY && nz >= minZ && nz <= maxZ) {

                                double dot = dir.offsetX * masterBack.offsetX + dir.offsetY * masterBack.offsetY
                                    + dir.offsetZ * masterBack.offsetZ;
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

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
        float clickY, float clickZ) {
        if (!world.isRemote && player.getHeldItem() != null
            && world.getTileEntity(x, y, z) instanceof SmelteryDrainLogic logic) {
            ItemStack heldItem = player.getHeldItem();

            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(heldItem);
            if (heldItem.getItem() instanceof FilledBucket bucket) {
                liquid = new FluidStack(bucket.getFluidStackInBucket(heldItem), FluidContainerRegistry.BUCKET_VOLUME);
            }

            // putting liquid into the tank
            if (liquid != null) {
                int amount = logic.fill(ForgeDirection.UNKNOWN, liquid, false);
                if (amount == liquid.amount) {
                    logic.fill(ForgeDirection.UNKNOWN, liquid, true);
                    if (!player.capabilities.isCreativeMode) {
                        replaceHeldItem(player, FluidContainerRegistry.drainFluidContainer(heldItem));
                    }

                    // update
                    player.inventoryContainer.detectAndSendChanges();
                    world.markBlockForUpdate(x, y, z);
                }
                return true;
            }

        }
        return super.onBlockActivated(world, x, y, z, player, side, clickX, clickY, clickZ);
    }
}
