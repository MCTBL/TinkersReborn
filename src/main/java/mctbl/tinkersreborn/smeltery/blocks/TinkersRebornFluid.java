package mctbl.tinkersreborn.smeltery.blocks;

import net.minecraftforge.fluids.Fluid;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;

public class TinkersRebornFluid extends Fluid {

    private int color;

    public TinkersRebornFluid(String fluidName, int materialId) {
        super(fluidName);
        this.color = TinkersRebornRegistry.getMaterialById(materialId).materialTextColor;
    }

    @Override
    public int getColor() {
        return this.color;
    }

}
