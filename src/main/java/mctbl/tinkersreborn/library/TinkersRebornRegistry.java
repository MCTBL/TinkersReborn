package mctbl.tinkersreborn.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.crafting.LiquidCasting;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ToolCore;

public class TinkersRebornRegistry {

    public static TinkersRebornRegistry instance = new TinkersRebornRegistry();

    private TinkersRebornRegistry() {}

    public static TinkersRebornCreativeTab blockTab;
    public static TinkersRebornCreativeTab toolsTab;
    public static TinkersRebornCreativeTab weaponsTab;
    public static TinkersRebornCreativeTab partsTab;
    public static TinkersRebornCreativeTab miscTab;

    public static List<ToolCore> tools;

    public static List<TinkersRebornMaterial> allMaterialsList;
    public static Map<String, TinkersRebornMaterial> materialIdentifierMaps;
    public static Map<Integer, TinkersRebornMaterial> materialIdMaps;

    public static LiquidCasting tableCasting;
    public static LiquidCasting basinCasting;

    public void preInit() {
        this.initCreativeTab();
        this.init();

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

    private void init() {
        tools = new ArrayList<>();
        allMaterialsList = new ArrayList<>();
        materialIdentifierMaps = new HashMap<>();
        materialIdMaps = new HashMap<>();
    }

    public static void addMaterialToMap(TinkersRebornMaterial m) {
        materialIdentifierMaps.put(m.identifier, m);
        materialIdMaps.put(m.materialId, m);
        allMaterialsList.add(m);
    }

    public static void addMaterialToMap(List<TinkersRebornMaterial> l) {
        for (TinkersRebornMaterial m : l) addMaterialToMap(m);
    }

    public static void addMaterialToMap(TinkersRebornMaterial... l) {
        addMaterialToMap(Arrays.asList(l));
    }

    public static TinkersRebornMaterial getMaterialByIdentifier(String identifier) {
        return materialIdentifierMaps.get(identifier);
    }

    public static TinkersRebornMaterial getMaterialById(int id) {
        return materialIdMaps.get(id);
    }
}
