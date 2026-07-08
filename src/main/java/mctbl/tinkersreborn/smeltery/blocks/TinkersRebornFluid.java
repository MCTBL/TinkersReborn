package mctbl.tinkersreborn.smeltery.blocks;

import net.minecraftforge.fluids.Fluid;

public class TinkersRebornFluid extends Fluid {

    public final int color;
    public final String identifier;

    /**
     * @param fluidName  iron.molten / blood
     * @param color      0xFFC1C1C1
     * @param identifier texture name like molten_iron / liquid_blood
     */
    public TinkersRebornFluid(String fluidName, int color, String identifier) {
        super(fluidName);
        this.color = color;
        this.identifier = identifier;
    }

    public static TinkersRebornFluid createMolten(String name, int color, String identifier) {
        TinkersRebornFluid f = new TinkersRebornFluid("molten_" + name, color, identifier);
        f.setDensity(3000)
            .setViscosity(6000)
            .setTemperature(1300)
            .setLuminosity(12);
        return f;
    }

    public int getColor() {
        return color;
    }
}
