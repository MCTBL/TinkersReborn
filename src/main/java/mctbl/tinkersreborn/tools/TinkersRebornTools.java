package mctbl.tinkersreborn.tools;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.tools.blocks.CastChestBlock;
import mctbl.tinkersreborn.tools.blocks.CraftingStationBlock;
import mctbl.tinkersreborn.tools.blocks.PartBuilderBlock;
import mctbl.tinkersreborn.tools.blocks.PartChestBlock;
import mctbl.tinkersreborn.tools.blocks.ToolForgeBlock;
import mctbl.tinkersreborn.tools.blocks.ToolStationBlock;
import mctbl.tinkersreborn.tools.entity.CastChestLogic;
import mctbl.tinkersreborn.tools.entity.CraftingStationLogic;
import mctbl.tinkersreborn.tools.entity.PartChestLogic;
import mctbl.tinkersreborn.tools.entity.TinkersRebornPartBuilderLogic;
import mctbl.tinkersreborn.tools.entity.TinkersRebornToolForgeLogic;
import mctbl.tinkersreborn.tools.entity.TinkersRebornToolStationLogic;
import mctbl.tinkersreborn.tools.itemblocks.TinkersRebornCastChestItemBlock;
import mctbl.tinkersreborn.tools.itemblocks.TinkersRebornPartBuilderItemBlock;
import mctbl.tinkersreborn.tools.itemblocks.TinkersRebornPartChestItemBlock;
import mctbl.tinkersreborn.tools.itemblocks.TinkersRebornToolForgeItemBlock;
import mctbl.tinkersreborn.tools.itemblocks.TinkersRebornToolStationItemBlock;
import mctbl.tinkersreborn.tools.items.BowString;
import mctbl.tinkersreborn.tools.items.Fletching;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;

public class TinkersRebornTools implements ITinkersRebornModule {

	@SidedProxy(clientSide = "mctbl.tinkersreborn.tools.TinkersRebornToolsProxyClient", serverSide = "mctbl.tinkersreborn.tools.TinkersRebornToolsProxyCommon")
	public static TinkersRebornToolsProxyCommon proxy;

	// Crafting blocks
	public static Block toolStation;
	public static Block toolForge;
	public static Block partBuilder;
	public static Block castChest;
	public static Block partChest;
	public static Block craftingStation;

//    public static Block heldItemBlock;
//    public static Block battlesignBlock;

	// Tool parts
	public static Item arrowhead;
	public static Item arrowShaft;
	public static Item axeHead;
	public static Item battlesignHead;
	public static Item binding;
	public static Item bowLimb;
	public static Item chiselHead;
	public static Item crossbar;
	public static Item crossbowBody;
	public static Item crossbowLimb;
	public static Item excavatorHead;
	public static Item frypanHead;
	public static Item fullGuard;
	public static Item hammerHead;
	public static Item knifeBlade;
	public static Item largeplate;
	public static Item largeGuard;
	public static Item largeSwordBlade;
	public static Item lumberaxeHead;
	public static Item mediumGuard;
	public static Item pickaxeHead;
	public static Item rod;
	public static Item scytheHead;
	public static Item shard;
	public static Item shovelHead;
	public static Item shuriken;
	public static Item swordBlade;
	public static Item toughbind;
	public static Item toughrod;
	
	public static Item bowString;
	public static Item fletching;

	// TODO
	public static Item boltCore;
	
	
	@Override
	public void preInit(FMLPreInitializationEvent e) {
		toolStation = new ToolStationBlock();
		GameRegistry.registerBlock(toolStation, TinkersRebornToolStationItemBlock.class,
				toolStation.getUnlocalizedName());
		GameRegistry.registerTileEntity(TinkersRebornToolStationLogic.class, toolStation.getUnlocalizedName());

		toolForge = new ToolForgeBlock();
		GameRegistry.registerBlock(toolForge, TinkersRebornToolForgeItemBlock.class, toolForge.getUnlocalizedName());
		GameRegistry.registerTileEntity(TinkersRebornToolForgeLogic.class, toolForge.getUnlocalizedName());

		partBuilder = new PartBuilderBlock();
		GameRegistry.registerBlock(partBuilder, TinkersRebornPartBuilderItemBlock.class,
				partBuilder.getUnlocalizedName());
		GameRegistry.registerTileEntity(TinkersRebornPartBuilderLogic.class, partBuilder.getUnlocalizedName());

		castChest = new CastChestBlock();
		GameRegistry.registerBlock(castChest, TinkersRebornCastChestItemBlock.class, castChest.getUnlocalizedName());
		GameRegistry.registerTileEntity(CastChestLogic.class, castChest.getUnlocalizedName());

		partChest = new PartChestBlock();
		GameRegistry.registerBlock(partChest, TinkersRebornPartChestItemBlock.class, partChest.getUnlocalizedName());
		GameRegistry.registerTileEntity(PartChestLogic.class, partChest.getUnlocalizedName());

		craftingStation = new CraftingStationBlock();
		GameRegistry.registerBlock(craftingStation, craftingStation.getUnlocalizedName());
		GameRegistry.registerTileEntity(CraftingStationLogic.class, craftingStation.getUnlocalizedName());

		arrowhead = new TinkersRebornToolPart("arrowhead", "Arrowhead");
		arrowShaft = new TinkersRebornToolPart("arrow_shaft", "ArrowShaft");
		axeHead = new TinkersRebornToolPart("axe_head", "AxeHead");
		battlesignHead = new TinkersRebornToolPart("battlesign_head", "BattlesignHead");
		binding = new TinkersRebornToolPart("binding", "Binding");
		bowLimb = new TinkersRebornToolPart("bow_limb", "BowLimb");
		chiselHead = new TinkersRebornToolPart("chisel_head", "ChiselHead");
		crossbar = new TinkersRebornToolPart("crossbar", "Crossbar");
		crossbowBody = new TinkersRebornToolPart("crossbow_body", "CrossbowBody");
		crossbowLimb = new TinkersRebornToolPart("crossbow_limb", "CrossbowLimb");
		excavatorHead = new TinkersRebornToolPart("excavator_head", "ExcavatorHead");
		frypanHead = new TinkersRebornToolPart("frypan_head", "FrypanHead");
		fullGuard = new TinkersRebornToolPart("full_guard", "FullGuard");
		hammerHead = new TinkersRebornToolPart("hammer_head", "HammerHead");
		knifeBlade = new TinkersRebornToolPart("knife_blade", "KnifeBlade");
		largeplate = new TinkersRebornToolPart("largeplate", "Largeplate");
		largeGuard = new TinkersRebornToolPart("large_guard", "LargeGuard");
		largeSwordBlade = new TinkersRebornToolPart("large_sword_blade", "LargeSwordBlade");
		lumberaxeHead = new TinkersRebornToolPart("lumberaxe_head", "LumberaxeHead");
		mediumGuard = new TinkersRebornToolPart("medium_guard", "MediumGuard");
		pickaxeHead = new TinkersRebornToolPart("pickaxe_head", "PickaxeHead");
		rod = new TinkersRebornToolPart("rod", "Rod");
		scytheHead = new TinkersRebornToolPart("scythe_head", "ScytheHead");
		shard = new TinkersRebornToolPart("shard", "Shard");
		shovelHead = new TinkersRebornToolPart("shovel_head", "ShovelHead");
		shuriken = new TinkersRebornToolPart("shuriken", "Shuriken");
		swordBlade = new TinkersRebornToolPart("sword_blade", "SwordBlade");
		toughbind = new TinkersRebornToolPart("toughbind", "Toughbind");
		toughrod = new TinkersRebornToolPart("toughrod", "Toughrod");
		
		bowString = new BowString();
		fletching = new Fletching();
//        new TinkersRebornToolPart("bolt","");
	}

	@Override
	public void init(FMLInitializationEvent e) {

		proxy.initialize();
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		// TODO Auto-generated method stub

	}
}
