package mctbl.tinkersreborn.smeltery;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.smeltery.blocks.FurnaceController;
import mctbl.tinkersreborn.smeltery.blocks.GlueBlock;
import mctbl.tinkersreborn.smeltery.blocks.LavaTankBlock;
import mctbl.tinkersreborn.smeltery.blocks.SearedBlock;
import mctbl.tinkersreborn.smeltery.blocks.SmelteryBlock;
import mctbl.tinkersreborn.smeltery.blocks.SmelteryController;
import mctbl.tinkersreborn.smeltery.blocks.SmelteryDrain;
import mctbl.tinkersreborn.smeltery.entity.CastingBasinLogic;
import mctbl.tinkersreborn.smeltery.entity.CastingTableLogic;
import mctbl.tinkersreborn.smeltery.entity.FaucetLogic;
import mctbl.tinkersreborn.smeltery.entity.LavaTankLogic;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryDrainLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.smeltery.itemblocks.FurnaceControllerItemBlock;
import mctbl.tinkersreborn.smeltery.itemblocks.LavaTankItemBlock;
import mctbl.tinkersreborn.smeltery.itemblocks.SearedTableItemBlock;
import mctbl.tinkersreborn.smeltery.itemblocks.SmelteryControllerItemBlock;
import mctbl.tinkersreborn.smeltery.itemblocks.SmelteryDrainItemBlock;
import mctbl.tinkersreborn.smeltery.itemblocks.SmelteryItemBlock;
import mctbl.tinkersreborn.smeltery.utils.MeltingRecipe;
import mctbl.tinkersreborn.tools.TinkersRebornTools;

public class TinkersRebornSmeltery implements ITinkersRebornModule {

    public static Block smelteryBlock;
    public static Block smelteryController;
    public static Block smelteryDrain;
    public static Block furnaceController;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block castingChannel;

    public static Block glueBlock;

    // TODO
    // public static Block clearGlass;
    // public static Block stainedGlassClear;
    // public static Block glassPane;
    // public static Block stainedGlassClearPane;

    @SidedProxy(
        clientSide = "mctbl.tinkersreborn.smeltery.TinkersRebornSmelteryProxyClient",
        serverSide = "mctbl.tinkersreborn.smeltery.TinkersRebornSmelteryProxyCommon")
    public static TinkersRebornSmelteryProxyCommon proxy;

    @Override
    public void preInit(FMLPreInitializationEvent e) {

        glueBlock = new GlueBlock();
        GameRegistry.registerBlock(glueBlock, glueBlock.getUnlocalizedName());
        OreDictionary.registerOre("blockRubber", new ItemStack(glueBlock));

        smelteryBlock = new SmelteryBlock();
        GameRegistry.registerBlock(smelteryBlock, SmelteryItemBlock.class, smelteryBlock.getUnlocalizedName());

        smelteryController = new SmelteryController();
        GameRegistry.registerBlock(
            smelteryController,
            SmelteryControllerItemBlock.class,
            smelteryController.getUnlocalizedName());

        smelteryDrain = new SmelteryDrain();
        GameRegistry.registerBlock(smelteryDrain, SmelteryDrainItemBlock.class, smelteryDrain.getUnlocalizedName());

        furnaceController = new FurnaceController();
        GameRegistry
            .registerBlock(furnaceController, FurnaceControllerItemBlock.class, furnaceController.getUnlocalizedName());

        GameRegistry.registerTileEntity(SmelteryLogic.class, "tinkersreborn.Smeltery");
        GameRegistry.registerTileEntity(SmelteryDrainLogic.class, "tinkersreborn.SmelteryDrain");
        GameRegistry.registerTileEntity(MultiServantLogic.class, "tinkersreborn.Servants");

        lavaTank = new LavaTankBlock();
        GameRegistry.registerBlock(lavaTank, LavaTankItemBlock.class, lavaTank.getUnlocalizedName());
        GameRegistry.registerTileEntity(LavaTankLogic.class, "tinkersreborn.LavaTank");

        searedBlock = new SearedBlock();
        GameRegistry.registerBlock(searedBlock, SearedTableItemBlock.class, searedBlock.getUnlocalizedName());
        GameRegistry.registerTileEntity(CastingTableLogic.class, "tinkersreborn.CastingTable");
        GameRegistry.registerTileEntity(FaucetLogic.class, "tinkersreborn.Faucet");
        GameRegistry.registerTileEntity(CastingBasinLogic.class, "tinkersreborn.CastingBasin");

        TinkersRebornRegistry.registerFuel(new FluidStack(FluidRegistry.LAVA, 50), 100);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        this.craftingTableRecipes();

        this.registerMeltingCasting();

        proxy.initialize();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {

        TinkersRebornRegistry.registerEntityMelting();
        this.registerAlloys();
    }

    private void craftingTableRecipes() {

        // Define
        String[] patSurround = { "###", "#m#", "###" };
        ItemStack searedBrick = new ItemStack(TinkersRebornTools.searedBrick, 1);

        // Register
        GameRegistry.addRecipe(new ItemStack(smelteryBlock, 1, 0), "bb", "bb", 'b', searedBrick); // Bricks Block
        GameRegistry.addRecipe(new ItemStack(smelteryController, 1), "bbb", "b b", "bbb", 'b', searedBrick); // Controller
        // GameRegistry.addRecipe(new ItemStack(smelteryBlock, 1, 3), " b ", "b b",
        // "bbb", 'b', searedBrick); // Furnace
        GameRegistry.addRecipe(new ItemStack(smelteryDrain, 1), "b b", "b b", "b b", 'b', searedBrick); // Drain
        GameRegistry.addRecipe(
            new ShapedOreRecipe(new ItemStack(lavaTank, 1, 0), patSurround, '#', searedBrick, 'm', "blockGlass")); // Tank
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(lavaTank, 1, 1),
                "bgb",
                "ggg",
                "bgb",
                'b',
                searedBrick,
                'g',
                "blockGlass")); // Glass
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(lavaTank, 1, 2),
                "bgb",
                "bgb",
                "bgb",
                'b',
                searedBrick,
                'g',
                "blockGlass")); // Window

        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); // Table
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 1), "b b", " b ", 'b', searedBrick); // Faucet
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); // Basin
        GameRegistry.addRecipe(new ItemStack(castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); // Channel
    }

    private void registerMeltingCasting() {
        int bucket = 1000;

        // Water
        Fluid water = FluidRegistry.WATER;
        TinkersRebornRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.ice, bucket), water, 305));
        TinkersRebornRegistry
            .registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.packed_ice, bucket * 2), water, 310));
        TinkersRebornRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.snow, bucket), water, 305));
        TinkersRebornRegistry
            .registerMelting(new MeltingRecipe(RecipeMatch.of(Items.snowball, bucket / 8), water, 301));

        // bloooooood
        // TinkersRebornRegistry.registerMelting(Items.rotten_flesh, TinkerFluids.blood, 40);
        // TinkersRebornTools.obsidianFluid

        // obsidian
        TinkersRebornRegistry.registerMelting(
            MeltingRecipe.forAmount(
                RecipeMatch.of("obsidian", TinkersRebornMaterial.VALUE_Ingot * 2),
                TinkersRebornTools.obsidianFluid,
                TinkersRebornMaterial.VALUE_Ingot * 2));

        // special melting
        TinkersRebornRegistry.registerMelting(
            Items.iron_horse_armor,
            TinkersRebornTools.ironFluid,
            TinkersRebornMaterial.VALUE_Ingot * 4);
        // TinkersRebornRegistry.registerMelting(Items.golden_horse_armor, TinkersRebornTools.goldFluid,
        // TinkersRebornMaterial.VALUE_Ingot * 4);

        // rails, some of these are caught through registerOredictMelting, but for
        // consistency all are just registered here
        TinkersRebornRegistry
            .registerMelting(Blocks.rail, TinkersRebornTools.ironFluid, TinkersRebornMaterial.VALUE_Ingot * 6 / 16);
        TinkersRebornRegistry
            .registerMelting(Blocks.activator_rail, TinkersRebornTools.ironFluid, TinkersRebornMaterial.VALUE_Ingot);
        TinkersRebornRegistry
            .registerMelting(Blocks.detector_rail, TinkersRebornTools.ironFluid, TinkersRebornMaterial.VALUE_Ingot);
        // TinkersRebornRegistry.registerMelting(Blocks.golden_rail, TinkersRebornTools.goldFluid,
        // TinkersRebornMaterial.VALUE_Ingot);
    }

    /**
     * Called by Tinkers Integration to register allows, some are conditional on integrations being loaded
     */
    public void registerAlloys() {
        // 1 bucket lava + 1 bucket water = 2 ingots = 1 block obsidian
        // 1000 + 1000 = 288
        // 125 + 125 = 36
        TinkersRebornRegistry.registerAlloy(
            new FluidStack(TinkersRebornTools.obsidianFluid, 36),
            new FluidStack(FluidRegistry.WATER, 125),
            new FluidStack(FluidRegistry.LAVA, 125));

        // 1 iron ingot + 80mB blood + 640mB emerald = 1 pigiron
        TinkersRebornRegistry.registerAlloy(
            new FluidStack(TinkersRebornTools.pigIronFluid, 144),
            new FluidStack(TinkersRebornTools.ironFluid, 144),
            new FluidStack(TinkersRebornGeneral.bloodFluid, 80),
            new FluidStack(TinkersRebornTools.emeraldFluid, 640));

        // 2 ingot cobalt + 2 ingot ardite = 2 ingot manyullyn!
        // 144 + 144 = 144
        TinkersRebornRegistry.registerAlloy(
            new FluidStack(TinkersRebornTools.manyullynFluid, 2),
            new FluidStack(TinkersRebornTools.cobaltFluid, 2),
            new FluidStack(TinkersRebornTools.arditeFluid, 2));

        // 3 ingots copper + 1 ingot tin = 4 ingots bronze
        if (TinkersRebornRegistry.isIntegrated(
            TinkersRebornTools.bronzeFluid,
            TinkersRebornTools.copperFluid,
            TinkersRebornTools.tinFluid)) {
            TinkersRebornRegistry.registerAlloy(
                new FluidStack(TinkersRebornTools.bronzeFluid, 4),
                new FluidStack(TinkersRebornTools.copperFluid, 3),
                new FluidStack(TinkersRebornTools.tinFluid, 1));
        }

        // 1 ingot aluminum + 1 ingot iron + 1 obsidian = 1 alumite
        // 144 + 144 + 288 = 144
        if (TinkersRebornRegistry.isIntegrated(
            TinkersRebornTools.alumiteFluid,
            TinkersRebornTools.aluminumFluid,
            TinkersRebornTools.ironFluid)) {
            TinkersRebornRegistry.registerAlloy(
                new FluidStack(TinkersRebornTools.alumiteFluid, 144),
                new FluidStack(TinkersRebornTools.aluminumFluid, 144),
                new FluidStack(TinkersRebornTools.ironFluid, 144),
                new FluidStack(TinkersRebornTools.obsidianFluid, 288));
        }
    }
}
