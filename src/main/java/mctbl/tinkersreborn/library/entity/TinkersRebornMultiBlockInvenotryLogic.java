package mctbl.tinkersreborn.library.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.common.network.TinkerNetwork;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.IActiveLogic;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.library.utils.FuelInfo;
import mctbl.tinkersreborn.smeltery.entity.LavaTankLogic;
import mctbl.tinkersreborn.smeltery.network.HeatingStructureFuelUpdatePacket;
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
    public static final String TAG_FUEL_RELEASE = "fuelRelease";
    public static final String TAG_TEMPERATURE = "temperature";
    public static final String TAG_NEEDS_FUEL = "needsFuel";
    public static final String TAG_ITEM_TEMPERATURES = "itemTemperatures";
    public static final String TAG_ITEM_TEMP_REQUIRED = "itemTempRequired";
    public static final String TAG_IS_HEATING = "isHeating";

    public static final BlockPos DEFAULT_POS = BlockPos.of(0, 0, 0);

    /** smallest coordinate INSIDE the multiblock */
    public BlockPos minPos = DEFAULT_POS;
    /** biggest coordinate INSIDE the multiblock */
    public BlockPos maxPos = DEFAULT_POS;

    public boolean validStructure;
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

    protected int tickCounter = 0;
    protected int secondCounter = 0;

    protected boolean needsUpdate;

    protected int temperature = 20; // internal temperature of the heater == speed of the heater
    public boolean isHeating = false; // If the last tick is heating item insde.

    protected int[] itemTemperatures; // current temperature of each item in the corresponding slot
    protected int[] itemTempRequired; // Temperature where the items want to goooooo

    protected static final int INIT_TEMPERATURES = 20; // ℃

    /**
     * used by {@link #checkSteppingingValid()} and {@link #stepNextInnerPos()}
     */
    protected BlockPos nextCheckInner;

    /**
     * @see #getDefaultName
     */
    protected String name;

    public TinkersRebornMultiBlockInvenotryLogic(String name) {
        super(0);
        this.itemTemperatures = new int[0];
        this.itemTempRequired = new int[0];
        this.lavaTanks = new ArrayList<>();
        this.name = "tinkersreborn.multi." + name;
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
            if (te instanceof LavaTankLogic) {
                IFluidTank tank = ((LavaTankLogic) te).tank;

                FluidStack liquid = tank.getFluid();
                if (liquid != null) {
                    FluidStack in = liquid.copy();
                    int bonusFuel = TinkersRebornRegistry.consumeSmelteryFuel(in);
                    int amount = liquid.amount - in.amount;
                    FluidStack drained = tank.drain(amount, false);

                    // we can drain. actually drain and add the fuel
                    if (drained != null && drained.amount == amount) {
                        tank.drain(amount, true);
                        this.currentFuel = drained.copy();
                        this.fuelReleaseTicks = bonusFuel;
                        this.addFuel(
                            bonusFuel,
                            Math.round(
                                TinkersRebornUtils.transferFahrenheitToCelsius(
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
        IFluidTank tank = getTankAt(pos);
        if (tank != null && tank.getFluid() != null) {
            if (tank.getFluidAmount() > 0 && TinkersRebornRegistry.isSmelteryFuel(tank.getFluid())) {
                // if we have a preference, only use that
                if (this.currentFuel == null || tank.getFluid()
                    .isFluidEqual(this.currentFuel)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Grabs the tank at the given location (if present)
     */
    private IFluidTank getTankAt(BlockPos pos) {
        TileEntity te = this.worldObj.getTileEntity(pos.x, pos.y, pos.z);
        if (te instanceof LavaTankLogic) {
            return ((LavaTankLogic) te).tank;
        }

        return null;
    }

    /**
     * Calculate the heat required for the given slot
     */
    protected abstract void updateHeatRequired(int index);

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);

        tags.setBoolean(TAG_ACTIVE, validStructure);
        tags.setTag(TAG_MINPOS, writePos(minPos));
        tags.setTag(TAG_MAXPOS, writePos(maxPos));

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
        super.readFromNBT(tags);

        validStructure = tags.getBoolean(TAG_ACTIVE);
        minPos = readPos(tags.getCompoundTag(TAG_MINPOS));
        maxPos = readPos(tags.getCompoundTag(TAG_MAXPOS));
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

        // needsUpdate = !worldObj.isRemote;
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
        for (int idx = 0; idx < loopIdx; idx++) {
            this.inventory[idx] = oldInv[idx];
        }
        for (int idx = loopIdx; idx < oldInv.length; idx++) {
            if (oldInv[idx] != null && oldInv[idx].stackSize != 0) TinkersRebornUtils.dropItemAtPos(
                this.worldObj,
                this.getBlockPos()
                    .offset(this.faceDirection),
                oldInv[idx]);
        }
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
        return this.itemTempRequired[i];
    }

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

        // we still have leftover fuel
        if (this.fuelReleaseTicks > 0) {
            // if the current fuel is null, something in the fluid registry changed
            // just replace it with lava and ignore for now, it will fix next time we
            // consume fuel
            if (currentFuel == null) {
                info.fluid = new FluidStack(FluidRegistry.LAVA, 0);
                info.maxCap = 1;
            } else {
                info.fluid = currentFuel.copy();
                info.fluid.amount = 0;
                info.maxCap = currentFuel.amount;
            }
            info.heat = this.temperature + 300;
        } else if (this.activeLavaTank != null) {
            // we need to consume fuel, check the current tank
            if (hasTankWithFuel(activeLavaTank)) {
                IFluidTank tank = getTankAt(activeLavaTank);
                assert tank != null;
                FluidStack tankFluid = tank.getFluid();
                assert tankFluid != null;
                info.fluid = tankFluid.copy();
                info.heat = temperature + 300;
                info.maxCap = tank.getCapacity();
            }
        }

        // check all other tanks (except the current one that we already checked) for
        // more fuel
        for (BlockPos pos : this.lavaTanks) {
            if (pos == activeLavaTank) {
                continue;
            }

            IFluidTank tank = getTankAt(pos);
            // tank exists and has something in it
            if (tank != null && tank.getFluidAmount() > 0) {
                assert tank.getFluid() != null;
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
}
