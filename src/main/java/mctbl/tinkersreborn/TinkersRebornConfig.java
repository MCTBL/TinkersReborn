package mctbl.tinkersreborn;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.Loader;

public class TinkersRebornConfig {

    public static String GENERAL = "General";
    public static String WORLD_GEN = "Worldgen";
    public static String TOOLS = "Tools";
    public static String ALLOWEDTOOLS = "Allowedtools";
    public static String SMELTERY = "Smeltery";
    public static String TOOLLEVELING = "ToolLeveling";

    public static boolean debug;

    public static String[] metalTypes;
    public static String[] oreTypes;
    public static String[] gravelOreTypes;

    public static boolean exportMaterialDefaultConfig;

    public static boolean exportHarvestLevelDefaultConfig;
    public static int[] vanillaHarvestLevelMapping;
    public static String[] oreDictPrefixes;
    public static boolean nerfVanillaTools;
    public static boolean nerfVanillaHoes;
    public static boolean nerfVanillaSwords;
    public static boolean nerfVanillaBows;
    // allowed tools that should not be nerfed
    public static boolean excludedToolsIsWhitelist;
    public static Set<String> excludedTools = new HashSet<String>();
    public static Set<String> excludedModTools = new HashSet<String>();

    public static boolean keepHunger;
    public static boolean disableAllRecipes;
    public static String[] miningLevels;
    public static String fluidUnit;

    public static int naturalSlimeSpawn;

    public static boolean generateCopper;
    public static boolean generateTin;
    public static boolean generateAluminum;
    public static boolean generateCobalt;
    public static boolean generateArdite;
    public static boolean generateIronSurface;
    public static boolean generateGoldSurface;
    public static boolean generateCopperSurface;
    public static boolean generateTinSurface;
    public static boolean generateAluminumSurface;

    public static int copperDensity;
    public static int tinDensity;
    public static int aluminumDensity;
    public static int cobaltDensity;
    public static int arditeDensity;
    public static int ironsRarity;
    public static int goldsRarity;
    public static int coppersRarity;
    public static int tinsRarity;
    public static int aluminumsRarity;

    public static int islandRarity;

    public static boolean addToVillages;
    public static int[] generatePatternNumber;
    public static int[] generateToolPartNumber;
    public static String[] generateToolPartMaterials;
    public static int[] generateToolPartMaterialsWeights;
    public static boolean generateVillageSmeltery;

    public static int defaultModifiers;
    public static boolean autoSmeltWithLapis;
    public static boolean diamondAndEmeraldCanIncreaseHarvestLevel;

    public static boolean celsiusPref;

    public static double oreToIngotRatio;
    public static int heatItemsTickrateSmeltery;

    public static String[] fluidIgnore;

    public static String[] entityMelting;
    public static int smelteryDrainEachTick;
    public static int vineHammerMaxOreMine;
    public static int vineHammerMineEachTick;

    public static Set<String> heartDropBlacklist;

    // leveling
    public static boolean toolLevelingEnable;
    public static int maxToolLevel;
    public static boolean pickaxeBoostRequired;
    public static boolean allowFakePlayerLeveling;
    public static int xpRequiredToolsPercentage;
    public static int xpRequiredWeaponsPercentage;
    public static float xpPerLevelMultiplier;
    public static float xpPerBoostLevelMultiplier;
    public static int levelingPickaxeBoostXpPercentage;
    public static int[] toolModifiersAtLevels;

    public static boolean detailedXpTooltip;

    public static boolean isAngelicaLoaded;

    public static void setupConfig(File location) {
        metalTypes = new String[] { "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze", "Tin", "Aluminum", "AluBrass",
            "Alumite", "Steel", "Ender" };

        oreTypes = new String[] { "nether_cobalt", "nether_ardite", "ore_copper", "ore_tin", "ore_aluminum" };

        gravelOreTypes = new String[] { "iron", "gold", "copper", "tin", "aluminum" };

        Configuration config = new Configuration(new File(location + "/Tinkersreborn/TinkersRebornGeneral.cfg"));

        config.load();

        debug = config.get(GENERAL, "debug", true, "debug mode")
            .getBoolean();

        keepHunger = config.get(GENERAL, "keepHunger", true, "Keep hunger on death")
            .getBoolean();

        disableAllRecipes = config
            .get(
                GENERAL,
                "Disable All Recipes",
                false,
                "Disable all TinkersReborn recipes (smeltery, drying rack, crafting, etc)")
            .getBoolean();
        miningLevels = config
            .get(
                GENERAL,
                "Mining Levels",
                new String[] { "§7tile.stone.name", "§fIron", "§4item.redstone.name", "#5C1BBDtile.obsidian.name",
                    "§9Cobalt", "§5Manyullyn" },
                "Mining levels")
            .getStringList();
        fluidUnit = config.get(GENERAL, "Fluid unit", "mB", "Only for display")
            .getString();

        heartDropBlacklist = Arrays
            .asList(
                config.get(GENERAL, "Heart Drop Black List", new String[] {}, "Which Boss won't drop yellow heart")
                    .getStringList())
            .stream()
            .map(s -> s.toLowerCase(Locale.US))
            .collect(Collectors.toSet());

        naturalSlimeSpawn = config.get(GENERAL, "Blue Slime spawn chance", 1, "Set to 0 to disable")
            .getInt();

        generateCopper = config.get(WORLD_GEN, "Generate Copper", true)
            .getBoolean();
        generateTin = config.get(WORLD_GEN, "Generate Tin", true)
            .getBoolean();
        generateAluminum = config.get(WORLD_GEN, "Generate Aluminum", true)
            .getBoolean();
        generateCobalt = config.get(WORLD_GEN, "Generate Cobalt", true)
            .getBoolean();
        generateArdite = config.get(WORLD_GEN, "Generate Ardite", true)
            .getBoolean();
        generateIronSurface = config.get(WORLD_GEN, "Generate Surface Iron", true)
            .getBoolean();
        generateGoldSurface = config.get(WORLD_GEN, "Generate Surface Gold", true)
            .getBoolean();
        generateCopperSurface = config.get(WORLD_GEN, "Generate Surface Copper", true)
            .getBoolean();
        generateTinSurface = config.get(WORLD_GEN, "Generate Surface Tin", true)
            .getBoolean();
        generateAluminumSurface = config.get(WORLD_GEN, "Generate Surface Aluminum", true)
            .getBoolean();

        copperDensity = config.get(WORLD_GEN, "Copper Underground Density", 2, "Density: Chances per chunk")
            .getInt();
        tinDensity = config.get(WORLD_GEN, "Tin Underground Density", 2)
            .getInt();
        aluminumDensity = config.get(WORLD_GEN, "Aluminum Underground Density", 3)
            .getInt();
        cobaltDensity = config.get(WORLD_GEN, "Cobalt Ore Density", 8)
            .getInt();
        arditeDensity = config.get(WORLD_GEN, "Ardite Ore Density", 8)
            .getInt();
        ironsRarity = config.get(WORLD_GEN, "Iron Surface Rarity", 400, "Rarity: 1/num to generate in chunk")
            .getInt();
        goldsRarity = config.get(WORLD_GEN, "Gold Surface Rarity", 900)
            .getInt();
        coppersRarity = config.get(WORLD_GEN, "Copper Surface Rarity", 100)
            .getInt();
        tinsRarity = config.get(WORLD_GEN, "Tin Surface Rarity", 100)
            .getInt();
        aluminumsRarity = config.get(WORLD_GEN, "Aluminum Surface Rarity", 50)
            .getInt();

        // Slime pools
        islandRarity = config.get(WORLD_GEN, "Slime Island Rarity", 1450)
            .getInt();

        addToVillages = config.get(WORLD_GEN, "Add Tinkers Reborn buildings to villages", true)
            .getBoolean();
        generatePatternNumber = config.get(WORLD_GEN, "Pattern generate number [lower, larger]", new int[] { 1, 5 })
            .getIntList();
        generateToolPartNumber = config.get(WORLD_GEN, "Tool part generate number [lower, larger]", new int[] { 4, 20 })
            .getIntList();
        generateToolPartMaterials = config
            .get(
                WORLD_GEN,
                "Allow tool part material generate in villages tool part chest",
                new String[] { "Wood", "Stone", "Flint", "Cactus", "Obsidian", "Bone", "String" })
            .getStringList();
        generateToolPartMaterialsWeights = config
            .get(WORLD_GEN, "Tool part material weights", new int[] { 10, 10, 10, 10, 10, 10, 10 })
            .getIntList();
        generateVillageSmeltery = config
            .get(
                WORLD_GEN,
                "Generate village smeltery",
                true,
                "Requires village structure generation to be enabled. The generated smeltery is decorative.")
            .getBoolean();

        defaultModifiers = config.get(TOOLS, "Default tool modifiers", 3)
            .getInt();

        autoSmeltWithLapis = config.get(TOOLS, "Can Autosmelt modify work with fortune", false)
            .getBoolean();
        diamondAndEmeraldCanIncreaseHarvestLevel = config
            .get(TOOLS, "Can diamnod and emerald increase mining level", true)
            .getBoolean();

        exportMaterialDefaultConfig = config
            .get(GENERAL, "Export Material Default Config", false, "export TinkersRebornMaterialDefault.cfg or not")
            .getBoolean();

        exportHarvestLevelDefaultConfig = config
            .get(
                GENERAL,
                "Export Harvest Level Default Config",
                false,
                "export TinkersRebornHarvestLevelDefault.cfg or not")
            .getBoolean();

        vanillaHarvestLevelMapping = config.get(
            GENERAL,
            "Vanilla Harvest Level Mapping",
            new int[] { 0, 1, 2, 3 },
            "Maps vanilla harvest levels (0=wood,1=stone,2=iron,3=diamond) to indices in the Mining Levels list, this mean 0 map to 0")
            .getIntList();

        oreDictPrefixes = config
            .get(
                GENERAL,
                "Override Ore Dict Prefixes",
                new String[] { "ore", "denseore", "oreNether", "denseoreNether", "block", "stone", "brick", "orePoor" })
            .getStringList();

        nerfVanillaTools = config.get(GENERAL, "disableRegularTools", true, "Makes all non-Tinkers tools mine nothing")
            .getBoolean();
        nerfVanillaHoes = config
            .get(
                GENERAL,
                "disableRegularHoes",
                false,
                "Makes all non-Tinkers hoes to not be able to hoe ground. Use the Mattock.")
            .getBoolean();
        nerfVanillaSwords = config
            .get(
                GENERAL,
                "disableRegularSwords",
                false,
                "Makes all non-Tinkers swords useless. Like whacking enemies with a stick.")
            .getBoolean();
        nerfVanillaBows = config
            .get(
                GENERAL,
                "disableRegularBows",
                false,
                "Makes all non-Tinkers bows useless. You suddenly forgot how to use a bow.")
            .getBoolean();

        String type = config.get(
            ALLOWEDTOOLS,
            "exclusionType",
            "blacklist",
            "Change the type of the exclusion.\n'blacklist' means the listed tools are made unusable.\n'whitelist' means ALL tools except the listed ones are unusable.",
            new String[] { "whitelist", "blacklist" })
            .getString();
        excludedToolsIsWhitelist = "whitelist".equals(type);
        String[] tools = config.get(
            ALLOWEDTOOLS,
            TOOLS,
            new String[] {
                // botania
                "Botania:manasteelAxe", "Botania:manasteelPick", "Botania:manasteelShovel",
                // Flaxbeards Steam Power
                "Steamcraft:axeGildedGold", "Steamcraft:pickGildedGold", "Steamcraft:shovelGildedGold",
                "Steamcraft:axeBrass", "Steamcraft:pickBrass", "Steamcraft:shovelBrass",
                // IC2
                "IC2:itemToolBronzeAxe", "IC2:itemToolBronzePickaxe", "IC2:itemToolBronzeSpade",
                // Railcraft
                "Railcraft:tool.steel.axe", "Railcraft:tool.steel.pickaxe", "Railcraft:tool.steel.shovel" },
            "Tools that are excluded if the option to nerf non-tinkers tools is enabled.")
            .getStringList();
        String[] swords = config.get(
            ALLOWEDTOOLS,
            "swords",
            new String[] { "Botania:manasteelSword", "Steamcraft:swordGildedGold", "Steamcraft:swordBrass",
                "ThermalExpansion:tool.battleWrenchInvar", "IC2:itemToolBronzeSword", "Railcraft:tool.steel.sword" },
            "Swords that are excluded if the option to nerf non-tinkers swords is enabled.")
            .getStringList();
        String[] bows = config
            .get(
                ALLOWEDTOOLS,
                "bows",
                new String[] {},
                "Bows that are excluded if the option to nerf non-tinkers bows is enabled.")
            .getStringList();
        String[] hoes = config
            .get(
                ALLOWEDTOOLS,
                "hoes",
                new String[] { "Steamcraft:hoeGildedGold", "Steamcraft:hoeBrass", "IC2:itemToolBronzeHoe",
                    "Railcraft:tool.steel.hoe" },
                "Hoes that are excluded if the option to nerf non-tinkers hoes is enabled.")
            .getStringList();

        excludedModTools.addAll(
            Arrays.asList(
                config
                    .get(
                        ALLOWEDTOOLS,
                        "mods",
                        new String[] { "minecraft", "Metallurgy", "Natura", "BiomesOPlenty", "ProjRed|Exploration",
                            "appliedenergistics2", "MekanismTool", "ThermalFoundation" },
                        "Here you can exclude entire mods by adding their mod-id (the first part of the string).")
                    .getStringList()));

        if (nerfVanillaTools) excludedTools.addAll(Arrays.asList(tools));
        if (nerfVanillaSwords) excludedTools.addAll(Arrays.asList(swords));
        if (nerfVanillaBows) excludedTools.addAll(Arrays.asList(bows));
        if (nerfVanillaHoes) excludedTools.addAll(Arrays.asList(hoes));

        celsiusPref = config.get(GENERAL, "Temperature Unit Pref", true, "true is Celsius and false is kelvin")
            .getBoolean();

        oreToIngotRatio = config.get(
            GENERAL,
            "oreToIngotRatio",
            2.0F,
            "Determines the ratio of ore to ingot, or in other words how many ingots you get out of an ore. This ratio applies to all ores (including poor and dense). The ratio can be any decimal, including 1.5 and the like, but can't go below 1. THIS ALSO AFFECTS MELTING TEMPERATURE!")
            .setMinValue(1)
            .getDouble();

        fluidIgnore = config
            .get(
                GENERAL,
                "fluidIgnore",
                new String[] {},
                "List of fluids to ignore, effectively preventing registration of melting and casting recipes.")
            .getStringList();

        heatItemsTickrateSmeltery = config.get(
            SMELTERY,
            "heatItemsTickrateSmeltery",
            4,
            "The tickrate at which items are heated and alloys are created in the smeltery. Defaults to every 4th tick.")
            .getInt();

        entityMelting = config
            .get(
                SMELTERY,
                "entityMelting",
                new String[] { "SnowMan;true;water;100", "Villager;true;molten_emerald;6",
                    "VillagerGolem;true;molten_iron;18", "PigZombie;true;molten_gold;10", "Enderman;true;ender;5", },
                "List of entity melting entries in the format 'entity;subtypes;fluid;amount'.")
            .getStringList();

        smelteryDrainEachTick = config.get(SMELTERY, "smelteryDrainEachTick", 6)
            .getInt();

        vineHammerMaxOreMine = config.get(TOOLS, "Vein Hammer can mine ores each time", 25)
            .getInt();
        // does this really need?
        vineHammerMineEachTick = config.get(TOOLS, "Vein Hammer each tick can mine howmany ore block", 25)
            .getInt();

        toolLevelingEnable = config
            .get(
                TOOLLEVELING,
                "Tool level system enable",
                true,
                "Can your skill with tools 'level up' as you use them?")
            .getBoolean();
        maxToolLevel = config.get(TOOLLEVELING, "maxToolLevel", 6, "", 1, 99)
            .getInt();
        pickaxeBoostRequired = config.get(
            TOOLLEVELING,
            "pickaxeBoostRequired",
            false,
            "Every Pickaxes Mining Level is reduced by 1 and needs a mining levelup (separate from tool level) or, if enabled, a mob head modifier to advance")
            .getBoolean();
        allowFakePlayerLeveling = config
            .get(TOOLLEVELING, "allowFakePlayerLeveling", true, "Allow tool leveling through fake players")
            .getBoolean();
        xpRequiredToolsPercentage = config
            .get(
                TOOLLEVELING,
                "xpRequiredToolsPercentage",
                100,
                "Change the XP required to level up tools in % (higher = more xp needed)",
                1,
                999)
            .getInt();
        xpRequiredWeaponsPercentage = config
            .get(
                TOOLLEVELING,
                "xpRequiredWeaponsPercentage",
                100,
                "Change the XP required to level up weapons in % (higher = more xp needed)",
                1,
                999)
            .getInt();
        levelingPickaxeBoostXpPercentage = config
            .get(
                TOOLLEVELING,
                "xpRequiredPickBoostPercentage",
                100,
                "Change the percentage of XP required to boost a pick (i.e. 200 means 2x normal boost xp required)",
                1,
                999)
            .getInt();
        detailedXpTooltip = config
            .get(TOOLLEVELING, "detailedXpTooltip", true, "XP tooltip shows numbers, in addition to percentage")
            .getBoolean();
        toolModifiersAtLevels = config
            .get(
                TOOLLEVELING,
                "toolModifiersAtLevels",
                new int[] { 2, 4, 6 },
                "Adds an extra modifier on these levelups if 'ExtraModifiers' is enabled")
            .getIntList();

        // y can't get float??
        xpPerLevelMultiplier = config.getFloat(
            "xpPerLevelMultiplier",
            TOOLLEVELING,
            1.15F,
            1.0F,
            9.99F,
            "Change the XP required to level up weapons in % (higher = more xp needed)");
        xpPerBoostLevelMultiplier = config.getFloat(
            "xpPerBoostLevelMultiplier",
            TOOLLEVELING,
            1.12f,
            1.0f,
            9.99f,
            "Exponential multiplier for required boost xp per level");

        if (Loader.isModLoaded("angelica")) {
            isAngelicaLoaded = true;
        }

        config.save();
    }

}
