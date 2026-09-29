package mctbl.tinkersreborn.library.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.entity.ItemIOHatchLogic;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;

public abstract class TinkersRebornSearedMultiBlockLogic extends TinkersRebornMultiBlockInvenotryLogic {

    public int blocksPerLayer;
    public int multiLayers;
    protected Block controller;

    protected TinkersRebornSearedMultiBlockLogic(String name, Block block) {
        super(name);
        this.controller = block;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) return;
        this.tickPre();
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
            if (this.tickCounter % TinkersRebornConfig.heatItemsTickrate == 0) {
                this.heatItems();
                this.heatItemsPost();
            }
            if (this.needsFuel) {
                this.consumeFuel();
            }
            if (this.tickCounter == 0) {
                // called every second, we check every 15s or so
                if (++this.secondCounter >= 15) {
                    this.secondCounter = 0;
                    this.checkWholeStructureValid();
                } else {
                    this.checkSteppingingValid();
                }
            }
        }
        this.tickPost();
        this.tickCounter = (this.tickCounter + 1) % 20;
    }

    protected void heatItemsPost() {}

    protected void adjustLayers() {
        this.blocksPerLayer = (this.maxPos.x - this.minPos.x + 1) * (this.maxPos.z - this.minPos.z + 1);
        this.multiLayers = (this.maxPos.y - this.minPos.y + 1);
        int innerBlockCount = calculateInnerBlockCount();
        this.resizeInventory(innerBlockCount);
        this.resizeTemperatures(innerBlockCount);
    }

    protected int calculateInnerBlockCount() {
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
                    .equals(this.getBlockPos())) {
                servant.removeMaster();
            } else if (tempEntity instanceof ItemIOHatchLogic hatch) {
                hatch.setFurnace(null);
            }
        }
        this.blocksPerLayer = 0;
        this.multiLayers = 0;
    }

}
