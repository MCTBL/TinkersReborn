package mctbl.tinkersreborn.smeltery.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.crafting.AlloyRecipe;
import mctbl.tinkersreborn.library.entity.TinkersRebornSearedMultiBlockLogic;
import mctbl.tinkersreborn.library.event.TinkerSmelteryEvent;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.smeltery.gui.GuiSmeltery;
import mctbl.tinkersreborn.smeltery.inventory.ContainerSmeltery;
import mctbl.tinkersreborn.smeltery.items.FilledBucket;
import mctbl.tinkersreborn.smeltery.network.SmelteryFluidUpdatePacket;
import mctbl.tinkersreborn.smeltery.utils.MeltingRecipe;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class SmelteryLogic extends TinkersRebornSearedMultiBlockLogic implements IFluidTank {

    public static final DamageSource smelteryDamage = new DamageSource("smeltery").setFireDamage();

    private static final int BUCKET_INPUT_SLOT = 0;
    private static final int BUCKET_OUTPUT_SLOT = 1;
    public static final int MB_PER_BLOCK_CAPACITY = TinkersRebornMaterial.VALUE_Ingot * 10;
    protected static final int ALLOYING_PER_TICK = 10; // how much liquid can be created per tick to make alloys
    public static final String MOLTEN_METAL_LIST = "MoltenMetal";

    protected final List<BlockPos> drains;

    public final List<FluidStack> moltenMetal = new ArrayList<>();
    public int maxMoltenMetalAmount;
    public int currentMoltenMetalAmount;

    public IInventory buckets = new InventoryBasic("smeltery.bucket", false, 2);

    public SmelteryLogic() {
        super("smeltery", TinkersRebornSmeltery.smelteryController);
        this.drains = new ArrayList<>();
    }

    @Override
    protected void heatItemsPost() {
        alloyAlloys();
    }

    @Override
    protected void tickPost() {
        if (this.tickCounter == 0) {
            this.interactWithEntitiesInside();
            // called every second, we check every 15s or so
            if (++this.secondCounter >= 15) {
                this.secondCounter = 0;
                this.checkWholeStructureValid();
            } else {
                this.checkSteppingingValid();
            }
        }
    };

    @Override
    protected boolean hasTopLayer() {
        return false;
    }

    @Override
    protected boolean hasBottmLayer() {
        return true;
    }

    // This is how you get blisters
    protected void interactWithEntitiesInside() {
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(
            minPos.getX(),
            minPos.getY(),
            minPos.getZ(),
            maxPos.getX() + 1,
            maxPos.getY() + 1,
            maxPos.getZ() + 1);
        List<Entity> entitiesInsideSmeltery = this.worldObj.getEntitiesWithinAABB(Entity.class, bb);
        for (Entity entity : entitiesInsideSmeltery) {
            // item?
            if (entity instanceof EntityItem entityItem) {
                if (TinkersRebornRegistry.getMelting(entityItem.getEntityItem()) != null) {
                    ItemStack stack = entityItem.getEntityItem();
                    // pick it up if we can melt it
                    for (int i = 0; i < this.getSizeInventory(); i++) {
                        if (!isStackInSlot(i)) {
                            // remove 1 from the stack and add it to the smeltery
                            ItemStack invStack = stack.copy();
                            stack.stackSize -= 1;
                            invStack.stackSize = 1;
                            this.setInventorySlotContents(i, invStack);
                        }
                        if (stack.stackSize == 0) {
                            // picked up whole stack
                            entity.setDead();
                            break;
                        }
                    }
                    this.markDirty();
                }
            } else
                if (entity instanceof EntityLivingBase && entity.isEntityAlive() && this.currentMoltenMetalAmount > 0) {
                    // we only melt living entities
                    FluidStack meltingForEntity = TinkersRebornRegistry.getMeltingForEntity(entity);
                    // no custom melting, there will be blood
                    if (meltingForEntity == null) {
                        meltingForEntity = new FluidStack(TinkersRebornGeneral.bloodFluid, 20);
                    }
                    // hurt it
                    if (entity.attackEntityFrom(smelteryDamage, 2f)) {
                        // spill the blood
                        this.fill(meltingForEntity.copy(), true);
                    }
                }
        }
    }

    // check for alloys and create them
    protected void alloyAlloys() {
        if (this.currentMoltenMetalAmount > this.maxMoltenMetalAmount) {
            return;
        }
        for (AlloyRecipe recipe : TinkersRebornRegistry.getAlloys()) {
            if (!recipe.isValid()) {
                continue;
            }
            // find out how often we can apply the recipe
            int matched = recipe.matches(this.moltenMetal);
            if (matched > ALLOYING_PER_TICK) {
                matched = ALLOYING_PER_TICK;
            }
            while (matched > 0) {
                // remove all liquids from the tank
                for (FluidStack liquid : recipe.getFluids()) {
                    FluidStack toDrain = liquid.copy();
                    FluidStack drained = this.drain(toDrain, true);
                    if (!drained.isFluidEqual(toDrain) || drained.amount != toDrain.amount) {
                        TinkersReborn.LOG.error(
                            "Smeltery alloy creation drained incorrect amount: was {}:{}, should be {}:{}",
                            drained.getUnlocalizedName(),
                            drained.amount,
                            toDrain.getUnlocalizedName(),
                            toDrain.amount);
                    }
                }

                // and insert the alloy
                FluidStack toFill = recipe.getResult()
                    .copy();
                int filled = this.fill(toFill, true);
                if (filled != recipe.getResult().amount) {
                    TinkersReborn.LOG.error(
                        "Smeltery alloy creation filled incorrect amount: was {}, should be {} ({})",
                        filled,
                        recipe.getResult().amount * matched,
                        recipe.getResult()
                            .getUnlocalizedName());
                    break;
                }
                matched -= filled;
            }
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public FluidStack getFluid() {
        if (!this.moltenMetal.isEmpty()) {
            return this.moltenMetal.get(0);
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
        int canFill = Math.max(0, Math.min(resource.amount, this.maxMoltenMetalAmount - this.currentMoltenMetalAmount));

        if (doFill) {
            boolean isAdded = false;
            for (FluidStack s : this.moltenMetal) {
                if (s.isFluidEqual(resource)) {
                    s.amount += canFill;
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
            this.onTankChanged(moltenMetal);
        }

        return canFill;
    }

    public FluidStack drain(FluidStack resource, boolean doDrain) {
        // search for the resource
        ListIterator<FluidStack> iter = this.moltenMetal.listIterator();
        while (iter.hasNext()) {
            FluidStack liquid = iter.next();
            if (liquid.isFluidEqual(resource)) {
                int drainable = Math.min(resource.amount, liquid.amount);
                if (doDrain) {
                    liquid.amount -= drainable;
                    this.currentMoltenMetalAmount -= drainable;
                    if (liquid.amount <= 0) {
                        iter.remove();
                    }
                    this.onTankChanged(this.moltenMetal);
                }

                // return drained amount
                resource = resource.copy();
                resource.amount = drainable;
                return resource;
            }
        }

        // nothing drained
        return null;
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
                this.onTankChanged(moltenMetal);
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

    public void moveFluidToFirst(int idx) {
        if (idx < 0 || idx >= this.moltenMetal.size()) return;

        FluidStack fluidStack = this.moltenMetal.get(idx);
        this.moltenMetal.remove(idx);
        this.moltenMetal.add(0, fluidStack);
    }

    @Override
    protected void reset(List<BlockPos> tempValidBlockList) {
        super.reset(tempValidBlockList);
        this.maxMoltenMetalAmount = 0;
    }

    @Override
    protected void updateTempRequired(int index) {
        ItemStack stack = getStackInSlot(index);
        if (!TinkersRebornUtils.isStackEmpty(stack)) {
            MeltingRecipe melting = TinkersRebornRegistry.getMelting(stack);
            if (melting != null) {
                setTempRequiredForSlot(index, Math.max(5, melting.getUsableTemperature()));

                // instantly consume fuel if required
                if (fuelReleaseTicks <= 0) {
                    consumeFuel();
                }

                return;
            }
        }
        setTempRequiredForSlot(index, 0);
    }

    public void onTankChanged(List<FluidStack> fluids) {
        // notify clients of liquid changes.
        // the null check is to prevent potential crashes during loading
        if (!this.worldObj.isRemote) {
            TinkerNetwork.sendToAll(new SmelteryFluidUpdatePacket(this.getBlockPos(), fluids));
        }
        // tell the chunk the tank changed
        this.markDirty();
    }

    @SideOnly(Side.CLIENT)
    public void updateFluidsFromPacket(List<FluidStack> fluids) {
        this.moltenMetal.clear();
        this.moltenMetal.addAll(fluids);
        this.currentMoltenMetalAmount = fluids.stream()
            .map(s -> s.amount)
            .reduce(0, Integer::sum);
        // Trigger chunk re-render so SmelteryRender reflects the updated fluid order
        if (this.worldObj != null) {
            this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new ContainerSmeltery(inventoryplayer, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getGui(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
        return new GuiSmeltery((ContainerSmeltery) getGuiContainer(inventoryplayer, world, x, y, z), this);
    }

    // melt stuff
    @Override
    protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
        // skip if full, as there is no case where we can melt an item into a full
        // smeltery
        // TODO: might be better to instead cache the amount of space needed per slot,
        // so for a less than full smeltery we don't need to find the recipe again if
        // still full
        if (currentMoltenMetalAmount >= maxMoltenMetalAmount) {
            // set error state for the UI
            itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
            return false;
        }
        MeltingRecipe recipe = TinkersRebornRegistry.getMelting(stack);

        if (recipe == null) {
            return false;
        }

        TinkerSmelteryEvent.OnMelting event = TinkerSmelteryEvent.OnMelting
            .fireEvent(this, stack, recipe.output.copy());

        FluidStack fluidStack = getValidFluidStackOrNull(event.result);
        int filled = fill(fluidStack, false);

        if (fluidStack != null && filled == fluidStack.amount) {
            fill(fluidStack, true);

            // only clear out items n stuff if it was successful
            setInventorySlotContents(slot, null);
            return true;
        } else {
            // can't fill into the smeltery, set error state
            itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
        }

        return false;
    }

    /**
     * Used to ensure that a fluidstack is valid. Basically when you return a
     * fluidstack, you should ALWAYS take the fluid from the FluidRegistry. This
     * isn't possible in all cases for us hence we replace the FluidStack with a
     * FluidStack containing the correct fluid.
     *
     * Example: Entity X melts into a specific fluid with specific NBT. However in
     * game Fluid X is not the default fluid anymore. We change the returned stack
     * to contain the default fluid instead of the fluid used during setup.
     *
     * @return A save FluidStack or null if there is no valid fluid for the
     *         fluidstack
     */
    public static FluidStack getValidFluidStackOrNull(FluidStack possiblyInvalidFluidstack) {
        FluidStack fluidStack = possiblyInvalidFluidstack;
        if (!FluidRegistry.isFluidDefault(fluidStack.getFluid())) {
            Fluid fluid = FluidRegistry.getFluid(
                fluidStack.getFluid()
                    .getName());
            if (fluid != null) {
                fluidStack = new FluidStack(fluid, fluidStack.amount, fluidStack.tag);
            } else {
                fluidStack = null;
            }
        }
        return fluidStack;
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        this.readMoltenFluidFromNBT(tags);
    }

    private void readMoltenFluidFromNBT(NBTTagCompound tags) {
        NBTTagList fluidList = tags.getTagList(MOLTEN_METAL_LIST, 10);
        this.moltenMetal.clear();
        int tagCount = fluidList.tagCount();
        for (int i = 0; i < tagCount; i++) {
            FluidStack fs = FluidStack.loadFluidStackFromNBT(fluidList.getCompoundTagAt(i));
            if (fs != null) {
                this.moltenMetal.add(fs);
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
        for (FluidStack fs : this.moltenMetal) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            fs.writeToNBT(fluidTag);
            fluidList.appendTag(fluidTag);
        }
        tags.setTag(MOLTEN_METAL_LIST, fluidList);
        tags.setInteger("MaxMoltenMetalAmount", this.maxMoltenMetalAmount);
        tags.setInteger("CurrentMoltenMetalAmount", this.currentMoltenMetalAmount);
        tags.setInteger("BlocksPerLayer", this.blocksPerLayer);
        tags.setInteger("MultiLayers", this.multiLayers);
    }

    public FluidTankInfo[] getMultiTankInfo() {
        List<FluidTankInfo> collect = this.moltenMetal.stream()
            .map(fluidStack -> new FluidTankInfo(fluidStack.copy(), fluidStack.amount))
            .collect(Collectors.toList());
        collect.add(new FluidTankInfo(null, this.maxMoltenMetalAmount - this.currentMoltenMetalAmount));
        return collect.toArray(new FluidTankInfo[] {});
    }

    public void fillOrClearBucket(boolean isShiftClick, EntityPlayer player) {
        ItemStack bucket = buckets.getStackInSlot(BUCKET_INPUT_SLOT);
        if (bucket == null || buckets.getStackInSlot(BUCKET_OUTPUT_SLOT) != null) {
            return;
        }

        if (bucket.getItem() instanceof FilledBucket || FluidContainerRegistry.isFilledContainer(bucket)) {
            emptyContainerIntoSmeltery(bucket, isShiftClick, player);
            return;
        }

        if (FluidContainerRegistry.isEmptyContainer(bucket)) {
            fillContainerFromSmeltery(bucket, isShiftClick, player);
        }
    }

    private void emptyContainerIntoSmeltery(ItemStack bucket, boolean isShiftClick, EntityPlayer player) {
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

    private void fillContainerFromSmeltery(ItemStack bucket, boolean isShiftClick, EntityPlayer player) {
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
}
