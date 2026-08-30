package mctbl.tinkersreborn.smeltery.entity;

import mctbl.tinkersreborn.library.entity.TinkersRebornMultiBlockInvenotryLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.blocks.FurnaceController;
import mctbl.tinkersreborn.smeltery.blocks.LavaTankBlock;
import mctbl.tinkersreborn.smeltery.blocks.SmelteryBlock;
import mctbl.tinkersreborn.smeltery.gui.GuiFurnace;
import mctbl.tinkersreborn.smeltery.inventory.ContainerFurnace;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

import static mctbl.tinkersreborn.TinkersRebornConfig.heatItemsTickrateFurnace;

public class FurnaceLogic extends TinkersRebornMultiBlockInvenotryLogic {

    private static final int MAX_FURNACE_SIZE = 7;

    protected final BlockPos[] CavityBox = new BlockPos[2];

    private int cavityMinX, cavityMaxX;
    private int cavityMinY, cavityMaxY;
    private int cavityMinZ, cavityMaxZ;

    public FurnaceLogic() {
        super("furnace");
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) return;

        if (tickCounter == 0 || needsUpdate) {
            this.needsUpdate = false;
            checkWholeStructureValid();
            isHeating = false;
        } else if (getActive()) {
            if (tickCounter % heatItemsTickrateFurnace == 0){
                heatItems();
            }
            if (this.needsFuel) {
                this.consumeFuel();
            }// we gradually check if the inside of the smeltery is blocked (for performance
            // reasons)
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

        this.tickCounter = (this.tickCounter + 1) % 20;
    }

    @Override
    protected void heatItems() {
        boolean heatedItem = false;
        boolean triedRefuel = false;
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!TinkersRebornUtils.isStackEmpty(stack)) {
                // heat item if possible
                if (itemTempRequired[i] > 0) {
                    // fuel is present, turn up the heat
                    if (fuelReleaseTicks > 0) {
                        // if the temperature is high enough for the slot
                        if (canHeat(i)) {
                            // are we done heating?
                            if (itemTemperatures[i] >= itemTempRequired[i]) {
                                if (onItemFinishedHeating(stack, i)) {
                                    itemTemperatures[i] = 0;
                                    itemTempRequired[i] = 0;
                                }
                            }
                            // otherwise turn up the heat
                            else {
                                itemTemperatures[i] += heatSlot(i);
                                heatedItem = true;
                            }
                        }
                    } else if (!triedRefuel) {
                        // out of fuel, try to consume more right now
                        // so we don't miss this tick's heating
                        this.needsFuel = true;
                        this.consumeFuel();
                        triedRefuel = true;
                        if (fuelReleaseTicks > 0) {
                            // fuel acquired, retry this slot
                            i--;
                            continue;
                        }
                        // truly out of fuel, nothing more we can do
                        break;
                    } else {
                        // already tried refueling this tick and failed, give up
                        break;
                    }
                }
            } else {
                itemTemperatures[i] = 0;
            }
        }

        if (heatedItem) {
            fuelReleaseTicks--;
        }
        updateIfChanged(heatedItem);
    }

    @Override
    public boolean canHeat(int index) {
        ItemStack stack = getStackInSlot(index);
        return FurnaceRecipes.smelting().getSmeltingResult(stack) != null;
    }

    @Override
    protected int heatSlot(int i) {
        return 40;
    }

    @Override
    protected void setTempRequiredForSlot(int index, int heat) {
        if (index < itemTempRequired.length) {
            ItemStack stack = getStackInSlot(index);
            if (stack != null) {
                itemTempRequired[index] = heat * stack.stackSize;
                return;
            }
            itemTempRequired[index] = heat;
        }
    }


    /**
     * Calculate the heat required for the given slot
     *
     * @param index
     */
    @Override
    protected void updateTempRequired(int index) {
        ItemStack stack = getStackInSlot(index);
        if (!TinkersRebornUtils.isStackEmpty(stack)) {
            if (FurnaceRecipes.smelting().getSmeltingResult(stack) != null) {
                setTempRequiredForSlot(index, 1000);
                if (fuelReleaseTicks <= 0) {
                    consumeFuel();
                }
                return;
            }
        }
        setTempRequiredForSlot(index, 0);
    }

    /**
     * Called when an item finished heating up. Return true if the processing was
     * successful, then the heating data will be cleared.
     *
     * @param stack
     * @param slot
     */
    @Override
    protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(stack);
        if (result != null) {
            result = result.copy();
            int amount = result.stackSize == 0 ? 1 : result.stackSize;
            result.stackSize = stack.stackSize * amount;
            setInventorySlotContents(slot, result);
            return true;
        }
        return false;
    }

    @Override
    public float getProgress(int index) {
        if (index >= itemTemperatures.length) {
            return 0f;
        }
        return Math.min(1f, (float) itemTemperatures[index] / itemTempRequired[index]);
    }

    @Override
    public void checkWholeStructureValid() {
        if (this.worldObj.isRemote) return;

        BlockPos center = this.getBlockPos()
            .offset(this.getForgeDirection()
                .getOpposite());

        this.measureCavity(center);

        boolean valid = this.checkShellValid();

        if (valid) {
            this.minPos = BlockPos.of(cavityMinX, cavityMinY, cavityMinZ);
            this.maxPos = BlockPos.of(cavityMaxX, cavityMaxY, cavityMaxZ);
            this.setActive(true);
            int innerBlockCount = (cavityMaxX - cavityMinX + 1)
                * (cavityMaxY - cavityMinY + 1)
                * (cavityMaxZ - cavityMinZ + 1);
            this.resizeInventory(innerBlockCount);
            this.resizeTemperatures(innerBlockCount);
        } else {
            this.setActive(false);
        }

        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    private void measureCavity(BlockPos center) {
        int xd1 = 1, xd2 = 1;
        for (int idx = 1; idx < MAX_FURNACE_SIZE; idx++) {
            if (this.worldObj.isAirBlock(center.x - xd1, center.y, center.z)) xd1++;
            else if (this.worldObj.isAirBlock(center.x + xd2, center.y, center.z)) xd2++;

            if (xd1 - xd2 > 1) {
                xd1--;
                center.x--;
                xd2++;
            }
            if (xd2 - xd1 > 1) {
                xd2--;
                center.x++;
                xd1++;
            }
        }

        int zd1 = 1, zd2 = 1;
        for (int i = 1; i < MAX_FURNACE_SIZE; i++) {
            if (this.worldObj.isAirBlock(center.x, center.y, center.z - zd1)) zd1++;
            else if (this.worldObj.isAirBlock(center.x, center.y, center.z + zd2)) zd2++;

            if (zd1 - zd2 > 1) {
                zd1--;
                center.z--;
                zd2++;
            }
            if (zd2 - zd1 > 1) {
                zd2--;
                center.z++;
                zd1++;
            }
        }

        this.cavityMinX = center.x - xd1 + 1;
        this.cavityMaxX = center.x + xd2 - 1;
        this.cavityMinZ = center.z - zd1 + 1;
        this.cavityMaxZ = center.z + zd2 - 1;

        int up = 0;
        while (up < MAX_FURNACE_SIZE && this.worldObj.isAirBlock(center.x, center.y + up, center.z)) up++;
        int down = 0;
        while (down < MAX_FURNACE_SIZE && this.worldObj.isAirBlock(center.x, center.y - down - 1, center.z)) down++;

        this.cavityMaxY = center.y + up - 1;
        this.cavityMinY = center.y - down;

        this.CavityBox[0] = BlockPos.of(center.x, cavityMaxY, center.z);
        this.CavityBox[1] = BlockPos.of(center.x, cavityMinY, center.z);
    }

    private boolean checkShellValid() {
        if (cavityMinX > cavityMaxX || cavityMinY > cavityMaxY || cavityMinZ > cavityMaxZ) {
            return false;
        }

        for (int x = cavityMinX - 1; x <= cavityMaxX + 1; x++) {
            for (int z = cavityMinZ - 1; z <= cavityMaxZ + 1; z++) {
                for (int y = cavityMinY - 1; y <= cavityMaxY + 1; y++) {
                    boolean interior = x >= cavityMinX && x <= cavityMaxX
                        && y >= cavityMinY && y <= cavityMaxY
                        && z >= cavityMinZ && z <= cavityMaxZ;
                    if (interior) continue;
                    Block block = this.worldObj.getBlock(x, y, z);
                    if (block instanceof FurnaceController) {
                        if (x == this.xCoord && y == this.yCoord && z == this.zCoord){
                            continue;
                        }return false;
                    }else if (block instanceof LavaTankBlock){
                        this.lavaTanks.add(BlockPos.of(x, y, z));
                        continue;
                    };
                    if (!isValidStructureBlock(block)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    private boolean isValidStructureBlock(Block block) {
        return block instanceof SmelteryBlock;
    }


    @Override
    public void checkSteppingingValid() {
        super.checkSteppingingValid();
    }

    @Override
    public void stepNextInnerPos() {
        super.stepNextInnerPos();
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerFurnace(inventoryplayer, this);
    }

    @Override
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiFurnace((ContainerFurnace) getGuiContainer(inventoryplayer, world, x, y, z), this);
    }
}
