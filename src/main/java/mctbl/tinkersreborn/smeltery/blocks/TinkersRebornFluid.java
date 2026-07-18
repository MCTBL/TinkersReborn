package mctbl.tinkersreborn.smeltery.blocks;

import net.minecraftforge.fluids.Fluid;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

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
        return createMolten(name, color, identifier, 300);
    }

    public static TinkersRebornFluid createMolten(String name, int color, String identifier, int temperature) {
        TinkersRebornFluid f = new TinkersRebornFluid("molten_" + name, color, identifier);
        f.setDensity(3000)
            .setViscosity(6000)
            .setTemperature(temperature)
            .setLuminosity(12);
        return f;
    }

    @Override
    public String getLocalizedName() {
        TinkersRebornMaterial material = TinkersRebornRegistry.getMaterialByIdentifier(identifier);
        String name = material != TinkersRebornMaterial.UNKNOWN ? material.localizedName()
            : TinkersRebornUtils.translate(this.getUnlocalizedName());

        if (getTemperature() > 300) {
            name = TinkersRebornUtils.translate("tinkersreborn.moltenFluid")
                .replace("%%material", name);
        }
        return name;
    }

    @Override
    public int getColor() {
        return this.color;
    }
}
