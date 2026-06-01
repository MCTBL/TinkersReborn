package mctbl.tinkersreborn.library.materials;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TinkersRebornMaterial {

    public static final String LOC_Name = "material.%s.name";
    public static final String LOC_Prefix = "material.%s.prefix";

    // How much the different items are "worth"
    // the values are used for both liquid conversion as well as part crafting
    public static final int VALUE_Ingot = 144;
    public static final int VALUE_Nugget = VALUE_Ingot / 9;
    public static final int VALUE_Fragment = VALUE_Ingot / 4;
    public static final int VALUE_Shard = VALUE_Ingot / 2;

    public static final int VALUE_Gem = 666; // divisible by 3!
    public static final int VALUE_Block = VALUE_Ingot * 9;

    public static final int VALUE_SearedBlock = VALUE_Ingot * 2;
    public static final int VALUE_SearedMaterial = VALUE_Ingot / 2;
    public static final int VALUE_Glass = 1000;
    public static final int VALUE_Bucket = 1000;

    public static final int VALUE_BrickBlock = VALUE_Ingot * 4;

    public static final int VALUE_SlimeBall = 250;

    /**
     * This String uniquely identifies a material.
     */
    public final String identifier;
    public final String localizationIdentifier;

    /** The fluid associated with this material, can be null */
    protected Fluid fluid;

    /** TinkersRebornMaterial can be crafted into parts in the PartBuilder */
    protected boolean craftable;

    /**
     * TinkersRebornMaterial can be cast into parts using the Smeltery and a Cast.
     * Fluid must be NON NULL
     */
    protected boolean castable;

    /**
     * This item, if it is not null, represents the material for rendering. In
     * general if you want to give a person this material, you can give them this
     * item.
     */
    private ItemStack representativeItem = null;

    /**
     * Ore name that represents this material
     */
    private String representativeOre = null;

    /**
     * This item will be used instead of the generic shard item when returning
     * leftovers.
     */
    private ItemStack shardItem = null;

    public final int materialId;
    public int materialTextColor = 0xffffff; // used in tooltips and other text. Saved in NBT.

    public final Map<MaterialStatusType, IMaterialStats> statsMap;

    public static final TinkersRebornMaterial UNKNOWN = new TinkersRebornMaterial(
        -1,
        "unknown",
        EnumChatFormatting.WHITE);
    static {
        // UNKNOWN.set
    }

    public TinkersRebornMaterial(int id, String identifier, EnumChatFormatting textColor) {
        this(id, identifier, ColorUtil.enumChatFormattingToColor(textColor));
    }

    public TinkersRebornMaterial(int id, String identifier, int color) {
        this.materialId = id;
        this.identifier = TinkersRebornUtils.sanitizeLocalizationString(identifier); // lowercases and removes
        this.localizationIdentifier = String.format(LOC_Name, identifier);

        // if invisible, make it fully opaque.
        if (((color >> 24) & 0xFF) == 0) {
            color |= 0xFF << 24;
        }

        this.materialTextColor = color;
        this.statsMap = new HashMap<>();
    }

    public TinkersRebornMaterial addStats(IMaterialStats m) {
        this.statsMap.put(m.getIdentifier(), m);
        return this;
    }

    public TinkersRebornMaterial addStats(List<IMaterialStats> l) {
        for (IMaterialStats m : l) this.addStats(m);
        return this;
    }

    public TinkersRebornMaterial addStats(IMaterialStats... l) {
        return this.addStats(Arrays.asList(l));
    }

    @SuppressWarnings("unchecked")
    public <T extends IMaterialStats> T getStats(MaterialStatusType t) {
        Class<? extends IMaterialStats> statusClass = t.getStatusClass();
        IMaterialStats obj = this.statsMap.get(t);
        if (obj != null && statusClass.isInstance(obj)) {
            return (T) statusClass.cast(this.statsMap.get(t));
        } else {
            return null;
        }
    }

    public Collection<? extends IMaterialStats> getAllStats() {
        return this.statsMap.values();
    }

    public boolean hasStats(MaterialStatusType t) {
        return this.statsMap.containsKey(t);
    }

    public boolean isCraftable() {
        return this.craftable;
    }

    public boolean hasFluid() {
        return fluid != null;
    }

    public Fluid getFluid() {
        return this.fluid;
    }

    public boolean isCastable() {
        return hasFluid() && this.castable;
    }

    /** Setting this to true allows to craft parts in the PartBuilder */
    public TinkersRebornMaterial setCraftable(boolean craftable) {
        this.craftable = craftable;
        return this;
    }

    /** Associates this fluid with the material. Used for melting/casting items. */
    public TinkersRebornMaterial setFluid(Fluid fluid) {
        if (fluid != null && !FluidRegistry.isFluidRegistered(fluid)) {
            TinkersReborn.LOG.warn("Materials cannot have an unregistered fluid associated with them!");
        }
        this.fluid = fluid;
        return this;
    }

    /**
     * Setting this to true allows to cast parts of this material. NEEDS TO HAVE A
     * FLUID SET BEFOREHAND!
     */
    public TinkersRebornMaterial setCastable(boolean castable) {
        this.castable = castable;
        return this;
    }

    public TinkersRebornMaterial setFluidAndCastable(Fluid fluid) {
        this.setFluid(fluid);
        this.setCastable(true);
        return this;
    }

    public String localizedName() {
        return StatCollector.translateToLocal(localizationIdentifier);
    }

    public ItemStack getRepresentativeItem() {
        return representativeItem;
    }

    public String getRepresentativeOre() {
        return representativeOre;
    }

    public ItemStack getShardItem() {
        return shardItem;
    }

    public TinkersRebornMaterial setRepresentativeItem(ItemStack representativeItem) {
        this.representativeItem = representativeItem;
        return this;
    }

    public TinkersRebornMaterial setRepresentativeOre(String representativeOre) {
        this.representativeOre = representativeOre;
        return this;
    }

    public TinkersRebornMaterial setShardItem(ItemStack shardItem) {
        this.shardItem = shardItem;
        return this;
    }

}
