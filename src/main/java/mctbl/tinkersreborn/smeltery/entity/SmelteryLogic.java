package mctbl.tinkersreborn.smeltery.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.crafting.AlloyRecipe;
import mctbl.tinkersreborn.library.entity.TinkersRebornMultiBlockInvenotryLogic;
import mctbl.tinkersreborn.library.event.TinkerSmelteryEvent;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.smeltery.TinkersRebornSmeltery;
import mctbl.tinkersreborn.smeltery.gui.GuiSmeltery;
import mctbl.tinkersreborn.smeltery.inventory.ContainerSmeltery;
import mctbl.tinkersreborn.smeltery.network.SmelteryFluidUpdatePacket;
import mctbl.tinkersreborn.smeltery.utils.MeltingRecipe;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class SmelteryLogic extends TinkersRebornMultiBlockInvenotryLogic implements IFluidTank {

    public static final DamageSource smelteryDamage = new DamageSource("smeltery").setFireDamage();

    private static final int MAX_SMELTERY_SIZE = 7;
    public static final int MB_PER_BLOCK_CAPACITY = TinkersRebornMaterial.VALUE_Ingot * 10;
    protected static final int ALLOYING_PER_TICK = 10; // how much liquid can be created per tick to make alloys
    public static final String MOLTEN_METAL_LIST = "MoltenMetal";

    protected final List<BlockPos> drains;

    public final List<FluidStack> moltenMetal = new ArrayList<>();
    public int maxMoltenMetalAmount;
    public int currentMoltenMetalAmount;
    public int blocksPerLayer;
    public int multiLayers;

    public SmelteryLogic() {
        super("smeltery");
        this.drains = new ArrayList<>();
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) return;

        if ((!this.getActive() && this.tickCounter == 0) || this.needsUpdate) {
            // check for smeltery once per second
            this.needsUpdate = false;
            this.checkWholeStructureValid();
            this.isHeating = false;
        } else if (this.getActive()) {
            // smeltery structure is there.. do stuff with the current fuel
            // this also updates the needsFuel flag, which causes us to consume fuel at the
            // end.
            // This way fuel is only consumed if it's actually needed
            if (tickCounter % TinkersRebornConfig.heatItemsTickrateSmeltery == 0) {
                heatItems();
                alloyAlloys();
            }

            if (this.needsFuel) {
                this.consumeFuel();
            }

            // we gradually check if the inside of the smeltery is blocked (for performance
            // reasons)
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
        }

        this.tickCounter = (this.tickCounter + 1) % 20;
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
        boolean checkUpper = true;
        boolean checkLower = true;
        int yd1 = 0;
        int yd2 = 1;

        List<BlockPos> tempValidBlockList = new ArrayList<>();

        while (checkUpper || checkLower) {
            if (checkUpper && isValidLayer(center, range, center.y + yd1, tempValidBlockList)) {
                yd1++;
                validLayerCount++;
            } else {
                checkUpper = false;
            }
            if (checkLower) {
                if (isValidLayer(center, range, center.y - yd2, tempValidBlockList)) {
                    yd2++;
                    validLayerCount++;
                    continue;
                } else if (isValidBottom(center, range, center.y - yd2, tempValidBlockList)) {
                    hasBottmLayer = true;
                }
                checkLower = false;
            }
        }

        if (hasBottmLayer && validLayerCount > 0 && !this.lavaTanks.isEmpty()) {
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
            this.setActive(false);
            this.temperature = INIT_TEMPERATURES;
            this.maxMoltenMetalAmount = 0;
            // reset fuel state to prevent stale values when structure is rebuilt
            this.fuelReleaseTicks = 0;
            this.fuelTotalTicks = 0;
            this.currentFuel = null;
            this.needsFuel = false;
            this.activeLavaTank = null;
            for (BlockPos b : tempValidBlockList) {
                TileEntity tempEntiry = this.worldObj.getTileEntity(b.x, b.y, b.z);
                if (tempEntiry instanceof MultiServantLogic servant && servant.getHasMaster()
                    && servant.getMasterPosition()
                        .equals(masterPos))
                    servant.removeMaster();
            }
            this.blocksPerLayer = 0;
            this.multiLayers = 0;
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
        this.blocksPerLayer = (this.maxPos.x - this.minPos.x + 1) * (this.maxPos.z - this.minPos.z + 1);
        this.multiLayers = (this.maxPos.y - this.minPos.y + 1);
        int innerBlockCount = this.blocksPerLayer * multiLayers;
        this.resizeInventory(innerBlockCount);
        this.resizeTemperatures(innerBlockCount);
        this.maxMoltenMetalAmount = MB_PER_BLOCK_CAPACITY * innerBlockCount;
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
}
