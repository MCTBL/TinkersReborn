package mctbl.tinkersreborn.smeltery;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.smeltery.blocks.GlueBlock;
import mctbl.tinkersreborn.smeltery.blocks.LavaTankBlock;
import mctbl.tinkersreborn.smeltery.blocks.SearedBlock;
import mctbl.tinkersreborn.smeltery.blocks.SmelteryBlock;
import mctbl.tinkersreborn.smeltery.entity.CastingBasinLogic;
import mctbl.tinkersreborn.smeltery.entity.CastingTableLogic;
import mctbl.tinkersreborn.smeltery.entity.FaucetLogic;
import mctbl.tinkersreborn.smeltery.entity.LavaTankLogic;
import mctbl.tinkersreborn.smeltery.entity.MultiServantLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryDrainLogic;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.smeltery.itemblocks.LavaTankItemBlock;
import mctbl.tinkersreborn.smeltery.itemblocks.SearedTableItemBlock;
import mctbl.tinkersreborn.smeltery.itemblocks.SmelteryItemBlock;

public class TinkersRebornSmeltery implements ITinkersRebornModule {

    public static Item buckets;

    public static Block smeltery;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block castingChannel;
    public static Block glueBlock;
    public static Block clearGlass;
    public static Block stainedGlassClear;
    public static Block glassPane;
    public static Block stainedGlassClearPane;

    // TODO
    // private static FluidType metalPatternFluidType;

    @SidedProxy(
        clientSide = "mctbl.tinkersreborn.smeltery.TinkersRebornSmelteryProxyClient",
        serverSide = "mctbl.tinkersreborn.smeltery.TinkersRebornSmelteryProxyCommon")
    public static TinkersRebornSmelteryProxyCommon proxy;

    @Override
    public void preInit(FMLPreInitializationEvent e) {

        glueBlock = new GlueBlock();
        GameRegistry.registerBlock(glueBlock, glueBlock.getUnlocalizedName());
        OreDictionary.registerOre("blockRubber", new ItemStack(glueBlock));

        smeltery = new SmelteryBlock();
        GameRegistry.registerBlock(smeltery, SmelteryItemBlock.class, smeltery.getUnlocalizedName());

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
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {

    }
}
