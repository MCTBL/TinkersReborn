package mctbl.tinkersreborn.smeltery.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.entity.TinkersRebornMultiBlockInvenotryLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.smeltery.gui.GuiTinkerTank;
import mctbl.tinkersreborn.smeltery.inventory.ContainerTinkerTank;
import mctbl.tinkersreborn.smeltery.items.FilledBucket;
import mctbl.tinkersreborn.smeltery.network.TinkerTankFluidUpdatePacket;

public class TinkerTankLogic extends TinkersRebornMultiBlockInvenotryLogic implements IFluidTank {

    private static final int MAX_TANK_SIZE = 9;
    private static final int BUCKET_INPUT_SLOT = 0;
    private static final int BUCKET_OUTPUT_SLOT = 1;
    public static final int MB_PER_BLOCK_CAPACITY = 4000; // 4B each block
    public static final String FLUID_LIST = "Fluids";

    public final List<FluidStack> fluids = new ArrayList<>();
    public int maxMoltenMetalAmount;
    public int currentMoltenMetalAmount;

    public IInventory buckets = new InventoryBasic("tinkertank.bucket", false, 2);

    protected final List<Block> validFrame;

    public TinkerTankLogic() {
        super("tinkertank", TinkersRebornSmeltery.tankController);
        this.validFrame = new ArrayList<>();
        this.validWall.addAll(
            Arrays.asList(
                this.controller,
                TinkersRebornSmeltery.smelteryDrain,
                TinkersRebornSmeltery.smelteryBlock,
                TinkersRebornSmeltery.soulGlass,
                TinkersRebornSmeltery.searedGlass));
        this.validCeiling.addAll(
            Arrays.asList(
                TinkersRebornSmeltery.smelteryBlock,
                TinkersRebornSmeltery.smelteryDrain,
                TinkersRebornSmeltery.soulGlass,
                TinkersRebornSmeltery.searedGlass));
        this.validFloor.addAll(
            Arrays.asList(this.controller, TinkersRebornSmeltery.smelteryBlock, TinkersRebornSmeltery.smelteryDrain));
        this.validFrame.addAll(Arrays.asList(TinkersRebornSmeltery.smelteryBlock, TinkersRebornSmeltery.smelteryDrain));
    }

    @Override
    protected boolean validFrameBlock(Block b, BlockPos pos) {
        return this.validFrame.contains(b);
    }

    @Override
    public int getMaxMultiSize() {
        return MAX_TANK_SIZE;
    }

    @Override
    public boolean hasCeilingLayer() {
        return true;
    }

    @Override
    public boolean hasFrame() {
        return true;
    }

    @Override
    public boolean hasFloorLayer() {
        return true;
    }

    @Override
    protected void adjustLayers() {
        super.adjustLayers();
        this.maxMoltenMetalAmount = MB_PER_BLOCK_CAPACITY * this.calculateInnerBlockCount();
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        if (this.getActive()) {
            return new ContainerTinkerTank(inventoryplayer, this);
        } else {
            return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        if (this.getActive()) {
            return new GuiTinkerTank((ContainerTinkerTank) getGuiContainer(inventoryplayer, world, x, y, z), this);
        } else {
            return null;
        }
    }

    @Override
    public FluidStack getFluid() {
        if (!this.fluids.isEmpty()) {
            return this.fluids.get(0);
        }
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
        int canFill = MathHelper
            .clamp_int(resource.amount, 0, this.maxMoltenMetalAmount - this.currentMoltenMetalAmount);

        if (doFill) {
            boolean isAdded = false;
            for (FluidStack s : this.fluids) {
                if (s.isFluidEqual(resource)) {
                    s.amount += canFill;
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded) {
                FluidStack copyFluid = resource.copy();
                copyFluid.amount = canFill;
                this.fluids.add(copyFluid);
            }

            this.currentMoltenMetalAmount += canFill;
            this.onTankChanged(fluids);
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
                if (fluid.amount <= 0) this.fluids.remove(fluid);
                this.onTankChanged(fluids);
            }
            return copy;
        }
        return null;
    }

    public void fillOrClearBucket(boolean isShiftClick, EntityPlayer player) {
        ItemStack bucket = buckets.getStackInSlot(BUCKET_INPUT_SLOT);
        if (bucket == null || buckets.getStackInSlot(BUCKET_OUTPUT_SLOT) != null) {
            return;
        }

        if (bucket.getItem() instanceof FilledBucket || FluidContainerRegistry.isFilledContainer(bucket)) {
            emptyContainerIntoTank(bucket, isShiftClick, player);
            return;
        }

        if (FluidContainerRegistry.isEmptyContainer(bucket)) {
            fillContainerFromTank(bucket, isShiftClick, player);
        }
    }

    private void emptyContainerIntoTank(ItemStack bucket, boolean isShiftClick, EntityPlayer player) {
        FluidStack containedFluid;
        ItemStack emptyContainer;

        // FilledBucket uses NBT to distinguish fluids, which FluidContainerRegistry ignores in 1.7.10.
        if (bucket.getItem() instanceof FilledBucket filledBucket) {
            TinkersRebornFluid fluid = filledBucket.getFluidStackInBucket(bucket);
            if (fluid == null) {
                return;
            }

            containedFluid = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
            emptyContainer = new ItemStack(Items.bucket);
        } else {
            containedFluid = FluidContainerRegistry.getFluidForFilledItem(bucket);
            emptyContainer = FluidContainerRegistry.drainFluidContainer(bucket);
        }

        if (containedFluid == null || emptyContainer == null) {
            return;
        }

        int accepted = this.fill(containedFluid, false);
        if (accepted != containedFluid.amount) {
            return;
        }

        this.fill(containedFluid, true);
        finishContainerOperation(emptyContainer, isShiftClick, player);
    }

    private void fillContainerFromTank(ItemStack bucket, boolean isShiftClick, EntityPlayer player) {
        FluidStack availableFluid = this.getFluid();
        if (availableFluid == null || availableFluid.amount <= 0) {
            return;
        }

        ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(availableFluid, bucket);
        int drainAmount = FluidContainerRegistry.getContainerCapacity(availableFluid, bucket);

        if (filledContainer == null && availableFluid.getFluid() instanceof TinkersRebornFluid fluid) {
            filledContainer = TinkersRebornGeneral.tinkersBucket.getNewFluidBucketWithMaterial(fluid.identifier);
            drainAmount = FluidContainerRegistry.BUCKET_VOLUME;
        }

        if (filledContainer == null || drainAmount <= 0) {
            return;
        }

        FluidStack drainedFluid = this.drain(drainAmount, false);
        if (drainedFluid == null || drainedFluid.amount != drainAmount) {
            return;
        }

        this.drain(drainAmount, true);
        finishContainerOperation(filledContainer, isShiftClick, player);
    }

    private void finishContainerOperation(ItemStack result, boolean isShiftClick, EntityPlayer player) {
        boolean returnedToPlayer = false;
        if (isShiftClick && player != null) {
            returnedToPlayer = player.inventory.addItemStackToInventory(result);
            player.inventory.markDirty();
        }

        if (!returnedToPlayer) {
            buckets.setInventorySlotContents(BUCKET_OUTPUT_SLOT, result);
        }

        buckets.decrStackSize(BUCKET_INPUT_SLOT, 1);
    }

    /**
     * used by click in gui make that fluid to first output
     *
     * @param fluid
     */
    public void moveFluidToFirst(FluidStack fluid) {
        if (fluid == null) return;

        FluidStack target = fluid;
        Iterator<FluidStack> iterator = this.fluids.iterator();
        FluidStack inside = null;

        while (iterator.hasNext() && (inside = iterator.next()) != null) {
            if (inside.isFluidEqual(target)) {
                target = inside;
                iterator.remove();
                break;
            }
        }
        this.fluids.add(0, target);
    }

    public void moveFluidToFirst(int idx) {
        if (idx < 0 || idx >= this.fluids.size()) return;

        FluidStack fluidStack = this.fluids.get(idx);
        this.fluids.remove(idx);
        this.fluids.add(0, fluidStack);
    }

    @SideOnly(Side.CLIENT)
    public void updateFluidsFromPacket(List<FluidStack> fluids) {
        this.fluids.clear();
        this.fluids.addAll(fluids);
        this.currentMoltenMetalAmount = fluids.stream()
            .map(s -> s.amount)
            .reduce(0, Integer::sum);
        // Trigger chunk re-render so SmelteryRender reflects the updated fluid order
        if (this.worldObj != null) {
            this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public void onTankChanged(List<FluidStack> fluids) {
        // notify clients of liquid changes.
        // the null check is to prevent potential crashes during loading
        if (!this.worldObj.isRemote) {
            TinkerNetwork.sendToAll(new TinkerTankFluidUpdatePacket(this.getBlockPos(), fluids));
        }
        // tell the chunk the tank changed
        this.markDirty();
    }

    public FluidTankInfo[] getMultiTankInfo() {
        List<FluidTankInfo> collect = this.fluids.stream()
            .map(fluidStack -> new FluidTankInfo(fluidStack.copy(), fluidStack.amount))
            .collect(Collectors.toList());
        collect.add(new FluidTankInfo(null, this.maxMoltenMetalAmount - this.currentMoltenMetalAmount));
        return collect.toArray(new FluidTankInfo[] {});
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        this.readMoltenFluidFromNBT(tags);
    }

    private void readMoltenFluidFromNBT(NBTTagCompound tags) {
        NBTTagList fluidList = tags.getTagList(FLUID_LIST, 10);
        this.fluids.clear();
        int tagCount = fluidList.tagCount();
        for (int i = 0; i < tagCount; i++) {
            FluidStack fs = FluidStack.loadFluidStackFromNBT(fluidList.getCompoundTagAt(i));
            if (fs != null) {
                this.fluids.add(fs);
            }
        }
        this.maxMoltenMetalAmount = tags.getInteger("MaxMoltenMetalAmount");
        this.currentMoltenMetalAmount = tags.getInteger("CurrentMoltenMetalAmount");
        this.blocksPerLayer = tags.getInteger("BlocksPerLayer");
        this.multiLayers = tags.getInteger("MultiLayers");
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        this.writeMoltenFluidFromNBT(tags);
    }

    private void writeMoltenFluidFromNBT(NBTTagCompound tags) {
        NBTTagList fluidList = new NBTTagList();
        for (FluidStack fs : this.fluids) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            fs.writeToNBT(fluidTag);
            fluidList.appendTag(fluidTag);
        }
        tags.setTag(FLUID_LIST, fluidList);
        tags.setInteger("MaxMoltenMetalAmount", this.maxMoltenMetalAmount);
        tags.setInteger("CurrentMoltenMetalAmount", this.currentMoltenMetalAmount);
        tags.setInteger("BlocksPerLayer", this.blocksPerLayer);
        tags.setInteger("MultiLayers", this.multiLayers);
    }

}
