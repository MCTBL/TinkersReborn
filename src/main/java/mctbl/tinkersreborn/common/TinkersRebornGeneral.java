package mctbl.tinkersreborn.common;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.client.StepSoundSlime;
import mctbl.tinkersreborn.common.blocks.GravelOre;
import mctbl.tinkersreborn.common.blocks.MetalOre;
import mctbl.tinkersreborn.common.blocks.StoneTorch;
import mctbl.tinkersreborn.common.blocks.TinkersRebornMetalBlock;
import mctbl.tinkersreborn.common.blocks.slime.SlimeFluid;
import mctbl.tinkersreborn.common.blocks.slime.SlimeGel;
import mctbl.tinkersreborn.common.blocks.slime.SlimeGrass;
import mctbl.tinkersreborn.common.blocks.slime.SlimeLeaves;
import mctbl.tinkersreborn.common.blocks.slime.SlimeSapling;
import mctbl.tinkersreborn.common.blocks.slime.SlimeTallGrass;
import mctbl.tinkersreborn.common.itemblocks.GravelOreItem;
import mctbl.tinkersreborn.common.itemblocks.MetalOreItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeGelItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeGrassItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeLeavesItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeSaplingItemBlock;
import mctbl.tinkersreborn.common.itemblocks.SlimeTallGrassItem;
import mctbl.tinkersreborn.common.itemblocks.TinkersRebornMetalItemBlock;
import mctbl.tinkersreborn.common.items.GoldenHead;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.smeltery.items.FilledBucket;
import mctbl.tinkersreborn.util.RecipeRemover;

public class TinkersRebornGeneral implements ITinkersRebornModule {

    @SidedProxy(
        clientSide = "mctbl.tinkersreborn.common.TinkersRebornGeneralProxyClient",
        serverSide = "mctbl.tinkersreborn.common.TinkersRebornGeneralProxyCommon")
    public static TinkersRebornGeneralProxyCommon proxy;

    public static Item tinkersBucket;
    public static Block stoneTorch;
    public static Item goldHead;
    public static Block metalBlock;

    // Slime
    public static SoundType slimeStep;
    public static Fluid blueSlimeFluid;
    public static Block slimePool;
    public static Block slimeGel;
    public static Block slimeGrass;
    public static Block slimeTallGrass;
    public static SlimeLeaves slimeLeaves;
    public static SlimeSapling slimeSapling;

    // Ores
    public static Block oreSlag;
    public static Block oreGravel;

    // Chest hooks
    public static ChestGenHooks tinkerHouseChest;
    public static ChestGenHooks tinkerHousePatterns;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        stoneTorch = new StoneTorch();
        GameRegistry.registerBlock(stoneTorch, stoneTorch.getUnlocalizedName());

        tinkersBucket = new FilledBucket(Block.getBlockFromItem(tinkersBucket));
        GameRegistry.registerItem(tinkersBucket, tinkersBucket.getUnlocalizedName());

        goldHead = new GoldenHead(4, 1.2F, false);
        GameRegistry.registerItem(goldHead, goldHead.getUnlocalizedName());

        metalBlock = new TinkersRebornMetalBlock(Material.iron, 10.0F);
        GameRegistry.registerBlock(metalBlock, TinkersRebornMetalItemBlock.class, metalBlock.getUnlocalizedName());

        slimeStep = new StepSoundSlime("mob.slime", 1.0f, 1.0f);
        blueSlimeFluid = new TinkersRebornFluid("slime_blue", 0X42E9F4, true);

        slimePool = new SlimeFluid(blueSlimeFluid, Material.water);
        blueSlimeFluid.setBlock(slimePool);

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

        // Vanilla stack sizes
        Items.wooden_door.setMaxStackSize(16);
        Items.iron_door.setMaxStackSize(16);
        Items.boat.setMaxStackSize(16);
        Items.minecart.setMaxStackSize(3);
        Items.cake.setMaxStackSize(16);

        oreRegistry();
    }

    /*
     * private void craftingTableRecipes() {
     * String[] patBlock = { "###", "###", "###" };
     * String[] patSurround = { "###", "#m#", "###" };
     * // Metal conversion Recipes
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 3),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 9)); // Copper
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 5),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 10)); // Tin
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 6),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 11)); // Aluminum
     * // GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 6),
     * // patBlock, '#', new ItemStack(TRepo.materials, 1, 12)); // Aluminum
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 4),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 13)); // Bronze
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 7),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 14)); // AluBrass
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 0),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 3)); // Cobalt
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 1),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 4)); // Ardite
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 2),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 5)); // Manyullyn
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 8),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 15)); // Alumite
     * GameRegistry.addRecipe(
     * new ItemStack(metalBlock, 1, 9),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 16)); // Steel
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 11),
     * "#",
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 12)); // Aluminum raw ->
     * // ingot
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 9),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 3)); // Copper
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 10),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 5)); // Tin
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 11),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 6)); // Aluminum
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 13),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 4)); // Bronze
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 14),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 7)); // AluBrass
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 3),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 0)); // Cobalt
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 4),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 1)); // Ardite
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 5),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 2)); // Manyullyn
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 15),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 8)); // Alumite
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 16),
     * "m",
     * 'm',
     * new ItemStack(metalBlock, 1, 9)); // Steel
     * GameRegistry
     * .addRecipe(new ItemStack(Items.iron_ingot), patBlock, '#', new ItemStack(TinkerTools.materials, 1, 19)); // Iron
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 9),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 20)); // Copper
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 10),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 21)); // Tin
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 11),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 22)); // Aluminum
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 14),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 24)); // Aluminum Brass
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 18),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 27)); // Obsidian
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 3),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 28)); // Cobalt
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 4),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 29)); // Ardite
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 5),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 30)); // Manyullyn
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 13),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 31)); // Bronze
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 15),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 32)); // Alumite
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 1, 16),
     * patBlock,
     * '#',
     * new ItemStack(TinkerTools.materials, 1, 33)); // Steel
     * GameRegistry.addRecipe(new ItemStack(TinkerTools.materials, 9, 19), "m", 'm', new ItemStack(Items.iron_ingot));
     * // Iron
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 20),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 9)); // Copper
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 21),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 10)); // Tin
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 22),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 11)); // Aluminum
     * // GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 22), "m",
     * // 'm', new ItemStack(TRepo.materials, 1, 12)); //Aluminum
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 24),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 14)); // Aluminum Brass
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 27),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 18)); // Obsidian
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 28),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 3)); // Cobalt
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 29),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 4)); // Ardite
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 30),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 5)); // Manyullyn
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 31),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 13)); // Bronze
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 32),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 15)); // Alumite
     * GameRegistry.addRecipe(
     * new ItemStack(TinkerTools.materials, 9, 33),
     * "m",
     * 'm',
     * new ItemStack(TinkerTools.materials, 1, 16)); // Steel
     * String[] dyeTypes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan",
     * "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange",
     * "dyeWhite" };
     * String color;
     * for (int i = 0; i < 16; i++) {
     * color = dyeTypes[15 - i];
     * GameRegistry.addRecipe(
     * new ShapedOreRecipe(
     * new ItemStack(Blocks.wool, 8, i),
     * patSurround,
     * 'm',
     * color,
     * '#',
     * new ItemStack(Blocks.wool, 1, Short.MAX_VALUE)));
     * }
     * // Jack o'Latern Recipe - Stone Torch
     * GameRegistry.addRecipe(
     * new ItemStack(Blocks.lit_pumpkin, 1, 0),
     * "p",
     * "s",
     * 'p',
     * new ItemStack(Blocks.pumpkin),
     * 's',
     * new ItemStack(stoneTorch));
     * // Stone Torch Recipe
     * GameRegistry.addRecipe(
     * new ShapedOreRecipe(
     * new ItemStack(stoneTorch, 4),
     * "p",
     * "w",
     * 'p',
     * new ItemStack(Items.coal, 1, Short.MAX_VALUE),
     * 'w',
     * "rodStone"));
     * // Stone Ladder Recipe
     * GameRegistry.addRecipe(
     * new ShapedOreRecipe(new ItemStack(stoneLadder, 3), "w w", "www", "w w", 'w', "rodStone"));
     * // Wooden Rail (if registered) Recipe
     * if (woodenRail != null) {
     * GameRegistry.addRecipe(
     * new ShapedOreRecipe(
     * new ItemStack(woodenRail, 4, 0),
     * "b b",
     * "bxb",
     * "b b",
     * 'b',
     * "plankWood",
     * 'x',
     * "stickWood"));
     * }
     * // Stonesticks Recipes
     * GameRegistry.addRecipe(new ItemStack(TinkerTools.toolRod, 4, 1), "c", "c", 'c', new ItemStack(Blocks.stone));
     * GameRegistry
     * .addRecipe(new ItemStack(TinkerTools.toolRod, 2, 1), "c", "c", 'c', new ItemStack(Blocks.cobblestone));
     * //
     * ItemStack aluBrass = new ItemStack(TinkerTools.materials, 1, 14);
     * // Clock Recipe - Vanilla alternative
     * GameRegistry.addRecipe(
     * new ShapedOreRecipe(
     * new ItemStack(Items.clock),
     * " i ",
     * "iri",
     * " i ",
     * 'i',
     * aluBrass,
     * 'r',
     * "dustRedstone"));
     * // Gold Pressure Plate - Vanilla alternative
     * // todo: temporarily disabled due to light weighted pressure plate being smeltable to gold
     * // GameRegistry.addRecipe(new ItemStack(Blocks.light_weighted_pressure_plate, 0, 1), "ii", 'i', aluBrass);
     * // Ultra hardcore recipes
     * GameRegistry.addRecipe(
     * new ShapedOreRecipe(
     * new ItemStack(goldHead),
     * patSurround,
     * '#',
     * "ingotGold",
     * 'm',
     * new ItemStack(Items.skull, 1, 3)));
     * // Wool Slab Recipes
     * for (int sc = 0; sc <= 7; sc++) {
     * GameRegistry.addRecipe(
     * new ItemStack(woolSlab1, 6, sc),
     * "www",
     * 'w',
     * new ItemStack(Blocks.wool, 1, sc));
     * GameRegistry.addRecipe(
     * new ItemStack(woolSlab2, 6, sc),
     * "www",
     * 'w',
     * new ItemStack(Blocks.wool, 1, sc + 8));
     * GameRegistry.addShapelessRecipe(
     * new ItemStack(Blocks.wool, 1, sc),
     * new ItemStack(woolSlab1, 1, sc),
     * new ItemStack(woolSlab1, 1, sc));
     * GameRegistry.addShapelessRecipe(
     * new ItemStack(Blocks.wool, 1, sc + 8),
     * new ItemStack(woolSlab2, 1, sc),
     * new ItemStack(woolSlab2, 1, sc));
     * }
     * GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.wool, 1, 0), "slabCloth", "slabCloth"));
     * // Trap Recipes
     * GameRegistry.addRecipe(
     * new ItemStack(punji, 5, 0),
     * "b b",
     * " b ",
     * "b b",
     * 'b',
     * new ItemStack(Items.reeds));
     * GameRegistry.addRecipe(
     * new ItemStack(barricadeSpruce, 1, 0),
     * "b",
     * "b",
     * 'b',
     * new ItemStack(Blocks.log, 1, 1));
     * GameRegistry.addRecipe(
     * new ItemStack(barricadeBirch, 1, 0),
     * "b",
     * "b",
     * 'b',
     * new ItemStack(Blocks.log, 1, 2));
     * GameRegistry.addRecipe(
     * new ItemStack(barricadeJungle, 1, 0),
     * "b",
     * "b",
     * 'b',
     * new ItemStack(Blocks.log, 1, 3));
     * GameRegistry.addRecipe(
     * new ShapedOreRecipe(new ItemStack(barricadeOak, 1, 0), "b", "b", 'b', "logWood"));
     * // Slime Recipes
     * GameRegistry.addRecipe(new ItemStack(slimeGel, 1, 0), "##", "##", '#', strangeFood);
     * GameRegistry.addRecipe(
     * new ItemStack(strangeFood, 4, 0),
     * "#",
     * '#',
     * new ItemStack(slimeGel, 1, 0));
     * GameRegistry.addRecipe(new ItemStack(slimeGel, 1, 1), "##", "##", '#', Items.slime_ball);
     * GameRegistry
     * .addRecipe(new ItemStack(Items.slime_ball, 4, 0), "#", '#', new ItemStack(slimeGel, 1, 1));
     * // slimeExplosive
     * GameRegistry.addShapelessRecipe(new ItemStack(slimeExplosive, 1, 0), Items.slime_ball, Blocks.tnt);
     * GameRegistry.addShapelessRecipe(
     * new ItemStack(slimeExplosive, 1, 2),
     * strangeFood,
     * Blocks.tnt);
     * GameRegistry.addRecipe(
     * new ShapelessOreRecipe(new ItemStack(slimeExplosive, 1, 0), "slimeball", Blocks.tnt));
     * GameRegistry.addRecipe(
     * new ShapelessOreRecipe(
     * new ItemStack(slimeChannel, 1, 0),
     * new ItemStack(slimeGel, 1, Short.MAX_VALUE),
     * "dustRedstone"));
     * GameRegistry.addRecipe(
     * new ShapelessOreRecipe(
     * new ItemStack(bloodChannel, 1, 0),
     * new ItemStack(strangeFood, 1, 1),
     * new ItemStack(strangeFood, 1, 1),
     * new ItemStack(strangeFood, 1, 1),
     * new ItemStack(strangeFood, 1, 1),
     * "dustRedstone"));
     * GameRegistry.addRecipe(
     * new ShapelessOreRecipe(
     * new ItemStack(slimeChannel, 1, 0),
     * "slimeball",
     * "slimeball",
     * "slimeball",
     * "slimeball",
     * "dustRedstone"));
     * GameRegistry.addRecipe(
     * new ShapelessOreRecipe(
     * new ItemStack(slimePad, 1, 0),
     * slimeChannel,
     * "slimeball"));
     * GameRegistry.addRecipe(
     * new ItemStack(meatBlock),
     * "mmm",
     * "mbm",
     * "mmm",
     * 'b',
     * new ItemStack(Items.bone),
     * 'm',
     * new ItemStack(Items.porkchop));
     * }
     * private void addRecipesForFurnace() {
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.craftedSoil, 1, 3),
     * new ItemStack(TinkerTools.craftedSoil, 1, 4),
     * 0.2f); // Concecrated
     * // Soil
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.craftedSoil, 1, 0),
     * new ItemStack(TinkerTools.materials, 1, 1),
     * 2f); // Slime
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.craftedSoil, 1, 1),
     * new ItemStack(TinkerTools.materials, 1, 2),
     * 2f); // Seared brick item
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.craftedSoil, 1, 2),
     * new ItemStack(TinkerTools.materials, 1, 17),
     * 2f); // Blue Slime
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.craftedSoil, 1, 6),
     * new ItemStack(TinkerTools.materials, 1, 37),
     * 2f); // Nether seared
     * // brick
     * // FurnaceRecipes.smelting().func_151394_a(new ItemStack(TRepo.oreSlag,
     * // 1, new ItemStack(TRepo.materials, 1, 3), 3f);
     * // FurnaceRecipes.smelting().func_151394_a(new ItemStack(TRepo.oreSlag,
     * // 2, new ItemStack(TRepo.materials, 1, 4), 3f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreSlag, 1, 3),
     * new ItemStack(TinkerTools.materials, 1, 9),
     * 0.5f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreSlag, 1, 4),
     * new ItemStack(TinkerTools.materials, 1, 10),
     * 0.5f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreSlag, 1, 5),
     * new ItemStack(TinkerTools.materials, 1, 11),
     * 0.5f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreBerries, 1, 0),
     * new ItemStack(TinkerTools.materials, 1, 19),
     * 0.2f);
     * FurnaceRecipes.smelting()
     * .func_151394_a(new ItemStack(oreBerries, 1, 1), new ItemStack(Items.gold_nugget), 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreBerries, 1, 2),
     * new ItemStack(TinkerTools.materials, 1, 20),
     * 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreBerries, 1, 3),
     * new ItemStack(TinkerTools.materials, 1, 21),
     * 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreBerries, 1, 4),
     * new ItemStack(TinkerTools.materials, 1, 22),
     * 0.2f);
     * // FurnaceRecipes.smelting().func_151394_a(new
     * // ItemStack(TRepo.oreBerries, 5, new ItemStack(TRepo.materials, 1, 23),
     * // 0.2f);
     * FurnaceRecipes.smelting()
     * .func_151394_a(new ItemStack(oreGravel, 1, 0), new ItemStack(Items.iron_ingot), 0.2f);
     * FurnaceRecipes.smelting()
     * .func_151394_a(new ItemStack(oreGravel, 1, 1), new ItemStack(Items.gold_ingot), 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreGravel, 1, 2),
     * new ItemStack(TinkerTools.materials, 1, 9),
     * 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreGravel, 1, 3),
     * new ItemStack(TinkerTools.materials, 1, 10),
     * 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(oreGravel, 1, 4),
     * new ItemStack(TinkerTools.materials, 1, 11),
     * 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.materials, 1, 38),
     * new ItemStack(TinkerTools.materials, 1, 4),
     * 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.materials, 1, 39),
     * new ItemStack(TinkerTools.materials, 1, 3),
     * 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.materials, 1, 40),
     * new ItemStack(TinkerTools.materials, 1, 11),
     * 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.materials, 1, 41),
     * new ItemStack(TinkerTools.materials, 1, 5),
     * 0.2f);
     * FurnaceRecipes.smelting().func_151394_a(
     * new ItemStack(TinkerTools.materials, 1, 42),
     * new ItemStack(TinkerTools.materials, 1, 14),
     * 0.2f);
     * }
     */

    private void oreRegistry() {
        ItemStack craftingTable = new ItemStack(Blocks.crafting_table, 1);

        OreDictionary.registerOre("oreCobalt", new ItemStack(oreSlag, 1, 1));
        OreDictionary.registerOre("oreArdite", new ItemStack(oreSlag, 1, 2));
        OreDictionary.registerOre("oreCopper", new ItemStack(oreSlag, 1, 3));
        OreDictionary.registerOre("oreTin", new ItemStack(oreSlag, 1, 4));
        OreDictionary.registerOre("oreAluminum", new ItemStack(oreSlag, 1, 5));
        OreDictionary.registerOre("oreAluminium", new ItemStack(oreSlag, 1, 5));

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

    @Override
    public void init(FMLInitializationEvent e) {
        if (!TinkersRebornConfig.disableAllRecipes) {
            // craftingTableRecipes();
            // addRecipesForFurnace();
        }

        proxy.initialize();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {

    }
}
