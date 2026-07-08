package mctbl.tinkersreborn.library.materials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.ITrait;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.library.utils.RecipeMatchRegistry;
import mctbl.tinkersreborn.tools.materials.BowMaterialStats;
import mctbl.tinkersreborn.tools.materials.ExtraMaterialStats;
import mctbl.tinkersreborn.tools.materials.FletchingMaterialStats;
import mctbl.tinkersreborn.tools.materials.HandleMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.materials.ProjectileMaterialStats;
import mctbl.tinkersreborn.tools.materials.ShaftMaterialStats;
import mctbl.tinkersreborn.tools.materials.StringMaterialStats;
import mctbl.tinkersreborn.util.ColorUtil;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TinkersRebornMaterial extends RecipeMatchRegistry {

    static final String LOC_Name = "material.%s.name";
    static final String LOC_Prefix = "material.%s.prefix";

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
    public final String localizationPrefix;

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

    // public final int materialId;
    public int materialTextColor = 0xffffff; // used in tooltips and other text. Saved in NBT.

    // we use a specific map for 2 reasons:
    // * A Map so we can obtain the stats we want quickly
    // * the linked map to ensure the order when iterating
    public final Map<MaterialStatusType, IMaterialStats> statsMap;
    /** Stat-ID -> Traits */
    public final Map<MaterialStatusType, List<ITrait>> traits;

    public static int VALUE_Ore() {
        return (int) (VALUE_Ingot * TinkersRebornConfig.oreToIngotRatio);
    }

    public static final TinkersRebornMaterial UNKNOWN = new TinkersRebornMaterial("unknown", EnumChatFormatting.WHITE);
    static {
        UNKNOWN.addStats(new HeadMaterialStats(1, 1, 1, 0));
        UNKNOWN.addStats(new HandleMaterialStats(1f, 0));
        UNKNOWN.addStats(new ExtraMaterialStats(0));
        UNKNOWN.addStats(new BowMaterialStats(1f, 1f, 0f));
        UNKNOWN.addStats(new StringMaterialStats(1f));
        UNKNOWN.addStats(new ShaftMaterialStats(1f, 0));
        UNKNOWN.addStats(new FletchingMaterialStats(1f, 1f));
        UNKNOWN.addStats(new ProjectileMaterialStats());
    }

    public TinkersRebornMaterial(String identifier, EnumChatFormatting textColor) {
        this(identifier, ColorUtil.enumChatFormattingToColor(textColor));
    }

    public TinkersRebornMaterial(String identifier, int color) {
        // this.materialId = id;
        this.identifier = TinkersRebornUtils.sanitizeLocalizationString(identifier); // lowercases and removes
        this.localizationIdentifier = String.format(LOC_Name, this.identifier);

        String tempPrefix = String.format(LOC_Prefix, this.identifier);
        this.localizationPrefix = StatCollector.canTranslate(tempPrefix) ? tempPrefix : this.localizationIdentifier;

        // if invisible, make it fully opaque.
        if (((color >> 24) & 0xFF) == 0) {
            color |= 0xFF << 24;
        }

        this.materialTextColor = color;
        this.statsMap = new LinkedHashMap<>();
        this.traits = new LinkedHashMap<>();
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

    public Set<Entry<MaterialStatusType, IMaterialStats>> getAlltatusType() {
        return this.statsMap.entrySet();
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

    public String localizedPrefix() {
        return StatCollector.translateToLocal(localizationPrefix);
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

    public void setRepresentativeItem(String representativeOre) {
        this.representativeOre = representativeOre;
    }

    public void setRepresentativeItem(Item representativeItem) {
        this.setRepresentativeItem(new ItemStack(representativeItem));
    }

    public void setRepresentativeItem(Block representativeBlock) {
        this.setRepresentativeItem(new ItemStack(representativeBlock));
    }

    public TinkersRebornMaterial setRepresentativeItem(ItemStack representativeItem) {
        if (this.representativeItem == null) {
            this.representativeItem = representativeItem;
        }
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

    /**
     * Obtains the list of traits for the given stat, creates it if it doesn't exist
     * yet.
     */
    protected List<ITrait> getStatTraits(MaterialStatusType stats) {
        return this.traits.computeIfAbsent(stats, k -> new LinkedList<>());
    }

    /**
     * Returns whether the material has a trait with that identifier.
     */
    public boolean hasTrait(String identifier, MaterialStatusType stats) {
        if (identifier == null || identifier.isEmpty()) {
            return false;
        }

        for (ITrait trait : this.getAllTraitsForStats(stats)) {
            if (trait.getIdentifier()
                .equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    public List<ITrait> getAllTraitsForStats(MaterialStatusType staus) {
        List<ITrait> list = new ArrayList<>();
        if (this.traits.containsKey(staus)) {
            list.addAll(this.traits.get(staus));
        }
        if (this.traits.containsKey(null)) {
            list.addAll(this.traits.get(null));
        }
        return list;
    }

    /**
     * Adds the trait as the default trait, will be used if no more specific one is
     * present time.
     */
    public TinkersRebornMaterial addTrait(IModifier materialTrait) {
        return addTrait(materialTrait, null);
    }

    /**
     * Adds the trait to be added if the specified stats are used.
     */
    public TinkersRebornMaterial addTrait(IModifier materialTrait, MaterialStatusType staus) {
        if (TinkersRebornRegistry.checkMaterialTrait(this, materialTrait, staus)) {
            getStatTraits(staus).add((ITrait) materialTrait);
        }
        return this;
    }

    public void addCommonItems(String oredictSuffix) {
        this.addItem("ingot" + oredictSuffix, 1, VALUE_Ingot);
        this.addItem("nugget" + oredictSuffix, 1, VALUE_Nugget);
        this.addItem("block" + oredictSuffix, 1, VALUE_Block);
    }

    public TinkersRebornMaterial setShard(ItemStack shard) {
        Optional<RecipeMatch.Match> matchOptional = matches(shard);
        if (matchOptional.isPresent()) {
            RecipeMatch.Match match = matchOptional.get();
            if (match.amount == VALUE_Shard) {
                this.shardItem = shard;
                if (TinkersRebornUtils.isStackEmpty(representativeItem)) {
                    this.setRepresentativeItem(shardItem.copy());
                }
            }
        }
        return this;
    }

    public static final class RenderMaterial extends TinkersRebornMaterial {

        public RenderMaterial(String identifier, int color) {
            super(identifier, color);
        }
    }
}
