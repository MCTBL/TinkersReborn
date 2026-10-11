package mctbl.tinkersreborn.library.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import mctbl.tinkersreborn.library.blocks.IActiveLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.entity.ItemIOHatchLogic;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public abstract class TinkersRebornMultiBlockInvenotryLogic extends TinkersRebornInventoryLogic
    implements IMasterLogic, IActiveLogic {

    // NBT Tags
    public static final String TAG_TANKS = "tanks";
    public static final String TAG_FUEL_QUALITY = "fuelQuality";
    public static final String TAG_CURRENT_FUEL = "currentFuel";
    public static final String TAG_CURRENT_TANK = "currentTank";
    public static final String TAG_ACTIVE = "active";
    public static final String TAG_MINPOS = "minPos";
    public static final String TAG_MAXPOS = "maxPos";
    protected static final int TIME_FACTOR = 8;
    public static final BlockPos DEFAULT_POS = BlockPos.of(0, 0, 0);
    /** smallest coordinate INSIDE the multiblock */
    public BlockPos minPos = DEFAULT_POS;
    /** biggest coordinate INSIDE the multiblock */
    public BlockPos maxPos = DEFAULT_POS;
    public boolean validStructure;
    protected int tickCounter = 0;
    protected int secondCounter = 0;
    protected boolean needsUpdate;
    /**
     * used by {@link #checkSteppingingValid()} and {@link #stepNextInnerPos()}
     */
    protected BlockPos nextCheckInner;
    /**
     * @see #getDefaultName
     */
    protected String name;

    public int blocksPerLayer;
    public int multiLayers;
    protected Block controller;

    protected final List<Block> validWall;
    protected final List<Block> validCeiling;
    protected final List<Block> validFloor;

    protected TinkersRebornMultiBlockInvenotryLogic(String name) {
        super(0);
        this.name = "tinkersreborn.gui." + name;

        this.validWall = new ArrayList<>();
        this.validCeiling = new ArrayList<>();
        this.validFloor = new ArrayList<>();
    }

    @Override
    public void checkWholeStructureValid() {
        if (this.worldObj.isRemote) return;
        ForgeDirection opposite = this.getForgeDirection()
            .getOpposite();
        BlockPos masterPos = this.getBlockPos();
        BlockPos center = masterPos.offset(opposite);

        // check x axis
        int xd1 = 1, xd2 = 1;
        for (int idx = 1; idx < getMaxMultiSize(); idx++) {
            if (this.worldObj.isAirBlock(center.x - xd1, center.y, center.z)) xd1++;
            else if (this.worldObj.isAirBlock(center.x + xd2, center.y, center.z)) xd2++;

            // if one side hit a wall and the other didn't we might have to center our
            // x-position again
            if (xd1 - xd2 > 1) {
                // move x and offsets to the -x
                xd1--;
                center.x--;
                xd2++;
            }
            // or the right
            if (xd2 - xd1 > 1) {
                xd2--;
                center.x++;
                xd1++;
            }
        }

        // check z axis
        int zd1 = 1, zd2 = 1;
        for (int i = 1; i < getMaxMultiSize(); i++) {
            if (this.worldObj.isAirBlock(center.x, center.y, center.z - zd1)) zd1++;
            else if (this.worldObj.isAirBlock(center.x, center.y, center.z + zd2)) zd2++;

            // if one side hit a wall and the other didn't we might have to center our
            // x-position again
            if (zd1 - zd2 > 1) {
                // move x and offsets to the -x
                zd1--;
                center.z--;
                zd2++;
            }
            // or the right
            if (zd2 - zd1 > 1) {
                zd2--;
                center.z++;
                zd1++;
            }
        }

        boolean hasBottmLayer = false;
        boolean hasTopLayer = false;
        int validLayerCount = 0;
        int[] range = new int[] { -xd1, xd2, -zd1, zd2 };
        // upper check this layer at same time
        boolean checkUpper = true;
        boolean checkLower = true;
        int yd1 = 0;
        int yd2 = 1;

        List<BlockPos> tempValidBlockList = new ArrayList<>();
        while (checkUpper || checkLower) {
            if (checkUpper) {
                if (isValidLayer(center, range, center.y + yd1, tempValidBlockList)) {
                    yd1++;
                    validLayerCount++;
                } else if (hasCeilingLayer() && isValidTop(center, range, center.y + yd1, tempValidBlockList)) {
                    hasTopLayer = true;
                    checkUpper = false;
                } else checkUpper = false;
            }
            if (checkLower) {
                if (isValidLayer(center, range, center.y - yd2, tempValidBlockList)) {
                    yd2++;
                    validLayerCount++;
                } else if (isValidBottom(center, range, center.y - yd2, tempValidBlockList)) {
                    hasBottmLayer = true;
                    checkLower = false;
                } else checkLower = false;
            }

        }

        if (hasCeilingLayer() == hasTopLayer && hasFloorLayer() == hasBottmLayer && validLayerCount > 0) {
            this.setActive(true);

            this.minPos = BlockPos.of(center.x - xd1 + 1, center.y - yd2 + 1, center.z - zd1 + 1);
            this.maxPos = BlockPos.of(center.x + xd2 - 1, center.y + yd1 - 1, center.z + zd2 - 1);

            this.adjustLayers();

            for (BlockPos b : tempValidBlockList) {
                TileEntity tempEntiry = this.worldObj.getTileEntity(b.x, b.y, b.z);
                if (tempEntiry instanceof MultiServantLogic servant) servant.overrideMaster(masterPos);
            }
        } else {
            reset(tempValidBlockList);
        }
        worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    protected void adjustLayers() {
        this.blocksPerLayer = (this.maxPos.x - this.minPos.x + 1) * (this.maxPos.z - this.minPos.z + 1);
        this.multiLayers = (this.maxPos.y - this.minPos.y + 1);
        int innerBlockCount = calculateInnerBlockCount();
        this.resizeInventory(innerBlockCount);
    }

    @Override
    public int calculateInnerBlockCount() {
        return this.blocksPerLayer * multiLayers;
    }

    protected void reset(List<BlockPos> tempValidBlockList) {
        this.reset();
        for (BlockPos b : tempValidBlockList) {
            TileEntity tempEntity = this.worldObj.getTileEntity(b.x, b.y, b.z);
            if (tempEntity instanceof MultiServantLogic servant && servant.getHasMaster()
                && servant.getMasterPosition()
                    .equals(this.getBlockPos())) {
                servant.removeMaster();
            }
        }
    }

    protected void reset() {
        this.setActive(false);
        this.blocksPerLayer = 0;
        this.multiLayers = 0;
    }

    protected boolean isValidLayer(BlockPos center, int[] xAndZRange, int y, List<BlockPos> tempValidBlockList) {
        for (int dx = xAndZRange[0]; dx <= xAndZRange[1]; dx++) {
            for (int dz = xAndZRange[2]; dz <= xAndZRange[3]; dz++) {
                boolean isCorner = (dx == xAndZRange[0] || dx == xAndZRange[1])
                    && (dz == xAndZRange[2] || dz == xAndZRange[3]);
                if (isCorner) {
                    if (hasFrame()) {
                        Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                        if (!validFrameBlock(block)) {
                            return false;
                        }
                        BlockPos newBlockPos = BlockPos.of(center.x + dx, y, center.z + dz);
                        tempValidBlockList.add(newBlockPos);
                    } else {
                        // skip outer
                        continue;
                    }
                } else {
                    // check otter wall
                    Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                    if (dx == xAndZRange[0] || dx == xAndZRange[1] || dz == xAndZRange[2] || dz == xAndZRange[3]) {
                        if (!validWallBlock(block)) {
                            return false;
                        }
                        BlockPos newBlockPos = BlockPos.of(center.x + dx, y, center.z + dz);
                        tempValidBlockList.add(newBlockPos);
                    } else if (block != Blocks.air) {
                        return false;
                    }
                }

            }
        }
        return true;
    }

    protected boolean isValidBottom(BlockPos center, int[] xAndZRange, int y, List<BlockPos> tempValidBlockList) {
        for (int dx = xAndZRange[0]; dx <= xAndZRange[1]; dx++) {
            for (int dz = xAndZRange[2]; dz <= xAndZRange[3]; dz++) {
                boolean isFrame = (dx == xAndZRange[0] || dx == xAndZRange[1])
                    || (dz == xAndZRange[2] || dz == xAndZRange[3]);
                if (isFrame) {
                    if (hasFrame()) {
                        Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                        if (!validFrameBlock(block)) {
                            return false;
                        }
                        BlockPos newBlockPos = BlockPos.of(center.x + dx, y, center.z + dz);
                        tempValidBlockList.add(newBlockPos);
                    } else {
                        // skip outer
                        continue;
                    }
                } else {
                    Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                    if (!validFloorBlock(block)) {
                        return false;
                    }
                    BlockPos newBlockPos = BlockPos.of(center.x + dx, y, center.z + dz);
                    tempValidBlockList.add(newBlockPos);
                }
            }
        }
        return true;
    }

    protected boolean isValidTop(BlockPos center, int[] xAndZRange, int y, List<BlockPos> tempValidBlockList) {
        for (int dx = xAndZRange[0]; dx <= xAndZRange[1]; dx++) {
            for (int dz = xAndZRange[2]; dz <= xAndZRange[3]; dz++) {
                boolean isFrame = (dx == xAndZRange[0] || dx == xAndZRange[1])
                    || (dz == xAndZRange[2] || dz == xAndZRange[3]);
                if (isFrame) {
                    if (hasFrame()) {
                        Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                        if (!validFrameBlock(block)) {
                            return false;
                        }
                        BlockPos newBlockPos = BlockPos.of(center.x + dx, y, center.z + dz);
                        tempValidBlockList.add(newBlockPos);
                    } else {
                        // skip outer
                        continue;
                    }
                } else {
                    Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                    if (!validCeilingBlock(block)) {
                        return false;
                    }
                    BlockPos newBlockPos = BlockPos.of(center.x + dx, y, center.z + dz);
                    tempValidBlockList.add(newBlockPos);
                }
            }
        }
        return true;
    }

    protected boolean validCeilingBlock(Block b) {
        return validCeiling.contains(b);
    }

    protected boolean validFrameBlock(Block b) {
        return validWall.contains(b);
    }

    protected boolean validWallBlock(Block b) {
        return validWall.contains(b);
    }

    protected boolean validFloorBlock(Block b) {
        return validFloor.contains(b);
    }

    @Override
    public void notifyChange(IServantLogic servant, int x, int y, int z) {
        this.checkWholeStructureValid();
    }

    @Override
    public boolean getActive() {
        return this.validStructure;
    }

    @Override
    public void setActive(boolean flag) {
        this.validStructure = flag;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    protected String getDefaultName() {
        return this.name;
    }

    @Override
    public abstract void updateEntity();

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);

        tags.setBoolean(TAG_ACTIVE, validStructure);
        tags.setTag(TAG_MINPOS, writePos(minPos));
        tags.setTag(TAG_MAXPOS, writePos(maxPos));
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);

        validStructure = tags.getBoolean(TAG_ACTIVE);
        minPos = readPos(tags.getCompoundTag(TAG_MINPOS));
        maxPos = readPos(tags.getCompoundTag(TAG_MAXPOS));

        needsUpdate = true;
    }

    public static NBTTagCompound writePos(BlockPos pos) {
        NBTTagCompound tag = new NBTTagCompound();
        if (pos != null) {
            tag.setInteger("x", pos.getX());
            tag.setInteger("y", pos.getY());
            tag.setInteger("z", pos.getZ());
        }
        return tag;
    }

    public static BlockPos readPos(NBTTagCompound tag) {
        if (tag != null) {
            return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
        }
        return null;
    }

    protected void resizeInventory(int newSize) {
        ItemStack[] oldInv = this.inventory;
        this.inventory = new ItemStack[newSize];
        int loopIdx = Math.min(oldInv.length, this.inventory.length);
        System.arraycopy(oldInv, 0, this.inventory, 0, loopIdx);
        for (int idx = loopIdx; idx < oldInv.length; idx++) {
            if (oldInv[idx] != null && oldInv[idx].stackSize != 0) TinkersRebornUtils.dropItemAtPos(
                this.worldObj,
                this.getBlockPos()
                    .offset(this.faceDirection),
                oldInv[idx]);
        }
    }

    /**
     * special for {@link ItemIOHatchLogic#canExtractItem(int, ItemStack, int)}
     * 
     * @param slot
     * @param stack
     * @return
     */
    public abstract boolean canExtractItem(int slot, ItemStack stack);
}
