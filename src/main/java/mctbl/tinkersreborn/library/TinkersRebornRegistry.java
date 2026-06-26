package mctbl.tinkersreborn.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.crafting.LiquidCasting;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial.RenderMaterial;
import mctbl.tinkersreborn.library.tools.IModifier;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TinkersRebornRegistry {

    public static TinkersRebornRegistry instance = new TinkersRebornRegistry();

    private TinkersRebornRegistry() {}

    public static TinkersRebornCreativeTab blockTab;
    public static TinkersRebornCreativeTab toolsTab;
    public static TinkersRebornCreativeTab weaponsTab;
    public static TinkersRebornCreativeTab partsTab;
    public static TinkersRebornCreativeTab miscTab;

    public static List<ToolCore> tools;
    public static Map<String, ToolCore> toolNameMap;
    public static List<ToolCore> toolStationCrafting;
    public static List<ToolCore> toolForgeCrafting;

    public static List<TinkersRebornMaterial> allMaterialsList;
    public static Map<String, TinkersRebornMaterial> materialIdentifierMaps;
    public static Map<Integer, TinkersRebornMaterial> materialIdMaps;
    public static Map<String, RenderMaterial> renderMaterials;

    public static Map<String, IModifier> modifierAndTraitIdentifierMaps;

    // contains all fluid that tinkers reborn registered
    public static List<TinkersRebornFluid> allTinkersFluid;

    private static Map<Class<? extends EntityLivingBase>, ItemStack> headDrops;

    public static LiquidCasting tableCasting;
    public static LiquidCasting basinCasting;

    public void preInit() {
        this.initCreativeTab();
        this.initRegistry();

        tableCasting = new LiquidCasting();
        basinCasting = new LiquidCasting();
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
        tools = new ArrayList<>();
        toolNameMap = new HashMap<>();
        toolStationCrafting = new ArrayList<>();
        toolForgeCrafting = new ArrayList<>();
        allMaterialsList = new ArrayList<>();
        materialIdentifierMaps = new HashMap<>();
        materialIdMaps = new HashMap<>();
        modifierAndTraitIdentifierMaps = new LinkedHashMap<>();
        allTinkersFluid = new ArrayList<>();
        headDrops = new HashMap<>();
        this.initRenderMaterial();
    }

    private void initRenderMaterial() {
        renderMaterials = new HashMap<>();
        renderMaterials.put("_internal_render1", new RenderMaterial("_internal_render1", 0XC1C1C1));
        renderMaterials.put("_internal_render2", new RenderMaterial("_internal_render2", 0X684E1E));
        renderMaterials.put("_internal_render3", new RenderMaterial("_internal_render3", 0X2376DD));
        renderMaterials.put("_internal_render4", new RenderMaterial("_internal_render4", 0X7146B0));
    }

    public static void addMaterialToMap(TinkersRebornMaterial m) {
        materialIdentifierMaps.put(m.identifier, m);
        materialIdMaps.put(m.materialId, m);
        allMaterialsList.add(m);
    }

    public static TinkersRebornMaterial getMaterialByIdentifier(String identifier) {
        return materialIdentifierMaps.get(identifier);
    }

    public static TinkersRebornMaterial getMaterialById(int id) {
        return materialIdMaps.get(id);
    }

    public static TinkersRebornFluid getFluidByIndex(int idx) {
        if (idx < allTinkersFluid.size()) {
            return allTinkersFluid.get(idx);
        } else {
            return null;
        }
    }

    /**
     * Call before adding a trait to a material. Checks consistency and takes care everything is in a consistent state.
     * Registers the trait if it's not registered, takes events into account.
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
}
