package mctbl.tinkersreborn.smeltery;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
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

    }

    @Override
    public void init(FMLInitializationEvent e) {

        proxy.initialize();
        this.craftingTableRecipes();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {

    }

    private void craftingTableRecipes() {

        // Define
        String[] patSurround = { "###", "#m#", "###" };
        ItemStack searedBrick = new ItemStack(TinkersRebornTools.searedBrick, 1);

        // Register
        GameRegistry.addRecipe(new ItemStack(smelteryBlock, 1, 0), "bb", "bb", 'b', searedBrick); // Bricks Block
        GameRegistry.addRecipe(new ItemStack(smelteryController, 1), "bbb", "b b", "bbb", 'b', searedBrick); // Controller
        // GameRegistry.addRecipe(new ItemStack(smelteryBlock, 1, 3), " b ", "b b", "bbb", 'b', searedBrick); // Furnace
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
}
