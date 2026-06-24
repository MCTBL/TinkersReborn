package mctbl.tinkersreborn;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class TinkersRebornConfig {

    public static String[] metalTypes;
    public static String[] oreTypes;
    public static String[] gravelOreTypes;

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

    public static int defaultModifiers;

    public static int potionIdBias;

    public static void setupConfig(File location) {
        metalTypes = new String[] { "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze", "Tin", "Aluminum", "AluBrass",
            "Alumite", "Steel", "Ender" };

        oreTypes = new String[] { "nether_cobalt", "nether_ardite", "ore_copper", "ore_tin", "ore_aluminum" };

        gravelOreTypes = new String[] { "iron", "gold", "copper", "tin", "aluminum" };

        Configuration config = new Configuration(new File(location + "/TinkersReborn"));

        disableAllRecipes = config
            .get(
                "General",
                "Disable All Recipes",
                false,
                "Disable all TinkersReborn recipes (smeltery, drying rack, crafting, etc)")
            .getBoolean();
        miningLevels = config
            .get(
                "General",
                "Mining Levels",
                new String[] { "§7tile.stone.name", "§fIron", "§4item.redstone.name", "#5C1BBDtile.obsidian.name",
                    "§9Cobalt", "§5Manyullyn" },
                "Mining levels")
            .getStringList();
        fluidUnit = config.get("General", "Fluid unit", "mB", "Only for display")
            .getString();

        naturalSlimeSpawn = config.get("Mobs", "Blue Slime spawn chance", 1, "Set to 0 to disable")
            .getInt();

        generateCopper = config.get("Worldgen", "Generate Copper", true)
            .getBoolean();
        generateTin = config.get("Worldgen", "Generate Tin", true)
            .getBoolean();
        generateAluminum = config.get("Worldgen", "Generate Aluminum", true)
            .getBoolean();
        generateCobalt = config.get("Worldgen", "Generate Cobalt", true)
            .getBoolean();
        generateArdite = config.get("Worldgen", "Generate Ardite", true)
            .getBoolean();
        generateIronSurface = config.get("Worldgen", "Generate Surface Iron", true)
            .getBoolean();
        generateGoldSurface = config.get("Worldgen", "Generate Surface Gold", true)
            .getBoolean();
        generateCopperSurface = config.get("Worldgen", "Generate Surface Copper", true)
            .getBoolean();
        generateTinSurface = config.get("Worldgen", "Generate Surface Tin", true)
            .getBoolean();
        generateAluminumSurface = config.get("Worldgen", "Generate Surface Aluminum", true)
            .getBoolean();

        copperDensity = config.get("Worldgen", "Copper Underground Density", 2, "Density: Chances per chunk")
            .getInt();
        tinDensity = config.get("Worldgen", "Tin Underground Density", 2)
            .getInt();
        aluminumDensity = config.get("Worldgen", "Aluminum Underground Density", 3)
            .getInt();
        cobaltDensity = config.get("worldgen", "Cobalt Ore Density", 8)
            .getInt();
        arditeDensity = config.get("worldgen", "Ardite Ore Density", 8)
            .getInt();
        ironsRarity = config.get("Worldgen", "Iron Surface Rarity", 400, "Rarity: 1/num to generate in chunk")
            .getInt();
        goldsRarity = config.get("Worldgen", "Gold Surface Rarity", 900)
            .getInt();
        coppersRarity = config.get("Worldgen", "Copper Surface Rarity", 100)
            .getInt();
        tinsRarity = config.get("Worldgen", "Tin Surface Rarity", 100)
            .getInt();
        aluminumsRarity = config.get("Worldgen", "Aluminum Surface Rarity", 50)
            .getInt();

        // Slime pools
        islandRarity = config.get("Worldgen", "Slime Island Rarity", 1450)
            .getInt();

        defaultModifiers = config.get("Tools", "Default tool modifiers", 3)
            .getInt();

        potionIdBias = config.get("General", "Potion effect start id", 500)
            .getInt();
    }

}
