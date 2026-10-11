package mctbl.tinkersreborn.smeltery.utils;

import net.minecraftforge.fluids.FluidStack;

public class TinkersRebornFuelRecord {

    private FluidStack fuel;
    private int fuelDuration;
    private int temperatureMulti;

    /**
     * @param fuel
     * @param fuelDuration
     */
    public TinkersRebornFuelRecord(FluidStack fuel, int fuelDuration, int temperatureMulti) {
        this.fuel = fuel;
        this.fuelDuration = fuelDuration;
        this.temperatureMulti = temperatureMulti;
    }

    public FluidStack getFuel() {
        return fuel;
    }

    public int getFuelDuration() {
        return fuelDuration;
    }

    public int getTemperatureMulti() {
        return temperatureMulti;
    }

}
