package mctbl.tinkersreborn.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.client.StepSoundSlime;
import mctbl.tinkersreborn.common.blocks.ConsecratedSoil;
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
import mctbl.tinkersreborn.common.entity.KingBlueSlime;
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
import mctbl.tinkersreborn.tools.entity.FancyEntityItem;
import mctbl.tinkersreborn.util.RecipeRemover;
import mctbl.tinkersreborn.world.gen.TinkersRebornSurfaceOreGen;
import mctbl.tinkersreborn.world.gen.TinkersRebornWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class TinkersRebornGeneral implements ITinkersRebornModule {

    @SidedProxy(clientSide = "mctbl.tinkersreborn.common.TinkersRebornGeneralProxyClient", serverSide = "mctbl.tinkersreborn.common.TinkersRebornGeneralProxyCommon")
    public static TinkersRebornGeneralProxyCommon proxy;

    public static Item tinkersBucket;
    public static Block stoneTorch;
    public static Item goldHead;
    public static Block metalBlock;
    public static Block slimeSand;
    public static Block grout;
    public static Block graveyardSoil;
    public static Block consecratedSoil;
    public static Block slimeDirt;

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
	blueSlimeFluid = new TinkersRebornFluid("slime_blue", 0X42E9F4, true, false);

	slimePool = new SlimeFluid(blueSlimeFluid);
	GameRegistry.registerBlock(slimePool, slimePool.getUnlocalizedName());
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

    @Override
    public void init(FMLInitializationEvent e) {
	if (!TinkersRebornConfig.disableAllRecipes) {
	    // craftingTableRecipes();
	    // addRecipesForFurnace();
	}
	this.addLoot();
	this.createEntities();
	proxy.initialize();
	
        GameRegistry.registerWorldGenerator(new TinkersRebornWorldGenerator(), 0);
        MinecraftForge.TERRAIN_GEN_BUS.register(new TinkersRebornSurfaceOreGen());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {

    }

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
	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.lead, 2), "ss ", "sS ", "  s", 's', Items.string,
		'S', "slimeball"));
    }

    private void addLoot() {
	// TODO add some loot to village
    }

    private void createEntities() {
	EntityRegistry.registerModEntity(FancyEntityItem.class, "Tinkers Fancy Item", 0, TinkersReborn.instance, 32, 5,true);
	EntityRegistry.registerModEntity(BlueSlime.class, "Tinkers Blue Slime", 1, TinkersReborn.instance, 64, 5, true);
	EntityRegistry.registerModEntity(KingBlueSlime.class, "Tinkers King Slime", 2, TinkersReborn.instance, 64, 5, true);
	
	
	if(TinkersRebornConfig.naturalSlimeSpawn > 1) {
	    Type[] biomeTypes = {Type.FOREST, Type.PLAINS, Type.MOUNTAIN, Type.HILLS, Type.SWAMP, Type.JUNGLE, Type.WASTELAND};
	    Set<BiomeGenBase> set = new HashSet<>();
	    for(Type t : biomeTypes) {
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
}
