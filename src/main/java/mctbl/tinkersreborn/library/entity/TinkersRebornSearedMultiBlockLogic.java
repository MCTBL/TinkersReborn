package mctbl.tinkersreborn.library.entity;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public abstract class TinkersRebornSearedMultiBlockLogic extends TinkersRebornMultiBlockInvenotryLogic{

    private static final int MAX_SMELTERY_SIZE = 7;
    public int blocksPerLayer;
    public int multiLayers;
    private Block controller;

    protected TinkersRebornSearedMultiBlockLogic(String name,Block block) {
        super(name);
        this.controller = block;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) return;
        tickPre();
        if ((!this.getActive() && this.tickCounter == 0) || this.needsUpdate) {
            // check for once per second
            this.needsUpdate = false;
            this.checkWholeStructureValid();
            this.isHeating = false;
        } else if (this.getActive()) {
            // structure is there.. do stuff with the current fuel
            // this also updates the needsFuel flag, which causes us to consume fuel at the
            // end.
            // This way fuel is only consumed if it's actually needed
            if (tickCounter % TinkersRebornConfig.heatItemsTickrateSmeltery == 0) {
                heatItems();
                heatItemsPost();
            }
            if (this.needsFuel) {
                this.consumeFuel();
            }
            tickPost();
        }
        this.tickCounter = (this.tickCounter + 1) % 20;
    }

    protected void heatItemsPost() {}

    protected void tickPre(){};

    protected void tickPost(){};

    protected abstract boolean hasTopLayer();

    protected abstract boolean hasBottmLayer();

    /**
     * check the whole structure
     */
    @Override
    public void checkWholeStructureValid() {
        if (this.worldObj.isRemote) return;

        ForgeDirection opposite = this.getForgeDirection()
            .getOpposite();
        BlockPos masterPos = this.getBlockPos();
        BlockPos center = masterPos.offset(opposite);

        // check x axis
        int xd1 = 1, xd2 = 1;
        for (int idx = 1; idx < MAX_SMELTERY_SIZE; idx++) {
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
        for (int i = 1; i < MAX_SMELTERY_SIZE; i++) {
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

        this.lavaTanks.clear();

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
//        while (checkUpper || checkLower) {
//            if (checkUpper && isValidLayer(center, range, center.y + yd1, tempValidBlockList)) {
//                yd1++;
//                validLayerCount++;
//            } else {
//                checkUpper = false;
//            }
//            if (checkLower) {
//                if (isValidLayer(center, range, center.y - yd2, tempValidBlockList)) {
//                    yd2++;
//                    validLayerCount++;
//                    continue;
//                } else if (isValidBottom(center, range, center.y - yd2, tempValidBlockList)) {
//                    hasBottmLayer = true;
//                }
//                checkLower = false;
//            }
//        }
//
//        if (hasBottmLayer && validLayerCount > 0 && !this.lavaTanks.isEmpty()) {
//            this.activeLavaTank = this.lavaTanks.get(0);
//            this.setActive(true);
//
//            this.minPos = BlockPos.of(center.x - xd1 + 1, center.y - yd2 + 1, center.z - zd1 + 1);
//            this.maxPos = BlockPos.of(center.x + xd2 - 1, center.y + yd1 - 1, center.z + zd2 - 1);
//
//            this.adjustLayers();
//
//            for (BlockPos b : tempValidBlockList) {
//                TileEntity tempEntiry = this.worldObj.getTileEntity(b.x, b.y, b.z);
//                if (tempEntiry instanceof MultiServantLogic servant) servant.overrideMaster(masterPos);
//            }
//        }
        while (checkUpper || checkLower) {
            if (checkUpper) {
                if (isValidLayer(center, range, center.y + yd1, tempValidBlockList)) {
                    yd1++;
                    validLayerCount++;
                }else if (hasTopLayer() && isValidTop(center, range, center.y + yd1, tempValidBlockList)){
                    hasTopLayer = true;
                    checkUpper = false;
                }else checkUpper = false;
            }
            if (checkLower) {
                if (isValidLayer(center, range, center.y - yd2, tempValidBlockList)) {
                    yd2++;
                    validLayerCount++;
                } else if (isValidBottom(center, range, center.y - yd2, tempValidBlockList)) {
                    hasBottmLayer = true;
                    checkLower = false;
                }else checkLower = false;
            }

        }

        if (hasTopLayer() == hasTopLayer && hasBottmLayer() == hasBottmLayer && validLayerCount > 0 && !this.lavaTanks.isEmpty()) {
            this.activeLavaTank = this.lavaTanks.get(0);
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

    protected boolean isValidLayer(BlockPos center, int[] xAndZRange, int y, List<BlockPos> tempValidBlockList) {
        List<BlockPos> tempList = new ArrayList<>();
        for (int dx = xAndZRange[0]; dx <= xAndZRange[1]; dx++) {
            for (int dz = xAndZRange[2]; dz <= xAndZRange[3]; dz++) {
                if (((dx == xAndZRange[0] || dx == xAndZRange[1]) && (dz == xAndZRange[2] || dz == xAndZRange[3]))) {
                    // skip 4 corner
                    continue;
                } else {
                    // check otter wall
                    Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                    if (dx == xAndZRange[0] || dx == xAndZRange[1] || dz == xAndZRange[2] || dz == xAndZRange[3]) {
                        if (!validWallBlock(block)) {
                            return false;
                        }
                        BlockPos newBlockPos = BlockPos.of(center.x + dx, y, center.z + dz);
                        if (validTankBlock(block)) {
                            tempList.add(newBlockPos);
                        }
                        tempValidBlockList.add(newBlockPos);
                    } else if (block != Blocks.air) {
                        return false;
                    }
                }

            }
        }
        this.lavaTanks.addAll(tempList);
        return true;
    }

    protected boolean isValidBottom(BlockPos center, int[] xAndZRange, int y, List<BlockPos> tempValidBlockList) {
        for (int dx = xAndZRange[0] + 1; dx <= xAndZRange[1] - 1; dx++) {
            for (int dz = xAndZRange[2] + 1; dz <= xAndZRange[3] - 1; dz++) {
                Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                if (!validBottomBlock(block)) {
                    return false;
                }
                tempValidBlockList.add(BlockPos.of(center.x + dx, y, center.z + dz));
            }
        }
        return true;
    }

    protected boolean isValidTop(BlockPos center, int[] xAndZRange, int y, List<BlockPos> tempValidBlockList) {
        for (int dx = xAndZRange[0] + 1; dx <= xAndZRange[1] - 1; dx++) {
            for (int dz = xAndZRange[2] + 1; dz <= xAndZRange[3] - 1; dz++) {
                Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                if (!validTopBlock(block)) {
                    return false;
                }
                tempValidBlockList.add(BlockPos.of(center.x + dx, y, center.z + dz));
            }
        }
        return true;
    }

    protected boolean validTopBlock(Block b) {
        return b == TinkersRebornSmeltery.smelteryBlock;
    }
    protected boolean validWallBlock(Block b) {
        return b == this.controller || b == TinkersRebornSmeltery.smelteryDrain
            || b == TinkersRebornSmeltery.smelteryBlock
            || b == TinkersRebornSmeltery.lavaTank;
    }

    protected boolean validBottomBlock(Block b) {
        return b == TinkersRebornSmeltery.smelteryBlock;
    }

    protected boolean validTankBlock(Block b) {
        return b == TinkersRebornSmeltery.lavaTank;
    }

    protected void adjustLayers() {
        this.blocksPerLayer = (this.maxPos.x - this.minPos.x + 1) * (this.maxPos.z - this.minPos.z + 1);
        this.multiLayers = (this.maxPos.y - this.minPos.y + 1);
        int innerBlockCount = calculateInnerBlockCount();
        this.resizeInventory(innerBlockCount);
        this.resizeTemperatures(innerBlockCount);
    }

    protected int calculateInnerBlockCount(){
        return this.blocksPerLayer * multiLayers;
    }

    protected void reset(List<BlockPos> tempValidBlockList) {
        this.setActive(false);
        this.temperature = INIT_TEMPERATURES;
        // reset fuel state to prevent stale values when structure is rebuilt
        this.fuelReleaseTicks = 0;
        this.fuelTotalTicks = 0;
        this.currentFuel = null;
        this.needsFuel = false;
        this.activeLavaTank = null;
        for (BlockPos b : tempValidBlockList) {
            TileEntity tempEntity = this.worldObj.getTileEntity(b.x, b.y, b.z);
            if (tempEntity instanceof MultiServantLogic servant && servant.getHasMaster()
                && servant.getMasterPosition()
                .equals(this.getBlockPos()))
                servant.removeMaster();
        }
        this.blocksPerLayer = 0;
        this.multiLayers = 0;
    }

}
