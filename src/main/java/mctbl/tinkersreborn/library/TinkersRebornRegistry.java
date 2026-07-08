package mctbl.tinkersreborn.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial.RenderMaterial;
import mctbl.tinkersreborn.library.smeltery.CastingRecipe;
import mctbl.tinkersreborn.library.smeltery.ICastingRecipe;
import mctbl.tinkersreborn.library.smeltery.PreferenceCastingRecipe;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.smeltery.utils.MaterialIntegration;
import mctbl.tinkersreborn.smeltery.utils.MeltingRecipe;
import mctbl.tinkersreborn.smeltery.utils.TinkersRebornFuelRecord;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.items.Pattern;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TinkersRebornRegistry {

    public static TinkersRebornRegistry instance = new TinkersRebornRegistry();

    private TinkersRebornRegistry() {}

    public static TinkersRebornCreativeTab blockTab;
    public static TinkersRebornCreativeTab toolsTab;
    public static TinkersRebornCreativeTab weaponsTab;
    public static TinkersRebornCreativeTab partsTab;
    public static TinkersRebornCreativeTab miscTab;

    protected static final List<ToolCore> tools = new ArrayList<>();
    protected static final Map<String, ToolCore> toolNameMap = new HashMap<>();
    protected static final Map<String, TinkersRebornToolPart> toolPartNameMap = new HashMap<>();
    protected static final List<ToolCore> toolStationCrafting = new ArrayList<>();
    protected static final List<ToolCore> toolForgeCrafting = new ArrayList<>();

    protected static final List<TinkersRebornMaterial> allMaterialsList = new ArrayList<>();
    protected static final Map<String, TinkersRebornMaterial> materialIdentifierMaps = new HashMap<>();
    protected static final Map<String, RenderMaterial> renderMaterials = new HashMap<>();

    protected static final Map<String, IModifier> modifierAndTraitIdentifierMaps = new LinkedHashMap<>();

    // contains all fluid that tinkers reborn registered
    protected static final Map<String, TinkersRebornFluid> allTinkersFluidMap = new LinkedHashMap<>();
    protected static final Map<Fluid, TinkersRebornFuelRecord> allAllowFuel = new LinkedHashMap<>();
    protected static final List<FluidStack> fluidForCast = new ArrayList<>();

    protected static final Map<Class<? extends EntityLivingBase>, ItemStack> headDrops = new HashMap<>();
    protected static final List<MeltingRecipe> meltingRegistry = new ArrayList<>();
    protected static final List<ICastingRecipe> tableCastRegistry = new ArrayList<>();
    protected static final List<ICastingRecipe> basinCastRegistry = new ArrayList<>();

    protected static final Map<String, FluidStack> entityMeltingRegistry = new HashMap<>();
    protected static final List<MaterialIntegration> materialIntegrations = new ArrayList<>();
    protected static final List<ItemStack> meltingBlacklist = new ArrayList<>();

    protected static final Map<Fluid, Set<Pair<String, Integer>>> knownOreFluids = new HashMap<>();

    public void preInit() {
        this.initCreativeTab();
        this.initRegistry();

    }

    private void initCreativeTab() {
        // TODO remember init after postInit to switch TinkersReborn's Item
        blockTab = new TinkersRebornCreativeTab("TinkersRebornBlocks").init(new ItemStack(Items.cookie));
        toolsTab = new TinkersRebornCreativeTab("TinkersRebornTools").init(new ItemStack(Items.flint_and_steel));
        weaponsTab = new TinkersRebornCreativeTab("TinkersRebornWeapons").init(new ItemStack(Items.diamond_boots));
        partsTab = new TinkersRebornCreativeTab("TinkersRebornParts").init(new ItemStack(Items.bow));
        miscTab = new TinkersRebornCreativeTab("TinkersRebornMisc").init(new ItemStack(Items.iron_pickaxe));
    }

    private void initRegistry() {
        this.initRenderMaterial();
    }

    private void initRenderMaterial() {
        renderMaterials.put("_internal_render1", new RenderMaterial("_internal_render1", 0XC1C1C1));
        renderMaterials.put("_internal_render2", new RenderMaterial("_internal_render2", 0X684E1E));
        renderMaterials.put("_internal_render3", new RenderMaterial("_internal_render3", 0X2376DD));
        renderMaterials.put("_internal_render4", new RenderMaterial("_internal_render4", 0X7146B0));
    }

    public static void addMaterialToMap(TinkersRebornMaterial m) {
        materialIdentifierMaps.put(m.identifier, m);
        allMaterialsList.add(m);
    }

    public static TinkersRebornMaterial getMaterialByIdentifier(String identifier) {
        return materialIdentifierMaps.getOrDefault(identifier, TinkersRebornMaterial.UNKNOWN);
    }

    public static TinkersRebornFluid getFluidByIdentifier(String identifier) {
        return allTinkersFluidMap.get(identifier);
    }

    /**
     * Call before adding a trait to a material. Checks consistency and takes care
     * everything is in a consistent state. Registers the trait if it's not
     * registered, takes events into account.
     */
    public static boolean checkMaterialTrait(TinkersRebornMaterial material, IModifier trait,
        MaterialStatusType staus) {
        if (material == null) {
            TinkersReborn.LOG.fatal("Could not add Trait \"{}\": Material is null", trait.getIdentifier());
            return false;
        }

        addModifierAndTrait(trait);

        return true;
    }

    public static void addModifierAndTrait(IModifier trait) {
        modifierAndTraitIdentifierMaps.put(trait.getIdentifier(), trait);
    }

    public static IModifier getModifierAndTrait(String identifier) {
        return modifierAndTraitIdentifierMaps.get(identifier);
    }

    public static Collection<IModifier> getAllModifier() {
        return modifierAndTraitIdentifierMaps.values();
    }

    public static void registerTool(ToolCore tool) {
        tools.add(tool);
        toolNameMap.put(tool.toolTypeName, tool);
    }

    public static List<ToolCore> getAllTools() {
        return tools;
    }

    public static void registerToolPart(TinkersRebornToolPart part) {
        toolPartNameMap.put(part.texture, part);
    }

    public static TinkersRebornToolPart getToolPartByPartName(String name) {
        return toolPartNameMap.getOrDefault(name, null);
    }

    /**
     * Adds a tool to the Crafting UI of both the Tool Station as well as the Tool
     * Forge
     */
    public static void registerToolCrafting(ToolCore tool) {
        registerToolStationCrafting(tool);
        registerToolForgeCrafting(tool);
    }

    /** Adds a tool to the Crafting UI of the Tool Station */
    public static void registerToolStationCrafting(ToolCore tool) {
        // TODO add event?
        toolStationCrafting.add(tool);
    }

    /** Adds a tool to the Crafting UI of the Tool Forge */
    public static void registerToolForgeCrafting(ToolCore tool) {
        toolForgeCrafting.add(tool);
    }

    /**
     * Registers a beheading head drop for an entity
     * 
     * @param clazz Entity class
     * @param head  Head that drops from that entity
     */
    public static void registerHeadDrop(Class<? extends EntityLivingBase> clazz, ItemStack head) {
        headDrops.put(clazz, head);
    }

    /**
     * Gets the heads that would be dropped by an entity
     * 
     * @param entity Entity to check
     * @return A collection of the entity's head drops
     */
    public static Collection<ItemStack> getHeadDrops(EntityLivingBase entity) {
        Collection<ItemStack> drops = new ArrayList<>();
        for (Map.Entry<Class<? extends EntityLivingBase>, ItemStack> entry : headDrops.entrySet()) {
            if (entry.getKey()
                .isAssignableFrom(entity.getClass())) {
                ItemStack stack = entry.getValue();
                if (!TinkersRebornUtils.isStackEmpty(stack)) {
                    drops.add(stack.copy());
                }
            }
        }
        return drops;
    }

    /**
     * consume fuel.amount can make multi work how many ticks
     * 
     * @param fuel
     * @param tick
     */
    public static void registerFuel(FluidStack fuel, int fuelDuration) {
        allAllowFuel.put(fuel.getFluid(), new TinkersRebornFuelRecord(fuel, fuelDuration));
    }

    public static boolean isSmelteryFuel(FluidStack fluid) {
        return allAllowFuel.containsKey(fluid.getFluid());
    }

    /**
     * Reduces the fluidstack by one increment of the fuel and returns how much fuel
     * duration it gives.
     */
    public static int consumeSmelteryFuel(FluidStack in) {
        for (Map.Entry<Fluid, TinkersRebornFuelRecord> entry : allAllowFuel.entrySet()) {
            if (in.getFluid() == entry.getKey()) {
                FluidStack fuel = entry.getValue()
                    .getFuel();
                int out = entry.getValue()
                    .getFuelDuration();
                if (in.amount < fuel.amount) {
                    float coeff = (float) in.amount / (float) fuel.amount;
                    out = Math.round(coeff * out);
                    in.amount = 0;
                } else {
                    in.amount -= fuel.amount;
                }

                return out;
            }
        }

        return 0;
    }

    /**
     * Registers this item with all its metadatas to melt into amount of the given
     * fluid.
     */
    public static void registerMelting(Item item, Fluid fluid, int amount) {
        ItemStack stack = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
        registerMelting(new MeltingRecipe(new RecipeMatch.Item(stack, 1, amount), fluid));
    }

    /**
     * Registers this block with all its metadatas to melt into amount of the given
     * fluid.
     */
    public static void registerMelting(Block block, Fluid fluid, int amount) {
        ItemStack stack = new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE);
        registerMelting(new MeltingRecipe(new RecipeMatch.Item(stack, 1, amount), fluid));
    }

    /**
     * Registers this itemstack NBT-SENSITIVE to melt into amount of the given
     * fluid.
     */
    public static void registerMelting(ItemStack stack, Fluid fluid, int amount) {
        registerMelting(new MeltingRecipe(new RecipeMatch.ItemCombination(amount, stack), fluid));
    }

    public static void registerMelting(String oredict, Fluid fluid, int amount) {
        registerMelting(new MeltingRecipe(new RecipeMatch.Oredict(oredict, 1, amount), fluid));
    }

    public static void registerMelting(MeltingRecipe recipe) {
        if (Arrays.stream(TinkersRebornConfig.fluidIgnore)
            .anyMatch(
                f -> f.equals(
                    recipe.output.getFluid()
                        .getName()))) {
            return;
        }
        meltingRegistry.add(recipe);
        // if (new TinkerRegisterEvent.MeltingRegisterEvent(recipe).fire()) {
        // meltingRegistry.add(recipe);
        // } else {
        // try {
        // String input = recipe.input.getInputs().stream().findFirst().map(ItemStack::getUnlocalizedName)
        // .orElse("?");
        // log.debug("Registration of melting recipe for " + recipe.getResult().getUnlocalizedName() + " from "
        // + input + " has been cancelled by event");
        // } catch (Exception e) {
        // log.error("Error when logging melting event", e);
        // }
        // }
    }

    /** Registers a casting recipe for casting table */
    public static void registerTableCasting(ItemStack output, ItemStack cast, Fluid fluid, int amount) {
        if (Arrays.stream(TinkersRebornConfig.fluidIgnore)
            .anyMatch(f -> f.equals(fluid.getName()))) {
            return;
        }
        RecipeMatch rm = null;
        if (cast != null) {
            rm = RecipeMatch.ofNBT(cast);
        }
        registerTableCasting(new CastingRecipe(output, rm, fluid, amount));
    }

    public static void registerTableCasting(ICastingRecipe recipe) {
        // if(new TinkerRegisterEvent.TableCastingRegisterEvent(recipe).fire()) {
        tableCastRegistry.add(recipe);
        // }
        // else {
        // try {
        // String output = Optional.ofNullable(recipe.getResult(ItemStack.EMPTY,
        // FluidRegistry.WATER)).map(ItemStack::getUnlocalizedName).orElse("Unknown");
        // log.debug("Registration of table casting recipe for " + output + " has been cancelled by event");
        // } catch(Exception e) {
        // log.error("Error when logging table casting event", e);
        // }
        // }
    }

    /** Registers a casting recipe for the casting basin */
    public static void registerBasinCasting(ItemStack output, ItemStack cast, Fluid fluid, int amount) {
        if (Arrays.stream(TinkersRebornConfig.fluidIgnore)
            .anyMatch(f -> f.equals(fluid.getName()))) {
            return;
        }
        RecipeMatch rm = null;
        if (!TinkersRebornUtils.isStackEmpty(cast)) {
            rm = RecipeMatch.ofNBT(cast);
        }
        registerBasinCasting(new CastingRecipe(output, rm, fluid, amount));
    }

    public static void registerBasinCasting(ICastingRecipe recipe) {
        // if(new TinkerRegisterEvent.BasinCastingRegisterEvent(recipe).fire()) {
        basinCastRegistry.add(recipe);
        // }
        // else {
        // try {
        // String output = Optional.ofNullable(recipe.getResult(ItemStack.EMPTY,
        // FluidRegistry.WATER)).map(ItemStack::getUnlocalizedName).orElse("Unknown");
        // log.debug("Registration of basin casting recipe for " + output + " has been cancelled by event");
        // } catch(Exception e) {
        // log.error("Error when logging basin casting event", e);
        // }
        // }
    }

    public static MeltingRecipe getMelting(ItemStack stack) {
        for (MeltingRecipe recipe : meltingRegistry) {
            if (recipe.matches(stack)) {
                return recipe;
            }
        }

        return null;
    }

    public static List<MeltingRecipe> getAllMeltingRecipies() {
        return ImmutableList.copyOf(meltingRegistry);
    }

    public static void integrate(MaterialIntegration mi) {
        materialIntegrations.add(mi);
    }

    public static List<MaterialIntegration> getMaterialIntegrations() {
        return materialIntegrations;
    }

    /**
     * Called by MaterialIntegration's to register tool part recipes
     * 
     * @param material
     */
    public static void registerToolpartMeltingCasting(TinkersRebornMaterial material) {
        // melt ALL the toolparts n stuff. Also cast them.
        Fluid fluid = material.getFluid();
        for (TinkersRebornToolPart toolPart : TinkersRebornRegistry.toolPartNameMap.values()) {

            ItemStack stack = toolPart.getNewPartWithMaterial(material);
            ItemStack cast = Pattern.newStackWithToolPart(toolPart);

            if (fluid != null && stack != null) {
                // melting
                registerMelting(stack, fluid, toolPart.getCost());
                // casting
                registerTableCasting(stack, cast, fluid, toolPart.getCost());
            }
            if (stack != null) {
                // register cast creation from the toolparts
                for (FluidStack fs : fluidForCast) {
                    registerTableCasting(new CastingRecipe(cast, RecipeMatch.ofNBT(stack), fs, true, true));
                }
            }

        }

        // same for shard
        if (TinkersRebornTools.castShard != null) {
            ItemStack stack = TinkersRebornTools.shard.getNewPartWithMaterial(material);
            int cost = TinkersRebornTools.shard.cost;

            if (fluid != null) {
                // melting
                registerMelting(stack, fluid, cost);
                // casting
                registerTableCasting(stack, TinkersRebornTools.castShard, fluid, cost);
            }
            // register cast creation from the toolparts
            for (FluidStack fs : fluidForCast) {
                registerTableCasting(
                    new CastingRecipe(TinkersRebornTools.castShard, RecipeMatch.ofNBT(stack), fs, true, true));
            }
        }
    }

    /**
     * Registers melting for all directly supported pre- and suffixes of the ore.
     * E.g. "Iron" -> "ingotIron", "blockIron", "oreIron",
     */
    @SuppressWarnings("unchecked")
    public static void registerOredictMeltingCasting(Fluid fluid, String ore) {
        ImmutableSet.Builder<Pair<String, Integer>> builder = ImmutableSet.builder();
        Pair<String, Integer> nuggetOre = Pair.of("nugget" + ore, TinkersRebornMaterial.VALUE_Nugget);
        Pair<String, Integer> ingotOre = Pair.of("ingot" + ore, TinkersRebornMaterial.VALUE_Ingot);
        Pair<String, Integer> blockOre = Pair.of("block" + ore, TinkersRebornMaterial.VALUE_Block);
        Pair<String, Integer> oreOre = Pair.of("ore" + ore, TinkersRebornMaterial.VALUE_Ore());
        Pair<String, Integer> oreNetherOre = Pair.of("oreNether" + ore, (int) (2 * TinkersRebornMaterial.VALUE_Ore()));
        Pair<String, Integer> oreDenseOre = Pair.of("denseore" + ore, (int) (3 * TinkersRebornMaterial.VALUE_Ore()));
        Pair<String, Integer> orePoorOre = Pair.of("orePoor" + ore, (int) (3 * TinkersRebornMaterial.VALUE_Ore()));
        Pair<String, Integer> oreNuggetOre = Pair.of("oreNugget" + ore, (int) (TinkersRebornMaterial.VALUE_Ore()));
        Pair<String, Integer> plateOre = Pair.of("plate" + ore, TinkersRebornMaterial.VALUE_Ingot);
        Pair<String, Integer> gearOre = Pair.of("gear" + ore, TinkersRebornMaterial.VALUE_Ingot * 4);
        Pair<String, Integer> dustOre = Pair.of("dust" + ore, TinkersRebornMaterial.VALUE_Ingot);

        builder.add(
            nuggetOre,
            ingotOre,
            blockOre,
            oreOre,
            oreNetherOre,
            oreDenseOre,
            orePoorOre,
            oreNuggetOre,
            plateOre,
            gearOre,
            dustOre);
        Set<Pair<String, Integer>> knownOres = builder.build();

        // register oredicts
        addKnownOreFluid(fluid, knownOres);

        // register oredict castings!
        // ingot casting
        registerTableCasting(
            new PreferenceCastingRecipe(
                ingotOre.getLeft(),
                RecipeMatch.ofNBT(TinkersRebornTools.castIngot),
                fluid,
                ingotOre.getRight()));
        // nugget casting
        registerTableCasting(
            new PreferenceCastingRecipe(
                nuggetOre.getLeft(),
                RecipeMatch.ofNBT(TinkersRebornTools.castNugget),
                fluid,
                nuggetOre.getRight()));
        // block casting
        registerBasinCasting(
            new PreferenceCastingRecipe(
                blockOre.getLeft(),
                null, // no cast
                fluid,
                blockOre.getRight()));

        // plate casting
        // registerTableCasting(new PreferenceCastingRecipe(plateOre.getLeft(), RecipeMatch.ofNBT(castPlate), fluid,
        // plateOre.getRight()));
        // gear casting
        // registerTableCasting(
        // new PreferenceCastingRecipe(gearOre.getLeft(), RecipeMatch.ofNBT(castGear), fluid, gearOre.getRight()));

        // and also cast creation!
        for (FluidStack fs : fluidForCast) {
            registerTableCasting(
                new CastingRecipe(TinkersRebornTools.castIngot, RecipeMatch.of(ingotOre.getLeft()), fs, true, true));
            registerTableCasting(
                new CastingRecipe(TinkersRebornTools.castNugget, RecipeMatch.of(nuggetOre.getLeft()), fs, true, true));
            // registerTableCasting(new CastingRecipe(castPlate, RecipeMatch.of(plateOre.getLeft()), fs, true, true));
            // registerTableCasting(new CastingRecipe(castGear, RecipeMatch.of(gearOre.getLeft()), fs, true, true));
        }
    }

    /**
     * Adds a fluid to the knownOreFluids list, adding recipes for each combination
     * 
     * @param fluid     Fluid recipes belong to
     * @param knownOres Set of pairs of an oredict name to a integer fluid amount
     */
    private static void addKnownOreFluid(Fluid fluid, Set<Pair<String, Integer>> knownOres) {
        if (Arrays.stream(TinkersRebornConfig.fluidIgnore)
            .anyMatch(f -> f.equals(fluid.getName()))) {
            return;
        }
        for (Pair<String, Integer> pair : knownOres) {
            registerMelting(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
        }

        knownOreFluids.put(fluid, knownOres);
    }

    public static List<TinkersRebornMaterial> getAllMaterialList() {
        return allMaterialsList;
    }

    public static RenderMaterial getRenderMaterial(String identifier) {
        return renderMaterials.get(identifier);
    }

    public static Map<String, RenderMaterial> getRenderMaterialMap() {
        return renderMaterials;
    }

    public static Map<String, TinkersRebornFluid> getAllFluidMap() {
        return allTinkersFluidMap;
    }

    public static List<ToolCore> getToolForgeCraftingList() {
        return toolForgeCrafting;
    }

    public static List<ToolCore> getToolStationCraftingList() {
        return toolStationCrafting;
    }

    public static ICastingRecipe getTableCasting(ItemStack cast, Fluid fluid) {
        for (ICastingRecipe recipe : tableCastRegistry) {
            if (recipe.matches(cast, fluid)) {
                return recipe;
            }
        }
        return null;
    }

    public static ICastingRecipe getBasinCasting(ItemStack cast, Fluid fluid) {
        for (ICastingRecipe recipe : basinCastRegistry) {
            if (recipe.matches(cast, fluid)) {
                return recipe;
            }
        }
        return null;
    }

    public static void addFluidForCast() {
        fluidForCast.add(new FluidStack(TinkersRebornTools.goldFluid, TinkersRebornMaterial.VALUE_Ingot * 2));
    }
}
