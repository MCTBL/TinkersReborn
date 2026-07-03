package mctbl.tinkersreborn.smeltery.utils;

import net.minecraftforge.fluids.FluidStack;

public class TinkersRebornFuelRecord {

    private FluidStack fuel;
    private int fuelDuration;

    /**
     * @param fuel
     * @param fuelDuration
     */
    public TinkersRebornFuelRecord(FluidStack fuel, int fuelDuration) {
        this.fuel = fuel;
        this.fuelDuration = fuelDuration;
    }

    public FluidStack getFuel() {
        return fuel;
    }

    public int getFuelDuration() {
        return fuelDuration;
    }

}
