package mctbl.tinkersreborn.library.utils;

import net.minecraftforge.fluids.FluidStack;

public interface IGuiLiquidTank {

    FluidStack getFluidStackAtPosition(int mouseX, int mouseY);
}
