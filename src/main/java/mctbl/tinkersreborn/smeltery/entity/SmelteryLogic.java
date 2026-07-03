package mctbl.tinkersreborn.smeltery.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.entity.TinkersRebornMultiBlockInvenotryLogic;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.gui.GuiSmeltery;
import mctbl.tinkersreborn.smeltery.inventory.ContainerSmeltery;

public class SmelteryLogic extends TinkersRebornMultiBlockInvenotryLogic implements IFluidTank {

    private static final int MAX_SMELTERY_SIZE = 7;
    public static final int MB_PER_BLOCK_CAPACITY = TinkersRebornMaterial.VALUE_Ingot * 10;

    protected final List<BlockPos> drains;

    public final List<FluidStack> moltenMetal = new ArrayList<>();
    public int maxMoltenMetalAmount;
    public int currentMoltenMetalAmount;

    public SmelteryLogic() {
        super("Smeltery");
        this.drains = new ArrayList<>();
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) return;

        if (!this.getActive() || this.needsUpdate) {
            this.needsUpdate = false;
            // check for smeltery once per second
            if (this.tickCounter == 0) {
                this.checkWholeStructureValid();
            }
            this.isHeating = false;
        } else {
            // smeltery structure is there.. do stuff with the current fuel
            // this also updates the needsFuel flag, which causes us to consume fuel at the
            // end.
            // This way fuel is only consumed if it's actually needed
            // if (tick % Config.heatItemsTickrateSmeltery == 0) {
            // heatItems();
            // alloyAlloys();
            // }

            if (this.needsFuel) {
                this.consumeFuel();
            }

            // we gradually check if the inside of the smeltery is blocked (for performance
            // reasons)
            if (this.tickCounter == 0) {
                // this.interactWithEntitiesInside();
                // called every second, we check every 15s or so
                if (++this.secondCounter >= 15) {
                    this.secondCounter = 0;
                    this.checkWholeStructureValid();
                } else {
                    this.checkSteppingingValid();
                }
            }
        }

        this.tickCounter = (this.tickCounter + 1) % 20;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public FluidStack getFluid() {
        if (this.moltenMetal.size() > 0) return this.moltenMetal.get(0);

        return null;
    }

    @Override
    public int getFluidAmount() {
        return this.currentMoltenMetalAmount;
    }

    @Override
    public int getCapacity() {
        return this.maxMoltenMetalAmount;
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int canFill = Math.max(0, Math.min(resource.amount, this.maxMoltenMetalAmount - this.currentMoltenMetalAmount));

        if (doFill) {
            boolean isAdded = false;
            for (FluidStack s : this.moltenMetal) {
                if (s.isFluidEqual(resource)) {
                    s.amount += canFill;
                    resource.amount -= canFill;
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded) {
                FluidStack copyFluid = resource.copy();
                copyFluid.amount = canFill;
                this.moltenMetal.add(copyFluid);
            }

            this.currentMoltenMetalAmount += canFill;
            this.markDirty();
        }

        return canFill;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack fluid = this.getFluid();

        if (fluid != null && fluid.amount > 0) {
            int drainAmount = Math.min(maxDrain, fluid.amount);
            FluidStack copy = fluid.copy();
            copy.amount = drainAmount;

            if (doDrain) {
                fluid.amount -= drainAmount;
                this.currentMoltenMetalAmount -= drainAmount;
                if (fluid.amount <= 0) this.moltenMetal.remove(fluid);
            }
            return copy;
        }
        return null;
    }

    /**
     * used by click in gui make that fluid to first output
     * 
     * @param fluid
     */
    public void moveFluidToFirst(FluidStack fluid) {
        if (fluid == null) return;

        FluidStack target = fluid;
        Iterator<FluidStack> iterator = this.moltenMetal.iterator();
        FluidStack inside = null;

        while (iterator.hasNext() && (inside = iterator.next()) != null) {
            if (inside.isFluidEqual(target)) {
                target = inside;
                iterator.remove();
                break;
            }
        }
        this.moltenMetal.add(0, target);
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
        int validLayerCount = 0;
        int[] range = new int[] { -xd1, xd2, -zd1, zd2 };
        // upper check this layer at same time
        boolean checkUpper = true, checkLower = true;
        int yd1 = 0, yd2 = 1;
        while (checkUpper || checkLower) {
            if (checkUpper && isValidLayer(center, range, center.y + yd1)) {
                yd1++;
                validLayerCount++;
            } else {
                checkUpper = false;
            }
            if (checkLower) {
                if (isValidLayer(center, range, center.y - yd2)) {
                    yd2++;
                    validLayerCount++;
                    continue;
                } else if (isValidBottom(center, range, center.y - yd2)) {
                    hasBottmLayer = true;
                }
                checkLower = false;
            }
        }

        if (hasBottmLayer && validLayerCount > 0 && this.lavaTanks.size() > 0) {
            this.activeLavaTank = this.lavaTanks.get(0);
            this.setActive(true);

            this.minPos = BlockPos.of(center.x - xd1 + 1, center.y - yd2 + 1, center.z - zd1 + 1);
            this.maxPos = BlockPos.of(center.x + xd2 - 1, center.y + yd1 - 1, center.z + zd2 - 1);

            this.adjustLayers();

            TinkersReborn.LOG.info("SmelteryLogic works! Found {} layers", validLayerCount);
        } else {
            this.setActive(false);
            this.temperature = INIT_TEMPERATURES;
        }

        worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    protected boolean isValidLayer(BlockPos center, int[] xAndZRange, int y) {
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
                        if (validTankBlock(block)) {
                            this.lavaTanks.add(BlockPos.of(center.x + dx, y, center.z + dz));
                        }
                    } else if (block != Blocks.air) {
                        return false;
                    }
                }

            }
        }
        return true;
    }

    protected boolean isValidBottom(BlockPos center, int[] xAndZRange, int y) {
        for (int dx = xAndZRange[0] + 1; dx <= xAndZRange[1] - 1; dx++) {
            for (int dz = xAndZRange[2] + 1; dz <= xAndZRange[3] - 1; dz++) {
                Block block = this.worldObj.getBlock(center.x + dx, y, center.z + dz);
                if (!validBottomBlock(block)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean validWallBlock(Block b) {
        return b == TinkersRebornSmeltery.smelteryController || b == TinkersRebornSmeltery.smelteryDrain
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
        int innerBlockCount = (this.maxPos.x - this.minPos.x + 1) * (this.maxPos.y - this.minPos.y + 1)
            * (this.maxPos.z - this.minPos.z + 1);
        this.resizeInventory(innerBlockCount);
        this.resizeTemperatures(innerBlockCount);
        this.maxMoltenMetalAmount = MB_PER_BLOCK_CAPACITY * innerBlockCount;
    }

    @Override
    protected void updateHeatRequired(int index) {
        // TODO Auto-generated method stub

    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerSmeltery(inventoryplayer, this);
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiSmeltery((ContainerSmeltery) getGuiContainer(inventoryplayer, world, x, y, z), this);
    }
}
