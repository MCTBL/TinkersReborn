package mctbl.tinkersreborn.library.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.library.utils.FuelInfo;
import mctbl.tinkersreborn.smeltery.entity.LavaTankLogic;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;
import mctbl.tinkersreborn.smeltery.network.HeatingStructureFuelUpdatePacket;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public abstract class TinkersRebornHeatableMultiBlockLogic extends TinkersRebornMultiBlockInvenotryLogic {

    public int blocksPerLayer;
    public int multiLayers;
    protected Block controller;
    /**
     * last time consume fluid stack
     */
    public FluidStack currentFuel;
    protected BlockPos activeLavaTank;
    protected final List<BlockPos> lavaTanks;
    /**
     * Ticks left until the current fuel is depleted and fuel is taken from the
     * tanks. Depletes every tick
     */
    public int fuelReleaseTicks;
    // amount of fuel gotten from a single consumption of the fluid, used for GUI
    // fuel percentage
    public int fuelTotalTicks;
    public boolean needsFuel; // If the last tick executed an operation that required fuel.
    protected int temperature = 20; // internal temperature of the heater == speed of the heater
    public boolean isHeating = false; // If the last tick is heating item insde.
    protected int[] itemTemperatures; // current temperature of each item in the corresponding slot
    protected int[] itemTempRequired; // Temperature where the items want to goooooo    
    public static final String TAG_FUEL_RELEASE = "fuelRelease";
    public static final String TAG_TEMPERATURE = "temperature";
    public static final String TAG_NEEDS_FUEL = "needsFuel";
    public static final String TAG_ITEM_TEMPERATURES = "itemTemperatures";
    public static final String TAG_ITEM_TEMP_REQUIRED = "itemTempRequired";
    public static final String TAG_IS_HEATING = "isHeating";
    protected static final int INIT_TEMPERATURES = 20; // ℃

    protected TinkersRebornHeatableMultiBlockLogic(String name, Block block) {
        super(name);
        this.controller = block;
        this.lavaTanks = new ArrayList<>();
        this.itemTemperatures = new int[0];
        this.itemTempRequired = new int[0];
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
            }
        }
        this.blocksPerLayer = 0;
        this.multiLayers = 0;
    }

    protected void resizeTemperatures(int newSize) {
        int[] oldTemperatures = this.itemTemperatures;
        int[] oldTempRequired = this.itemTempRequired;
        this.itemTemperatures = new int[newSize];
        this.itemTempRequired = new int[newSize];

        Arrays.fill(this.itemTemperatures, INIT_TEMPERATURES * 10);
        Arrays.fill(this.itemTempRequired, INIT_TEMPERATURES * 10);

        int loopIdx = Math.min(oldTemperatures.length, this.itemTemperatures.length);
        for (int idx = 0; idx < loopIdx; idx++) {
            this.itemTemperatures[idx] = oldTemperatures[idx];
            this.itemTempRequired[idx] = oldTempRequired[idx];
        }
    }

    protected void setTempRequiredForSlot(int index, int heat) {
        if (index < itemTempRequired.length) {
            itemTempRequired[index] = heat * TIME_FACTOR;
        }
    }

    public int getTemperature(int i) {
        if (i < 0 || i >= this.itemTemperatures.length) {
            return 0;
        }
        return this.itemTemperatures[i];
    }

    public int getTempRequired(int i) {
        if (i < 0 || i >= this.itemTempRequired.length) {
            return 0;
        }
        return this.itemTempRequired[i] / TIME_FACTOR;
    }

    public float getHeatingProgress(int index) {
        if (index < 0 || index > getSizeInventory() - 1) {
            return -1f;
        }

        if (!canHeat(index)) {
            return -1f;
        }

        return getProgress(index);
    }

    public boolean canHeat(int index) {
        return temperature >= getTempRequired(index);
    }

    protected int heatSlot(int i) {
        return temperature / 100; // if your heater has <100 heat then it deserves to not create any heat .
    }

    public float getProgress(int index) {
        if (index >= itemTemperatures.length) {
            return 0f;
        }
        return (float) itemTemperatures[index] / (float) itemTempRequired[index];
    }

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

    protected void consumeFuel() {
        if (!this.needsFuel) {
            return;
        }

        // get current tank
        this.searchForFuel();

        // got a tank?
        if (this.activeLavaTank != null) {
            // consume fuel!
            TileEntity te = this.worldObj
                .getTileEntity(this.activeLavaTank.x, this.activeLavaTank.y, this.activeLavaTank.z);
            if (te instanceof LavaTankLogic tankLogic) {
                FluidStack liquid = tankLogic.getFluid();
                if (liquid != null) {
                    FluidStack in = liquid.copy();
                    int bonusFuel = TinkersRebornRegistry.consumeSmelteryFuel(in);
                    int amount = liquid.amount - in.amount;
                    FluidStack drained = tankLogic.drain(null, amount, false);

                    // we can drain. actually drain and add the fuel
                    if (drained != null && drained.amount == amount) {
                        tankLogic.drain(null, amount, true);
                        this.currentFuel = drained.copy();
                        this.fuelReleaseTicks = bonusFuel;
                        this.addFuel(
                            bonusFuel,
                            Math.round(
                                TinkersRebornUtils.transferKelvinToCelsius(
                                    drained.getFluid()
                                        .getTemperature())));
                        // convert to degree celcius

                        // notify client of fuel/temperature changes
                        if (!this.worldObj.isRemote) {
                            TinkerNetwork.sendToAll(
                                new HeatingStructureFuelUpdatePacket(
                                    this.getBlockPos(),
                                    activeLavaTank,
                                    temperature,
                                    currentFuel));
                        }

                        return;
                    }
                }

                this.fuelReleaseTicks = 0;
            }
        }

    }

    protected void addFuel(int fuel, int newTemperature) {
        this.fuelTotalTicks = fuel;
        this.needsFuel = false;
        this.temperature = newTemperature;
    }

    /**
     * Locates a tank containing fuel, if one exists
     *
     * @return true if successful
     */
    private void searchForFuel() {
        // is the current tank still up to date?
        if (this.activeLavaTank != null && this.hasTankWithFuel(this.activeLavaTank)) {
            return;
        }

        // nope, current tank is empty, check others for same fuel
        for (BlockPos pos : this.lavaTanks) {
            if (this.hasTankWithFuel(pos)) {
                this.activeLavaTank = pos;
                return;
            }
        }

        // nothing found, try again with new fuel
        this.currentFuel = null;
        for (BlockPos pos : this.lavaTanks) {
            if (this.hasTankWithFuel(pos)) {
                this.activeLavaTank = pos;
                return;
            }
        }

        this.activeLavaTank = null;
    }

    // checks if the given location has a fluid tank that contains fuel
    private boolean hasTankWithFuel(BlockPos pos) {
        LavaTankLogic tank = getTankAt(pos);
        if (tank != null && tank.getFluid() != null) {
            if (tank.getFluidAmount() > 0 && TinkersRebornRegistry.isSmelteryFuel(tank.getFluid())) {
                // if we have a preference, only use that
                return this.currentFuel == null || tank.getFluid()
                    .isFluidEqual(this.currentFuel);
            }
        }

        return false;
    }

    /**
     * Grabs the tank at the given location (if present)
     */
    @Nullable
    protected LavaTankLogic getTankAt(BlockPos pos) {
        TileEntity te = this.worldObj.getTileEntity(pos.x, pos.y, pos.z);
        if (te instanceof LavaTankLogic logic) {
            return logic;
        }

        return null;
    }

    /**
     * Calculate the heat required for the given slot
     */
    public abstract void updateTempRequired(int index);

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);

        tags.setInteger(TAG_FUEL_RELEASE, fuelReleaseTicks);
        tags.setInteger(TAG_TEMPERATURE, temperature);
        tags.setBoolean(TAG_NEEDS_FUEL, needsFuel);
        tags.setIntArray(TAG_ITEM_TEMPERATURES, itemTemperatures);
        tags.setIntArray(TAG_ITEM_TEMP_REQUIRED, itemTempRequired);
        tags.setBoolean(TAG_IS_HEATING, isHeating);

        tags.setInteger(TAG_FUEL_QUALITY, fuelTotalTicks);

        tags.setTag(TAG_CURRENT_TANK, writePos(activeLavaTank));
        NBTTagList tankList = new NBTTagList();
        for (BlockPos pos : lavaTanks) {
            tankList.appendTag(writePos(pos));
        }
        tags.setTag(TAG_TANKS, tankList);

        NBTTagCompound fuelTag = new NBTTagCompound();
        if (currentFuel != null) {
            currentFuel.writeToNBT(fuelTag);
        }

        tags.setTag(TAG_CURRENT_FUEL, fuelTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        fuelReleaseTicks = tags.getInteger(TAG_FUEL_RELEASE);
        temperature = tags.getInteger(TAG_TEMPERATURE);
        needsFuel = tags.getBoolean(TAG_NEEDS_FUEL);
        itemTemperatures = tags.getIntArray(TAG_ITEM_TEMPERATURES);
        itemTempRequired = tags.getIntArray(TAG_ITEM_TEMP_REQUIRED);
        isHeating = tags.getBoolean(TAG_IS_HEATING);
        fuelTotalTicks = tags.getInteger(TAG_FUEL_QUALITY);

        activeLavaTank = readPos(tags.getCompoundTag(TAG_CURRENT_TANK));
        NBTTagList tankList = tags.getTagList(TAG_TANKS, 10);
        lavaTanks.clear();
        for (int i = 0; i < tankList.tagCount(); i++) {
            lavaTanks.add(readPos(tankList.getCompoundTagAt(i)));
        }
        currentFuel = FluidStack.loadFluidStackFromNBT(tags.getCompoundTag(TAG_CURRENT_FUEL));
        super.readFromNBT(tags);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        // reset heat if set to null or a different item
        if (TinkersRebornUtils.isStackEmpty(itemstack) || (!TinkersRebornUtils.isStackEmpty(getStackInSlot(slot))
            && !ItemStack.areItemStacksEqual(itemstack, getStackInSlot(slot)))) {
            itemTemperatures[slot] = 0;
        }
        super.setInventorySlotContents(slot, itemstack);

        // when an item gets added, check for its heat required
        updateTempRequired(slot);
    }

    /**
     * Called when an item finished heating up. Return true if the processing was
     * successful, then the heating data will be cleared.
     */
    protected abstract boolean onItemFinishedHeating(ItemStack stack, int slot);

    @SideOnly(Side.CLIENT)
    public void updateFuelTemperatureFromPacket(HeatingStructureFuelUpdatePacket packet) {
        this.temperature = packet.temperature;
        this.currentFuel = packet.fuel;
        this.activeLavaTank = packet.tank;
    }

    @SideOnly(Side.CLIENT)
    public void updateFuelFromPacket(int index, int fuel) {
        if (index == 0) {
            this.fuelReleaseTicks = fuel;
        } else if (index == 1) {
            this.fuelTotalTicks = fuel;
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateTemperatureFromPacket(int index, int heat) {
        if (index < 0 || index > getSizeInventory() - 1) {
            return;
        }

        this.itemTemperatures[index] = heat;
    }

    @SideOnly(Side.CLIENT)
    public void updateTempRequiredFromPacket(int index, int heat) {
        if (index < 0 || index > getSizeInventory() - 1) {
            return;
        }

        this.itemTempRequired[index] = heat;
    }

    @SideOnly(Side.CLIENT)
    public FuelInfo getFuelDisplay() {
        FuelInfo info = new FuelInfo();

        if (this.activeLavaTank != null && hasTankWithFuel(activeLavaTank)) {
            // we need to consume fuel, check the current tank
            LavaTankLogic tank = getTankAt(activeLavaTank);
            if (tank != null) {
                FluidStack tankFluid = tank.getFluid();

                info.fluid = tankFluid.copy();
                info.heat = temperature + 273;
                info.maxCap = tank.getCapacity();
            }
        }

        // check all other tanks (except the current one that we already checked) for
        // more fuel
        for (BlockPos pos : this.lavaTanks) {
            if (pos.equals(activeLavaTank)) {
                continue;
            }

            LavaTankLogic tank = getTankAt(pos);
            // tank exists and has something in it
            if (tank != null && tank.getFluidAmount() > 0) {

                // we don't have fuel yet, use this
                if (info.fluid == null) {
                    info.fluid = tank.getFluid()
                        .copy();
                    info.heat = info.fluid.getFluid()
                        .getTemperature(info.fluid);
                    info.maxCap = tank.getCapacity();
                }
                // otherwise add the same together
                else if (tank.getFluid()
                    .isFluidEqual(info.fluid)) {
                        info.fluid.amount += tank.getFluidAmount();
                        info.maxCap += tank.getCapacity();
                    }
            }
        }

        return info;
    }

    protected void updateIfChanged(boolean heatedItem) {
        if (heatedItem != isHeating) {
            isHeating = heatedItem;
        }
    }
}
