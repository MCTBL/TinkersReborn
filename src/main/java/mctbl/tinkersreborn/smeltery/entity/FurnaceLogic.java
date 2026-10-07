package mctbl.tinkersreborn.smeltery.entity;

import static mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery.lavaTank;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.entity.TinkersRebornHeatableMultiBlockLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.gui.GuiFurnace;
import mctbl.tinkersreborn.smeltery.inventory.ContainerFurnace;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class FurnaceLogic extends TinkersRebornHeatableMultiBlockLogic {

    private static final int MAX_FURNACE_SIZE = 11;

    public FurnaceLogic() {
        super("furnace", TinkersRebornSmeltery.furnaceController);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!worldObj.isRemote) {
            for (int i = 0; i < inventory.length - 1; i++) {
                updateTempRequired(i);
            }
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        if (!super.isItemValidForSlot(slot, itemstack)) return false;
        if (itemstack == null) return false;
        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(itemstack);
        if (result == null || result.stackSize <= 0) return false;
        ItemStack existing = this.getStackInSlot(slot);
        int cap = Math.min(16, 64 / result.stackSize);
        int amount = itemstack.stackSize + (existing != null ? existing.stackSize : 0);
        return amount <= cap;
    }

    @Override
    public void checkWholeStructureValid() {
        ForgeDirection facing = this.getForgeDirection();
        BlockPos masterPos = this.getBlockPos();
        BlockPos center = masterPos.offset(facing.getOpposite());

        this.lavaTanks.clear();

        if (!this.worldObj.isAirBlock(center.x, center.y, center.z)) {
            this.reset(new ArrayList<>());
            return;
        }

        BlockPos wallWest = this.traceWall(center, ForgeDirection.WEST);
        BlockPos wallEast = this.traceWall(center, ForgeDirection.EAST);
        BlockPos wallDown = this.traceWall(center, ForgeDirection.DOWN);
        BlockPos wallUp = this.traceWall(center, ForgeDirection.UP);
        BlockPos wallNorth = this.traceWall(center, ForgeDirection.NORTH);
        BlockPos wallSouth = this.traceWall(center, ForgeDirection.SOUTH);

        if (wallWest == null || wallEast == null
            || wallDown == null
            || wallUp == null
            || wallNorth == null
            || wallSouth == null) {
            this.reset(new ArrayList<>());
            return;
        }

        BlockPos minPos = BlockPos.of(wallWest.x + 1, wallDown.y + 1, wallNorth.z + 1);
        BlockPos maxPos = BlockPos.of(wallEast.x - 1, wallUp.y - 1, wallSouth.z - 1);

        if (minPos.x > maxPos.x || minPos.y > maxPos.y || minPos.z > maxPos.z) {
            this.reset(new ArrayList<>());
            return;
        }

        for (BlockPos pos : BlockPos.getAllInBox(minPos, maxPos)) {
            if (!this.worldObj.isAirBlock(pos.x, pos.y, pos.z)) {
                this.reset(new ArrayList<>());
                return;
            }
        }

        List<BlockPos> shellBlocks = new ArrayList<>();
        List<BlockPos> tanks = new ArrayList<>();
        if (!this.checkShell(minPos, maxPos, shellBlocks, tanks)) {
            this.reset(shellBlocks);
            return;
        }

        this.minPos = minPos;
        this.maxPos = maxPos;
        this.lavaTanks.addAll(tanks);
        this.activeLavaTank = tanks.isEmpty() ? null : tanks.get(0);

        this.setActive(true);
        this.adjustLayers();

        for (BlockPos pos : shellBlocks) {
            TileEntity te = this.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof MultiServantLogic servant) {
                servant.overrideMaster(masterPos);
            }
        }
    }

    private BlockPos traceWall(BlockPos center, ForgeDirection dir) {
        Vec3 start = Vec3.createVectorHelper(center.x + 0.5D, center.y + 0.5D, center.z + 0.5D);
        Vec3 end = Vec3.createVectorHelper(
            start.xCoord + dir.offsetX * MAX_FURNACE_SIZE,
            start.yCoord + dir.offsetY * MAX_FURNACE_SIZE,
            start.zCoord + dir.offsetZ * MAX_FURNACE_SIZE);

        MovingObjectPosition mop = this.worldObj.rayTraceBlocks(start, end);
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return null;
        }
        return BlockPos.of(mop.blockX, mop.blockY, mop.blockZ);
    }

    private boolean checkShell(BlockPos min, BlockPos max, List<BlockPos> shellBlocks, List<BlockPos> tanks) {
        boolean valid = true;

        int minX = min.x - 1, maxX = max.x + 1;
        int minY = min.y - 1, maxY = max.y + 1;
        int minZ = min.z - 1, maxZ = max.z + 1;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    boolean isSurface = x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ;
                    if (!isSurface) {
                        continue;
                    }

                    Block block = this.worldObj.getBlock(x, y, z);
                    BlockPos pos = BlockPos.of(x, y, z);
                    if (block == this.controller && this.getBlockPos()
                        .equals(pos)) {
                        continue;
                    }
                    shellBlocks.add(pos);

                    if (!this.validShellBlock(block)) {
                        valid = false;
                        continue;
                    }

                    if (block == lavaTank) {
                        tanks.add(pos);
                    }

                }
            }
        }

        return valid && !tanks.isEmpty();
    }

    private boolean validShellBlock(Block block) {
        return (block == TinkersRebornSmeltery.smelteryBlock || block == lavaTank
            || block == TinkersRebornSmeltery.itemIOHatch);
    }

    /**
     * Calculate the heat required for the given slot
     *
     * @param index
     */
    @Override
    public void updateTempRequired(int index) {
        ItemStack stack = getStackInSlot(index);
        if (!TinkersRebornUtils.isStackEmpty(stack)) {
            if (FurnaceRecipes.smelting()
                .getSmeltingResult(stack) != null) {
                int base = 200;
                float temp = base * stack.stackSize / 4f;

                if (stack.getItem() instanceof ItemFood) {
                    temp *= 0.8f;
                }

                setTempRequiredForSlot(index, (int) temp);
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
        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(stack);
        if (result != null) {
            result = result.copy();
            int amount = stack.stackSize;
            result.stackSize *= amount;
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
    protected int calculateInnerBlockCount() {
        return super.calculateInnerBlockCount() * 3 + 9;
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerFurnace(inventoryplayer, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiFurnace((ContainerFurnace) getGuiContainer(inventoryplayer, world, x, y, z), this);
    }
}
