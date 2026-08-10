package mctbl.tinkersreborn.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.client.StepSoundSlime;
import mctbl.tinkersreborn.common.blocks.ConsecratedSoil;
import mctbl.tinkersreborn.common.blocks.DryingRackBlock;
import mctbl.tinkersreborn.common.blocks.GravelOre;
import mctbl.tinkersreborn.common.blocks.GraveyardSoil;
import mctbl.tinkersreborn.common.blocks.Grout;
import mctbl.tinkersreborn.common.blocks.MetalOre;
import mctbl.tinkersreborn.common.blocks.SlimeSand;
import mctbl.tinkersreborn.common.blocks.StoneTorch;
import mctbl.tinkersreborn.common.blocks.TinkersRebornMetalBlock;
import mctbl.tinkersreborn.common.blocks.slime.SlimeDirt;
import mctbl.tinkersreborn.common.blocks.slime.SlimeFluid;
import mctbl.tinkersreborn.common.blocks.slime.SlimeGel;
import mctbl.tinkersreborn.common.blocks.slime.SlimeGrass;
import mctbl.tinkersreborn.common.blocks.slime.SlimeLeaves;
import mctbl.tinkersreborn.common.blocks.slime.SlimeSapling;
import mctbl.tinkersreborn.common.blocks.slime.SlimeTallGrass;
import mctbl.tinkersreborn.common.entity.BlueSlime;
import mctbl.tinkersreborn.common.entity.DryingRackLogic;
import mctbl.tinkersreborn.common.entity.KingBlueSlime;
import mctbl.tinkersreborn.common.events.HealthBarRenderer;
import mctbl.tinkersreborn.common.events.TinkersRebornPlayerHandler;
import mctbl.tinkersreborn.common.itemblocks.GravelOreItem;
import mctbl.tinkersreborn.common.itemblocks.MetalOreItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeGelItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeGrassItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeLeavesItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeSaplingItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeTallGrassItem;
import mctbl.tinkersreborn.common.itemblocks.TinkersRebornMetalItemBlock;
import mctbl.tinkersreborn.common.items.GoldenHead;
import mctbl.tinkersreborn.common.items.HeartCanister;
import mctbl.tinkersreborn.common.items.Jerky;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.smeltery.items.FilledBucket;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.entity.FancyEntityItem;
import mctbl.tinkersreborn.tools.items.Pattern;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.util.RecipeRemover;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.world.gen.SlimeIslandGen;
import mctbl.tinkersreborn.world.gen.TinkersRebornSurfaceOreGen;
import mctbl.tinkersreborn.world.gen.TinkersRebornWorldGenerator;
import mctbl.tinkersreborn.world.village.ComponentSmeltery;
import mctbl.tinkersreborn.world.village.ComponentToolWorkshop;
import mctbl.tinkersreborn.world.village.VillageSmelteryHandler;
import mctbl.tinkersreborn.world.village.VillageToolWorkshopHandler;

public class TinkersRebornGeneral implements ITinkersRebornModule {

    @SidedProxy(
        clientSide = "mctbl.tinkersreborn.common.TinkersRebornGeneralProxyClient",
        serverSide = "mctbl.tinkersreborn.common.TinkersRebornGeneralProxyCommon")
    public static TinkersRebornGeneralProxyCommon proxy;

    public static Item tinkersBucket;
    public static Block stoneTorch;
    public static Item goldHead;
    public static Item jerky;
    public static Block metalBlock;
    public static Block slimeSand;
    public static Block grout;
    public static Block graveyardSoil;
    public static Block consecratedSoil;
    public static Block slimeDirt;

    // Slime
    public static SoundType slimeStep;
    public static TinkersRebornFluid blueSlimeFluid;
    public static Block slimePool;
    public static Block slimeGel;
    public static Block slimeGrass;
    public static Block slimeTallGrass;
    public static SlimeLeaves slimeLeaves;
    public static SlimeSapling slimeSapling;

    public static TinkersRebornFluid bloodFluid;
    public static TinkersRebornFluid enderFluid;

    // Ores
    public static Block oreSlag;
    public static Block oreGravel;
    public static Block dryingRack;

    // Chest hooks
    public static ChestGenHooks tinkerHouseChest;
    public static ChestGenHooks tinkerHousePatterns;
    public static ChestGenHooks tinkerHouseParts;

    public static Item heartCanister;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        stoneTorch = new StoneTorch();
        GameRegistry.registerBlock(stoneTorch, stoneTorch.getUnlocalizedName());

        tinkersBucket = new FilledBucket(Block.getBlockFromItem(tinkersBucket));
        GameRegistry.registerItem(tinkersBucket, tinkersBucket.getUnlocalizedName());

        goldHead = new GoldenHead(4, 1.2F, false);
        GameRegistry.registerItem(goldHead, goldHead.getUnlocalizedName());

        jerky = new Jerky(Loader.isModLoaded("HungerOverhaul") || Loader.isModLoaded("fc_food"));
        GameRegistry.registerItem(jerky, jerky.getUnlocalizedName());

        metalBlock = new TinkersRebornMetalBlock(Material.iron, 10.0F);
        GameRegistry.registerBlock(metalBlock, TinkersRebornMetalItemBlock.class, metalBlock.getUnlocalizedName());

        slimeSand = new SlimeSand();
        GameRegistry.registerBlock(slimeSand, slimeSand.getUnlocalizedName());

        grout = new Grout();
        GameRegistry.registerBlock(grout, grout.getUnlocalizedName());

        graveyardSoil = new GraveyardSoil();
        GameRegistry.registerBlock(graveyardSoil, graveyardSoil.getUnlocalizedName());

        consecratedSoil = new ConsecratedSoil();
        GameRegistry.registerBlock(consecratedSoil, consecratedSoil.getUnlocalizedName());

        slimeDirt = new SlimeDirt();
        GameRegistry.registerBlock(slimeDirt, slimeDirt.getUnlocalizedName());

        slimeStep = new StepSoundSlime("mob.slime", 1.0f, 1.0f);

        blueSlimeFluid = new TinkersRebornFluid("blue_slime", 0X42E9F4, "slime_blue");
        FluidRegistry.registerFluid(blueSlimeFluid);

        slimePool = new SlimeFluid(blueSlimeFluid);
        GameRegistry.registerBlock(slimePool, slimePool.getUnlocalizedName());

        // Slime Islands
        slimeGel = new SlimeGel();
        GameRegistry.registerBlock(slimeGel, SlimeGelItemBlock.class, slimeGel.getUnlocalizedName());
        slimeGrass = new SlimeGrass();
        GameRegistry.registerBlock(slimeGrass, SlimeGrassItemBlock.class, slimeGrass.getUnlocalizedName());
        slimeTallGrass = new SlimeTallGrass();
        GameRegistry.registerBlock(slimeTallGrass, SlimeTallGrassItem.class, slimeTallGrass.getUnlocalizedName());
        slimeLeaves = new SlimeLeaves();
        GameRegistry.registerBlock(slimeLeaves, SlimeLeavesItemBlock.class, slimeLeaves.getUnlocalizedName());
        slimeSapling = new SlimeSapling();
        GameRegistry.registerBlock(slimeSapling, SlimeSaplingItemBlock.class, slimeSapling.getUnlocalizedName());

        oreSlag = new MetalOre();
        GameRegistry.registerBlock(oreSlag, MetalOreItemBlock.class, oreSlag.getUnlocalizedName());
        oreGravel = new GravelOre();
        GameRegistry.registerBlock(oreGravel, GravelOreItem.class, oreGravel.getUnlocalizedName());

        bloodFluid = new TinkersRebornFluid("blood", 0xFF0000, "blood");
        enderFluid = new TinkersRebornFluid("ender", 0x0B4D42, "ender");

        dryingRack = new DryingRackBlock();
        GameRegistry.registerBlock(dryingRack, dryingRack.getUnlocalizedName());
        GameRegistry.registerTileEntity(DryingRackLogic.class, dryingRack.getUnlocalizedName());

        heartCanister = new HeartCanister();
        GameRegistry.registerItem(heartCanister, heartCanister.getUnlocalizedName());

        // Vanilla stack sizes
        Items.wooden_door.setMaxStackSize(16);
        Items.iron_door.setMaxStackSize(16);
        Items.boat.setMaxStackSize(16);
        Items.minecart.setMaxStackSize(3);
        Items.cake.setMaxStackSize(16);

        oreRegistry();

        MinecraftForge.EVENT_BUS.register(new TinkersRebornPlayerHandler());
        HealthBarRenderer healthBarRenderer = new HealthBarRenderer();
        MinecraftForge.EVENT_BUS.register(healthBarRenderer);
        FMLCommonHandler.instance()
            .bus()
            .register(healthBarRenderer);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        if (!TinkersRebornConfig.disableAllRecipes) {
            craftingTableRecipes();
            // addRecipesForFurnace();
        }
        this.createEntities();
        this.registerDrying();
        this.registerVillageStructures();
        proxy.init();

        GameRegistry.registerWorldGenerator(new TinkersRebornWorldGenerator(), 0);
        MinecraftForge.TERRAIN_GEN_BUS.register(new TinkersRebornSurfaceOreGen());
        GameRegistry.registerWorldGenerator(new SlimeIslandGen(slimePool, 2), 2);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        addLoot();
    }

    private void oreRegistry() {
        ItemStack craftingTable = new ItemStack(Blocks.crafting_table, 1);

        OreDictionary.registerOre("oreCobalt", new ItemStack(oreSlag, 1, 0));
        OreDictionary.registerOre("oreArdite", new ItemStack(oreSlag, 1, 1));
        OreDictionary.registerOre("oreCopper", new ItemStack(oreSlag, 1, 2));
        OreDictionary.registerOre("oreTin", new ItemStack(oreSlag, 1, 3));
        OreDictionary.registerOre("oreAluminum", new ItemStack(oreSlag, 1, 4));
        OreDictionary.registerOre("oreAluminium", new ItemStack(oreSlag, 1, 4));

        OreDictionary.registerOre("oreIron", new ItemStack(oreGravel, 1, 0));
        OreDictionary.registerOre("oreGold", new ItemStack(oreGravel, 1, 1));
        OreDictionary.registerOre("oreCobalt", new ItemStack(oreGravel, 1, 5));
        OreDictionary.registerOre("oreCopper", new ItemStack(oreGravel, 1, 2));
        OreDictionary.registerOre("oreTin", new ItemStack(oreGravel, 1, 3));
        OreDictionary.registerOre("oreAluminum", new ItemStack(oreGravel, 1, 4));
        OreDictionary.registerOre("oreAluminium", new ItemStack(oreGravel, 1, 4));

        OreDictionary.registerOre("blockCobalt", new ItemStack(metalBlock, 1, 0));
        OreDictionary.registerOre("blockArdite", new ItemStack(metalBlock, 1, 1));
        OreDictionary.registerOre("blockManyullyn", new ItemStack(metalBlock, 1, 2));
        OreDictionary.registerOre("blockCopper", new ItemStack(metalBlock, 1, 3));
        OreDictionary.registerOre("blockBronze", new ItemStack(metalBlock, 1, 4));
        OreDictionary.registerOre("blockTin", new ItemStack(metalBlock, 1, 5));
        OreDictionary.registerOre("blockAluminum", new ItemStack(metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminium", new ItemStack(metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminumBrass", new ItemStack(metalBlock, 1, 7));
        OreDictionary.registerOre("blockAluminiumBrass", new ItemStack(metalBlock, 1, 7));
        OreDictionary.registerOre("blockAlumite", new ItemStack(metalBlock, 1, 8));
        OreDictionary.registerOre("blockSteel", new ItemStack(metalBlock, 1, 9));
        OreDictionary.registerOre("blockEnder", new ItemStack(metalBlock, 1, 10));

        OreDictionary.registerOre("crafterWood", craftingTable);
        OreDictionary.registerOre("craftingTableWood", craftingTable);

        OreDictionary.registerOre("torchStone", new ItemStack(stoneTorch));

        // Vanilla stuff
        OreDictionary.registerOre("slimeball", new ItemStack(Items.slime_ball));
        OreDictionary.registerOre("blockGlass", new ItemStack(Blocks.glass));
        RecipeRemover.removeShapedRecipe(new ItemStack(Blocks.sticky_piston));
        RecipeRemover.removeShapedRecipe(new ItemStack(Items.magma_cream));
        RecipeRemover.removeShapedRecipe(new ItemStack(Items.lead));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.sticky_piston), "slimeball", Blocks.piston));
        GameRegistry
            .addRecipe(new ShapelessOreRecipe(new ItemStack(Items.magma_cream), "slimeball", Items.blaze_powder));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(Items.lead, 2),
                "ss ",
                "sS ",
                "  s",
                's',
                Items.string,
                'S',
                "slimeball"));
    }

    private static void addLoot() {
        tinkerHouseChest = ChestGenHooks.getInfo("TinkersRebornHouse");
        tinkerHouseChest.setMin(3);
        tinkerHouseChest.setMax(8);
        tinkerHouseChest.addItem(new WeightedRandomChestContent(new ItemStack(heartCanister, 1, 1), 1, 1, 2));
        tinkerHouseChest
            .addItem(new WeightedRandomChestContent(new ItemStack(TinkersRebornTools.searedBrick), 2, 8, 12));
        tinkerHouseChest
            .addItem(new WeightedRandomChestContent(Pattern.newStackWithIdentifier(Pattern.PATTERN_BLANK), 1, 3, 10));
        tinkerHouseChest.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_ingot), 1, 3, 5));
        tinkerHouseChest.addItem(new WeightedRandomChestContent(new ItemStack(Items.gold_ingot), 1, 2, 2));

        tinkerHousePatterns = ChestGenHooks.getInfo("TinkersRebornPatterns");
        tinkerHousePatterns.setMin(TinkersRebornConfig.generatePatternNumber[0]);
        tinkerHousePatterns.setMax(TinkersRebornConfig.generatePatternNumber[1]);
        TinkersRebornTools.patternAndCast.getAllPatternType()
            .forEach(
                part -> tinkerHousePatterns.addItem(
                    new WeightedRandomChestContent(
                        Pattern.newStackWithIdentifier(part),
                        1,
                        part.equals(Pattern.PATTERN_BLANK) ? 5 : 1,
                        part.equals(Pattern.PATTERN_BLANK) ? 60 : 12)));

        tinkerHouseParts = ChestGenHooks.getInfo("TinkersRebornParts");
        tinkerHouseParts.setMin(TinkersRebornConfig.generateToolPartNumber[0]);
        tinkerHouseParts.setMax(TinkersRebornConfig.generateToolPartNumber[1]);

        List<TinkersRebornMaterial> allowMaterialList = Arrays.asList(TinkersRebornConfig.generateToolPartMaterials)
            .stream()
            .map(TinkersRebornUtils::sanitizeLocalizationString)
            .map(TinkersRebornRegistry::getMaterialByIdentifier)
            .collect(Collectors.toList());
        for (TinkersRebornToolPart part : TinkersRebornRegistry.getAllToolParts()) {
            for (int idx = 0; idx < allowMaterialList.size(); idx++) {
                TinkersRebornMaterial material = allowMaterialList.get(idx);
                if (material.getStats(part.allowType) != null) {
                    ItemStack stack = part.getNewPartWithMaterial(material);
                    if (stack != null) {
                        int weight = TinkersRebornConfig.generateToolPartMaterialsWeights[idx];
                        tinkerHouseParts.addItem(new WeightedRandomChestContent(stack, 1, 1, weight));
                    }
                }

            }
        }
    }

    private void registerVillageStructures() {
        if (!TinkersRebornConfig.addToVillages) return;

        VillagerRegistry.instance()
            .registerVillageCreationHandler(new VillageToolWorkshopHandler());
        MapGenStructureIO.func_143031_a(ComponentToolWorkshop.class, "TinkersReborn:ToolWorkshop");

        if (TinkersRebornConfig.generateVillageSmeltery) {
            VillagerRegistry.instance()
                .registerVillageCreationHandler(new VillageSmelteryHandler());
            MapGenStructureIO.func_143031_a(ComponentSmeltery.class, "TinkersReborn:Smeltery");
        }
    }

    private void createEntities() {
        EntityRegistry
            .registerModEntity(FancyEntityItem.class, "Tinkers Fancy Item", 0, TinkersReborn.instance, 32, 5, true);
        EntityRegistry.registerModEntity(BlueSlime.class, "Tinkers Blue Slime", 1, TinkersReborn.instance, 64, 5, true);
        EntityRegistry
            .registerModEntity(KingBlueSlime.class, "Tinkers King Slime", 2, TinkersReborn.instance, 64, 5, true);

        if (TinkersRebornConfig.naturalSlimeSpawn > 1) {
            Type[] biomeTypes = { Type.FOREST, Type.PLAINS, Type.MOUNTAIN, Type.HILLS, Type.SWAMP, Type.JUNGLE,
                Type.WASTELAND };
            Set<BiomeGenBase> set = new HashSet<>();
            for (Type t : biomeTypes) {
                set.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(t)));
            }
            EntityRegistry.addSpawn(
                BlueSlime.class,
                TinkersRebornConfig.naturalSlimeSpawn,
                4,
                20,
                EnumCreatureType.monster,
                set.toArray(new BiomeGenBase[0]));
        }
    }

    private void registerDrying() {
        // Jerky
        int time = 20 * 60 * 5;
        TinkersRebornRegistry.registerDryingRecipe(Items.beef, new ItemStack(jerky, 1, 0), time);
        TinkersRebornRegistry.registerDryingRecipe(Items.chicken, new ItemStack(jerky, 1, 1), time);
        TinkersRebornRegistry.registerDryingRecipe(Items.porkchop, new ItemStack(jerky, 1, 2), time);
        // TinkersRebornRegistry.registerDryingRecipe(Items.mutton, new ItemStack(jerky, 1, 3), time);
        TinkersRebornRegistry.registerDryingRecipe(Items.fish, new ItemStack(jerky, 1, 4), time);
        TinkersRebornRegistry.registerDryingRecipe(Items.rotten_flesh, new ItemStack(jerky, 1, 5), time);

        // Sapling to dead bush
        TinkersRebornRegistry.registerDryingRecipe("treeSapling", new ItemStack(Blocks.deadbush), 20 * 60 * 6);
    }

    private void craftingTableRecipes() {
        ItemStack sandBlock = new ItemStack(Blocks.sand);
        ItemStack dirtBlock = new ItemStack(Blocks.dirt);
        ItemStack gravelBlock = new ItemStack(Blocks.gravel);
        ItemStack clayBlock = new ItemStack(Blocks.clay);
        ItemStack searedBrick = new ItemStack(TinkersRebornTools.searedBrick, 1);
        ItemStack boneMeal = new ItemStack(Items.dye, 1, 15);
        ItemStack flesh = new ItemStack(Items.rotten_flesh);
        ItemStack stoneRod = TinkersRebornTools.rod.getNewPartWithMaterial("stone");
        ItemStack graveyardSoilBlock = new ItemStack(graveyardSoil);
        ItemStack consecratedSoilBlock = new ItemStack(consecratedSoil);

        // Jack o'Latern Recipe - Stone Torch
        GameRegistry.addRecipe(
            new ItemStack(Blocks.lit_pumpkin, 1, 0),
            "p",
            "s",
            'p',
            new ItemStack(Blocks.pumpkin),
            's',
            new ItemStack(stoneTorch));
        // Stone Torch Recipe
        GameRegistry
            .addRecipe(new ItemStack(stoneTorch, 4), "p", "w", 'p', new ItemStack(Items.coal, 1), 'w', stoneRod);

        GameRegistry
            .addRecipe(new ItemStack(grout, 8), "ABA", "BCB", "ABA", 'A', sandBlock, 'B', gravelBlock, 'C', clayBlock);
        GameRegistry
            .addShapelessRecipe(new ItemStack(grout, 2), sandBlock, gravelBlock, new ItemStack(Items.clay_ball));
        GameRegistry.addSmelting(new ItemStack(grout, 1), searedBrick, 0);

        GameRegistry.addShapelessRecipe(graveyardSoilBlock, dirtBlock, flesh, boneMeal);
        GameRegistry.addSmelting(graveyardSoilBlock, consecratedSoilBlock, 0);
        GameRegistry.addRecipe(new ShapedOreRecipe(dryingRack, "WWW", 'W', "slabWood"));
    }
}
