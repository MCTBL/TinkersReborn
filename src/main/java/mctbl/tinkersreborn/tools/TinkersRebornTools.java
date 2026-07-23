package mctbl.tinkersreborn.tools;

import static mctbl.tinkersreborn.library.TinkersRebornRegistry.integrate;
import static mctbl.tinkersreborn.library.materials.TinkersRebornMaterial.VALUE_Fragment;
import static mctbl.tinkersreborn.library.materials.TinkersRebornMaterial.VALUE_Ingot;
import static mctbl.tinkersreborn.library.materials.TinkersRebornMaterial.VALUE_Shard;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.alien;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.aridiculous;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.baconlicious;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.breakable;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.cheap;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.cheapskate;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.coldblooded;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.crude;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.crude2;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.dense;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.duritos;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.ecological;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.enderference;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.established;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.fractured;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.freezing;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.heavy;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.hellish;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.holy;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.hovering;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.insatiable;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.lightweight;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.magnetic;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.magnetic2;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.momentum;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.petramor;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.poisonous;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.prickly;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.raging;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.raging2;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.sharp;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.slimeyBlue;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.slimeyGreen;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.spiky;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.splintering;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.splitting;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.stiff;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.stonebound;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.tasty;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.writable;
import static mctbl.tinkersreborn.tools.TinkersRebornTraits.writable2;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.common.TinkersRebornGeneral;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.utils.MiningLevelHelper;
import mctbl.tinkersreborn.library.utils.RecipeMatch;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
import mctbl.tinkersreborn.smeltery.utils.MaterialIntegration;
import mctbl.tinkersreborn.tools.blocks.CastChestBlock;
import mctbl.tinkersreborn.tools.blocks.CraftingStationBlock;
import mctbl.tinkersreborn.tools.blocks.PartBuilderBlock;
import mctbl.tinkersreborn.tools.blocks.PartChestBlock;
import mctbl.tinkersreborn.tools.blocks.ToolForgeBlock;
import mctbl.tinkersreborn.tools.blocks.ToolStationBlock;
import mctbl.tinkersreborn.tools.entity.CastChestLogic;
import mctbl.tinkersreborn.tools.entity.CraftingStationLogic;
import mctbl.tinkersreborn.tools.entity.PartBuilderLogic;
import mctbl.tinkersreborn.tools.entity.PartChestLogic;
import mctbl.tinkersreborn.tools.entity.ToolForgeLogic;
import mctbl.tinkersreborn.tools.entity.ToolStationLogic;
import mctbl.tinkersreborn.tools.itemblocks.CastChestItemBlock;
import mctbl.tinkersreborn.tools.itemblocks.PartBuilderItemBlock;
import mctbl.tinkersreborn.tools.itemblocks.PartChestItemBlock;
import mctbl.tinkersreborn.tools.itemblocks.ToolForgeItemBlock;
import mctbl.tinkersreborn.tools.itemblocks.ToolStationItemBlock;
import mctbl.tinkersreborn.tools.items.BoltCore;
import mctbl.tinkersreborn.tools.items.BowString;
import mctbl.tinkersreborn.tools.items.Fletching;
import mctbl.tinkersreborn.tools.items.MaterialItem;
import mctbl.tinkersreborn.tools.items.Pattern;
import mctbl.tinkersreborn.tools.items.SharpeningKit;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.tools.items.tools.Arrow;
import mctbl.tinkersreborn.tools.items.tools.Bolt;
import mctbl.tinkersreborn.tools.items.tools.BroadSword;
import mctbl.tinkersreborn.tools.items.tools.Cleaver;
import mctbl.tinkersreborn.tools.items.tools.Excavator;
import mctbl.tinkersreborn.tools.items.tools.Hammer;
import mctbl.tinkersreborn.tools.items.tools.Hatchet;
import mctbl.tinkersreborn.tools.items.tools.Kama;
import mctbl.tinkersreborn.tools.items.tools.LongBow;
import mctbl.tinkersreborn.tools.items.tools.LongSword;
import mctbl.tinkersreborn.tools.items.tools.LumberAxe;
import mctbl.tinkersreborn.tools.items.tools.Mattock;
import mctbl.tinkersreborn.tools.items.tools.Pickaxe;
import mctbl.tinkersreborn.tools.items.tools.Rapier;
import mctbl.tinkersreborn.tools.items.tools.Scythe;
import mctbl.tinkersreborn.tools.items.tools.ShortBow;
import mctbl.tinkersreborn.tools.items.tools.Shovel;
import mctbl.tinkersreborn.tools.items.tools.Vinehammer;
import mctbl.tinkersreborn.tools.materials.BowMaterialStats;
import mctbl.tinkersreborn.tools.materials.ExtraMaterialStats;
import mctbl.tinkersreborn.tools.materials.FletchingMaterialStats;
import mctbl.tinkersreborn.tools.materials.HandleMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.materials.ShaftMaterialStats;
import mctbl.tinkersreborn.tools.materials.StringMaterialStats;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class TinkersRebornTools implements ITinkersRebornModule {

    @SidedProxy(
        clientSide = "mctbl.tinkersreborn.tools.TinkersRebornToolsProxyClient",
        serverSide = "mctbl.tinkersreborn.tools.TinkersRebornToolsProxyCommon")
    public static TinkersRebornToolsProxyCommon proxy;

    // Crafting blocks
    public static Block toolStation;
    public static Block toolForge;
    public static Block partBuilder;
    public static Block castChest;
    public static Block partChest;
    public static Block craftingStation;

    // public static Block heldItemBlock;
    // public static Block battlesignBlock;

    // Tool parts
    public static TinkersRebornToolPart arrowhead;
    public static TinkersRebornToolPart arrowShaft;
    public static TinkersRebornToolPart axeHead;
    public static TinkersRebornToolPart battlesignHead;
    public static TinkersRebornToolPart binding;
    public static TinkersRebornToolPart bowLimb;
    public static TinkersRebornToolPart chiselHead;
    public static TinkersRebornToolPart crossbar;
    public static TinkersRebornToolPart crossbowBody;
    public static TinkersRebornToolPart crossbowLimb;
    public static TinkersRebornToolPart excavatorHead;
    public static TinkersRebornToolPart frypanHead;
    public static TinkersRebornToolPart fullGuard;
    public static TinkersRebornToolPart hammerHead;
    public static TinkersRebornToolPart knifeBlade;
    public static TinkersRebornToolPart largeplate;
    public static TinkersRebornToolPart largeGuard;
    public static TinkersRebornToolPart largeSwordBlade;
    public static TinkersRebornToolPart lumberaxeHead;
    public static TinkersRebornToolPart mediumGuard;
    public static TinkersRebornToolPart pickaxeHead;
    public static TinkersRebornToolPart rod;
    public static TinkersRebornToolPart scytheHead;
    public static TinkersRebornToolPart shard;
    public static TinkersRebornToolPart shovelHead;
    public static TinkersRebornToolPart shuriken;
    public static TinkersRebornToolPart swordBlade;
    public static TinkersRebornToolPart toughbind;
    public static TinkersRebornToolPart toughrod;
    public static TinkersRebornToolPart kamaHead;

    public static TinkersRebornToolPart bowString;
    public static TinkersRebornToolPart fletching;
    public static TinkersRebornToolPart boltCore;

    public static TinkersRebornToolPart sharpeningKit;

    // Tools
    public static ToolCore pickaxe;
    public static ToolCore shovel;
    public static ToolCore hatchet;
    public static ToolCore kama;
    public static ToolCore mattock;
    public static ToolCore hammer;
    public static ToolCore excavator;
    public static ToolCore lumberAxe;
    public static ToolCore scythe;
    public static ToolCore cleaver;
    public static ToolCore vineHammer;

    public static ToolCore broadSword;
    public static ToolCore longSword;
    public static ToolCore rapier;

    public static ToolCore shortBow;
    public static ToolCore longBow;
    public static ToolCore arrow;
    public static ToolCore bolt;

    // other items
    public static Item paperStack;
    public static Item slimeCrystal;
    public static Item searedBrick;
    public static Item cobaltIngot;
    public static Item arditeIngot;
    public static Item manyullynIngot;
    public static Item mossball;
    public static Item lavaCrystal;
    public static Item necroticBone;
    public static Item copperIngot;
    public static Item tinIngot;
    public static Item aluminumIngot;
    public static Item rawAluminum;
    public static Item bronzeIngot;
    public static Item aluBrassIngot;
    public static Item alumiteIngot;
    public static Item steelIngot;
    public static Item blueSlimeCrystal;
    public static Item obsidianIngot;
    public static Item ironNugget;
    public static Item copperNugget;
    public static Item tinNugget;
    public static Item aluminumNugget;
    public static Item aluBrassNugget;
    public static Item silkyCloth;
    public static Item silkyJewel;
    public static Item obsidianNugget;
    public static Item cobaltNugget;
    public static Item arditeNugget;
    public static Item manyullynNugget;
    public static Item bronzeNugget;
    public static Item alumiteNugget;
    public static Item steelNugget;
    public static Item pigIronIngot;
    public static Item pigIronNugget;
    public static Item glueBall;
    public static Item arditeDust;
    public static Item cobaltDust;
    public static Item aluminumDust;
    public static Item manyullynDust;
    public static Item aluBrassDust;
    public static Item reinforcement;

    public static Pattern patternAndCast;
    public static Item creativeModifier; // TODO

    public static TinkersRebornFluid ironFluid;
    public static TinkersRebornFluid obsidianFluid;
    public static TinkersRebornFluid cobaltFluid;
    public static TinkersRebornFluid arditeFluid;
    public static TinkersRebornFluid manyullynFluid;
    public static TinkersRebornFluid copperFluid;
    public static TinkersRebornFluid bronzeFluid;
    public static TinkersRebornFluid alumiteFluid;
    public static TinkersRebornFluid steelFluid;
    public static TinkersRebornFluid pigIronFluid;
    public static TinkersRebornFluid goldFluid;
    public static TinkersRebornFluid tinFluid;
    public static TinkersRebornFluid silverFluid;
    public static TinkersRebornFluid leadFluid;
    public static TinkersRebornFluid emeraldFluid;
    public static TinkersRebornFluid aluminumFluid;

    public static TinkersRebornMaterial woodMaterial;
    public static TinkersRebornMaterial stoneMaterial;
    public static TinkersRebornMaterial ironMaterial;
    public static TinkersRebornMaterial flintMaterial;
    public static TinkersRebornMaterial cactusMaterial;
    public static TinkersRebornMaterial boneMaterial;
    public static TinkersRebornMaterial obsidianMaterial;
    public static TinkersRebornMaterial netherrackMaterial;
    public static TinkersRebornMaterial slimeMaterial;
    public static TinkersRebornMaterial paperMaterial;
    public static TinkersRebornMaterial cobaltMaterial;
    public static TinkersRebornMaterial arditeMaterial;
    public static TinkersRebornMaterial manyullynMaterial;
    public static TinkersRebornMaterial copperMaterial;
    public static TinkersRebornMaterial bronzeMaterial;
    public static TinkersRebornMaterial alumiteMaterial;
    public static TinkersRebornMaterial steelMaterial;
    public static TinkersRebornMaterial blueSlimeMaterial;
    public static TinkersRebornMaterial pigIronMaterial;
    public static TinkersRebornMaterial endStoneMaterial;

    public static TinkersRebornMaterial leadMaterial;
    public static TinkersRebornMaterial silverMaterial;
    public static TinkersRebornMaterial bloodBoneMaterial;

    // bowstring materials
    public static TinkersRebornMaterial stringMaterial;
    public static TinkersRebornMaterial vineMaterial;
    public static TinkersRebornMaterial slimeVineMaterial;

    // additional arrow shaft
    public static TinkersRebornMaterial blazeMaterial;
    public static TinkersRebornMaterial reedMaterial;
    public static TinkersRebornMaterial iceMaterial;

    // fletching
    public static TinkersRebornMaterial featherMaterial;
    public static TinkersRebornMaterial leafMaterial;
    public static TinkersRebornMaterial slimeleafMaterial;

    public static ItemStack castShard;
    public static ItemStack castIngot;
    public static ItemStack castNugget;
    // public static ItemStack castGem;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        TinkersRebornToolsEventsHandler tre = new TinkersRebornToolsEventsHandler();
        MinecraftForge.EVENT_BUS.register(tre);
        FMLCommonHandler.instance()
            .bus()
            .register(tre);

        MinecraftForge.EVENT_BUS.register(new TinkersRebornProjectileRenderEvents());

        toolStation = new ToolStationBlock();
        GameRegistry.registerBlock(toolStation, ToolStationItemBlock.class, toolStation.getUnlocalizedName());
        GameRegistry.registerTileEntity(ToolStationLogic.class, toolStation.getUnlocalizedName());

        toolForge = new ToolForgeBlock();
        GameRegistry.registerBlock(toolForge, ToolForgeItemBlock.class, toolForge.getUnlocalizedName());
        GameRegistry.registerTileEntity(ToolForgeLogic.class, toolForge.getUnlocalizedName());

        partBuilder = new PartBuilderBlock();
        GameRegistry.registerBlock(partBuilder, PartBuilderItemBlock.class, partBuilder.getUnlocalizedName());
        GameRegistry.registerTileEntity(PartBuilderLogic.class, partBuilder.getUnlocalizedName());

        castChest = new CastChestBlock();
        GameRegistry.registerBlock(castChest, CastChestItemBlock.class, castChest.getUnlocalizedName());
        GameRegistry.registerTileEntity(CastChestLogic.class, castChest.getUnlocalizedName());

        partChest = new PartChestBlock();
        GameRegistry.registerBlock(partChest, PartChestItemBlock.class, partChest.getUnlocalizedName());
        GameRegistry.registerTileEntity(PartChestLogic.class, partChest.getUnlocalizedName());

        craftingStation = new CraftingStationBlock();
        GameRegistry.registerBlock(craftingStation, craftingStation.getUnlocalizedName());
        GameRegistry.registerTileEntity(CraftingStationLogic.class, craftingStation.getUnlocalizedName());

        patternAndCast = new Pattern();

        arrowhead = new TinkersRebornToolPart("arrowhead", "ArrowHead", VALUE_Ingot * 2);
        arrowShaft = new TinkersRebornToolPart("arrow_shaft", "ArrowShaft", VALUE_Ingot * 2, MaterialStatusType.SHAFT);
        axeHead = new TinkersRebornToolPart("axe_head", "AxeHead", VALUE_Ingot * 2);
        battlesignHead = new TinkersRebornToolPart("battlesign_head", "SignHead", VALUE_Ingot * 3);
        binding = new TinkersRebornToolPart("binding", "Binding", VALUE_Ingot);
        bowLimb = new TinkersRebornToolPart("bow_limb", "BowLimb", VALUE_Ingot * 3, MaterialStatusType.BOW);
        chiselHead = new TinkersRebornToolPart("chisel_head", "ChiselHead", VALUE_Ingot * 2);
        crossbar = new TinkersRebornToolPart("crossbar", "Crossbar", VALUE_Ingot);
        crossbowBody = new TinkersRebornToolPart("crossbow_body", "CrossbowBody", VALUE_Ingot * 4);
        crossbowLimb = new TinkersRebornToolPart(
            "crossbow_limb",
            "CrossbowLimb",
            VALUE_Ingot * 2,
            MaterialStatusType.BOW);
        excavatorHead = new TinkersRebornToolPart("excavator_head", "ExcavatorHead", VALUE_Ingot * 8);
        frypanHead = new TinkersRebornToolPart("frypan_head", "FrypanHead", VALUE_Ingot * 4);
        fullGuard = new TinkersRebornToolPart("full_guard", "FullGuard", VALUE_Ingot);
        hammerHead = new TinkersRebornToolPart("hammer_head", "HammerHead", VALUE_Ingot * 8);
        knifeBlade = new TinkersRebornToolPart("knife_blade", "KnifeBlade", VALUE_Ingot);
        largeplate = new TinkersRebornToolPart("largeplate", "LargePlate", VALUE_Ingot * 8);
        largeGuard = new TinkersRebornToolPart("large_guard", "LargeGuard", VALUE_Ingot);
        largeSwordBlade = new TinkersRebornToolPart("large_sword_blade", "LargeSwordBlade", VALUE_Ingot * 8);
        lumberaxeHead = new TinkersRebornToolPart("lumberaxe_head", "LumberAxeHead", VALUE_Ingot * 8);
        mediumGuard = new TinkersRebornToolPart("medium_guard", "MediumGuard", VALUE_Ingot);
        pickaxeHead = new TinkersRebornToolPart("pickaxe_head", "PickaxeHead", VALUE_Ingot * 2);
        rod = new TinkersRebornToolPart("rod", "ToolRod", VALUE_Ingot);
        scytheHead = new TinkersRebornToolPart("scythe_head", "ScytheHead", VALUE_Ingot * 8);
        shard = new TinkersRebornToolPart("shard", "ToolShard", VALUE_Shard);
        shovelHead = new TinkersRebornToolPart("shovel_head", "ShovelHead", VALUE_Ingot * 2);
        shuriken = new TinkersRebornToolPart("shuriken", "Shuriken", VALUE_Ingot * 2);
        swordBlade = new TinkersRebornToolPart("sword_blade", "SwordBlade", VALUE_Ingot * 2);
        toughbind = new TinkersRebornToolPart("toughbind", "ToughBinding", VALUE_Ingot * 3);
        toughrod = new TinkersRebornToolPart("toughrod", "ToughRod", VALUE_Ingot * 3);
        kamaHead = new TinkersRebornToolPart("kama_head", "KamaHead", VALUE_Ingot * 2);
        sharpeningKit = new SharpeningKit("sharpening_kit", "SharpeningKit");
        bowString = new BowString();
        fletching = new Fletching();
        boltCore = new BoltCore();

        patternAndCast.addNewPatterntype(arrowhead);
        patternAndCast.addNewPatterntype(arrowShaft);
        patternAndCast.addNewPatterntype(axeHead);
        patternAndCast.addNewPatterntype(battlesignHead);
        patternAndCast.addNewPatterntype(binding);
        patternAndCast.addNewPatterntype(bowLimb);
        patternAndCast.addNewPatterntype(chiselHead);
        patternAndCast.addNewPatterntype(crossbar);
        patternAndCast.addNewPatterntype(crossbowBody);
        patternAndCast.addNewPatterntype(crossbowLimb);
        patternAndCast.addNewPatterntype(excavatorHead);
        patternAndCast.addNewPatterntype(frypanHead);
        patternAndCast.addNewPatterntype(fullGuard);
        patternAndCast.addNewPatterntype(hammerHead);
        patternAndCast.addNewPatterntype(knifeBlade);
        patternAndCast.addNewPatterntype(largeplate);
        patternAndCast.addNewPatterntype(largeGuard);
        patternAndCast.addNewPatterntype(largeSwordBlade);
        patternAndCast.addNewPatterntype(lumberaxeHead);
        patternAndCast.addNewPatterntype(mediumGuard);
        patternAndCast.addNewPatterntype(pickaxeHead);
        patternAndCast.addNewPatterntype(rod);
        patternAndCast.addNewPatterntype(scytheHead);
        // patternAndCast.addNewPatterntype(shard);
        patternAndCast.addNewPatterntype(shovelHead);
        patternAndCast.addNewPatterntype(shuriken);
        patternAndCast.addNewPatterntype(swordBlade);
        patternAndCast.addNewPatterntype(toughbind);
        patternAndCast.addNewPatterntype(toughrod);
        patternAndCast.addNewPatterntype(sharpeningKit);
        patternAndCast.addNewPatterntype(bowString);
        patternAndCast.addNewPatterntype(fletching);
        patternAndCast.addNewPatterntype(kamaHead);

        GameRegistry.registerItem(arrowhead, arrowhead.getUnlocalizedName());
        GameRegistry.registerItem(arrowShaft, arrowShaft.getUnlocalizedName());
        GameRegistry.registerItem(axeHead, axeHead.getUnlocalizedName());
        GameRegistry.registerItem(battlesignHead, battlesignHead.getUnlocalizedName());
        GameRegistry.registerItem(binding, binding.getUnlocalizedName());
        GameRegistry.registerItem(bowLimb, bowLimb.getUnlocalizedName());
        GameRegistry.registerItem(chiselHead, chiselHead.getUnlocalizedName());
        GameRegistry.registerItem(crossbar, crossbar.getUnlocalizedName());
        GameRegistry.registerItem(crossbowBody, crossbowBody.getUnlocalizedName());
        GameRegistry.registerItem(crossbowLimb, crossbowLimb.getUnlocalizedName());
        GameRegistry.registerItem(excavatorHead, excavatorHead.getUnlocalizedName());
        GameRegistry.registerItem(frypanHead, frypanHead.getUnlocalizedName());
        GameRegistry.registerItem(fullGuard, fullGuard.getUnlocalizedName());
        GameRegistry.registerItem(hammerHead, hammerHead.getUnlocalizedName());
        GameRegistry.registerItem(knifeBlade, knifeBlade.getUnlocalizedName());
        GameRegistry.registerItem(largeplate, largeplate.getUnlocalizedName());
        GameRegistry.registerItem(largeGuard, largeGuard.getUnlocalizedName());
        GameRegistry.registerItem(largeSwordBlade, largeSwordBlade.getUnlocalizedName());
        GameRegistry.registerItem(lumberaxeHead, lumberaxeHead.getUnlocalizedName());
        GameRegistry.registerItem(mediumGuard, mediumGuard.getUnlocalizedName());
        GameRegistry.registerItem(pickaxeHead, pickaxeHead.getUnlocalizedName());
        GameRegistry.registerItem(rod, rod.getUnlocalizedName());
        GameRegistry.registerItem(scytheHead, scytheHead.getUnlocalizedName());
        GameRegistry.registerItem(shard, shard.getUnlocalizedName());
        GameRegistry.registerItem(shovelHead, shovelHead.getUnlocalizedName());
        GameRegistry.registerItem(shuriken, shuriken.getUnlocalizedName());
        GameRegistry.registerItem(swordBlade, swordBlade.getUnlocalizedName());
        GameRegistry.registerItem(toughbind, toughbind.getUnlocalizedName());
        GameRegistry.registerItem(toughrod, toughrod.getUnlocalizedName());
        GameRegistry.registerItem(bowString, bowString.getUnlocalizedName());
        GameRegistry.registerItem(fletching, fletching.getUnlocalizedName());
        GameRegistry.registerItem(boltCore, boltCore.getUnlocalizedName());
        GameRegistry.registerItem(sharpeningKit, sharpeningKit.getUnlocalizedName());
        GameRegistry.registerItem(kamaHead, kamaHead.getUnlocalizedName());

        pickaxe = new Pickaxe();
        GameRegistry.registerItem(pickaxe, pickaxe.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(pickaxe);
        TinkersRebornRegistry.registerToolCrafting(pickaxe);

        shovel = new Shovel();
        GameRegistry.registerItem(shovel, shovel.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(shovel);
        TinkersRebornRegistry.registerToolCrafting(shovel);

        hatchet = new Hatchet();
        GameRegistry.registerItem(hatchet, hatchet.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(hatchet);
        TinkersRebornRegistry.registerToolCrafting(hatchet);

        kama = new Kama();
        GameRegistry.registerItem(kama, kama.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(kama);
        TinkersRebornRegistry.registerToolCrafting(kama);

        mattock = new Mattock();
        GameRegistry.registerItem(mattock, mattock.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(mattock);
        TinkersRebornRegistry.registerToolCrafting(mattock);

        broadSword = new BroadSword();
        GameRegistry.registerItem(broadSword, broadSword.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(broadSword);
        TinkersRebornRegistry.registerToolCrafting(broadSword);

        longSword = new LongSword();
        GameRegistry.registerItem(longSword, longSword.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(longSword);
        TinkersRebornRegistry.registerToolCrafting(longSword);

        rapier = new Rapier();
        GameRegistry.registerItem(rapier, rapier.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(rapier);
        TinkersRebornRegistry.registerToolCrafting(rapier);

        hammer = new Hammer();
        GameRegistry.registerItem(hammer, hammer.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(hammer);
        TinkersRebornRegistry.registerToolForgeCrafting(hammer);

        excavator = new Excavator();
        GameRegistry.registerItem(excavator, excavator.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(excavator);
        TinkersRebornRegistry.registerToolForgeCrafting(excavator);

        lumberAxe = new LumberAxe();
        GameRegistry.registerItem(lumberAxe, lumberAxe.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(lumberAxe);
        TinkersRebornRegistry.registerToolForgeCrafting(lumberAxe);

        scythe = new Scythe();
        GameRegistry.registerItem(scythe, scythe.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(scythe);
        TinkersRebornRegistry.registerToolForgeCrafting(scythe);

        cleaver = new Cleaver();
        GameRegistry.registerItem(cleaver, cleaver.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(cleaver);
        TinkersRebornRegistry.registerToolForgeCrafting(cleaver);

        vineHammer = new Vinehammer();
        GameRegistry.registerItem(vineHammer, vineHammer.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(vineHammer);
        TinkersRebornRegistry.registerToolForgeCrafting(vineHammer);

        arrow = new Arrow();
        GameRegistry.registerItem(arrow, arrow.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(arrow);
        TinkersRebornRegistry.registerToolCrafting(arrow);

        bolt = new Bolt();
        GameRegistry.registerItem(bolt, bolt.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(bolt);
        TinkersRebornRegistry.registerToolForgeCrafting(bolt);

        shortBow = new ShortBow();
        GameRegistry.registerItem(shortBow, shortBow.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(shortBow);
        TinkersRebornRegistry.registerToolCrafting(shortBow);

        longBow = new LongBow();
        GameRegistry.registerItem(longBow, longBow.getUnlocalizedName());
        TinkersRebornRegistry.registerTool(longBow);
        TinkersRebornRegistry.registerToolForgeCrafting(longBow);

        mossball = new MaterialItem("Mossball", "mossball");
        slimeCrystal = new MaterialItem("SlimeCrystal", "slimecrystal");
        lavaCrystal = new MaterialItem("LavaCrystal", "lavacrystal");
        silkyCloth = new MaterialItem("SilkyCloth", "silkycloth");
        silkyJewel = new MaterialItem("SilkyJewel", "silkyjewel");
        necroticBone = new MaterialItem("NecroticBone", "necroticbone");
        blueSlimeCrystal = new MaterialItem("BlueSlimeCrystal", "blueslimecrystal");
        reinforcement = new MaterialItem("Reinforcement", "reinforcement");

        paperStack = new MaterialItem("PaperStack", "paperstack");
        searedBrick = new MaterialItem("SearedBrick", "searedbrick");
        cobaltIngot = new MaterialItem("CobaltIngot", "cobaltingot");
        arditeIngot = new MaterialItem("ArditeIngot", "arditeingot");
        manyullynIngot = new MaterialItem("ManyullynIngot", "manyullyningot");
        copperIngot = new MaterialItem("CopperIngot", "copperingot");
        tinIngot = new MaterialItem("TinIngot", "tiningot");
        aluminumIngot = new MaterialItem("AluminumIngot", "aluminumingot");
        bronzeIngot = new MaterialItem("BronzeIngot", "bronzeingot");
        aluBrassIngot = new MaterialItem("AluBrassIngot", "alubrassingot");
        alumiteIngot = new MaterialItem("AlumiteIngot", "alumiteingot");
        steelIngot = new MaterialItem("SteelIngot", "steelingot");
        obsidianIngot = new MaterialItem("ObsidianIngot", "obsidianingot");
        pigIronIngot = new MaterialItem("PigIronIngot", "pigironingot");

        ironNugget = new MaterialItem("IronNugget", "nugget_iron");
        copperNugget = new MaterialItem("CopperNugget", "nugget_copper");
        tinNugget = new MaterialItem("TinNugget", "nugget_tin");
        aluminumNugget = new MaterialItem("AluminumNugget", "nugget_aluminum");
        aluBrassNugget = new MaterialItem("AluBrassNugget", "nugget_alubrass");
        obsidianNugget = new MaterialItem("ObsidianNugget", "nugget_obsidian");
        arditeNugget = new MaterialItem("ArditeNugget", "nugget_ardite");
        cobaltNugget = new MaterialItem("CobaltNugget", "nugget_cobalt");
        manyullynNugget = new MaterialItem("ManyullynNugget", "nugget_manyullyn");
        bronzeNugget = new MaterialItem("BronzeNugget", "nugget_bronze");
        alumiteNugget = new MaterialItem("AlumiteNugget", "nugget_alumite");
        steelNugget = new MaterialItem("SteelNugget", "nugget_steel");
        pigIronNugget = new MaterialItem("PigIronNugget", "nugget_pigiron");

        glueBall = new MaterialItem("GlueBall", "glueball");
        rawAluminum = new MaterialItem("RawAluminum", "aluminumraw");
        arditeDust = new MaterialItem("ArditeDust", "ardite_dust");
        cobaltDust = new MaterialItem("CobaltDust", "cobalt_dust");
        aluminumDust = new MaterialItem("AluminumDust", "aluminum_dust");
        manyullynDust = new MaterialItem("ManyullynDust", "manyullyn_dust");
        aluBrassDust = new MaterialItem("AluBrassDust", "alubrass_dust");

        GameRegistry.registerItem(mossball, mossball.getUnlocalizedName());
        GameRegistry.registerItem(slimeCrystal, slimeCrystal.getUnlocalizedName());
        GameRegistry.registerItem(blueSlimeCrystal, blueSlimeCrystal.getUnlocalizedName());
        GameRegistry.registerItem(lavaCrystal, lavaCrystal.getUnlocalizedName());
        GameRegistry.registerItem(silkyCloth, silkyCloth.getUnlocalizedName());
        GameRegistry.registerItem(silkyJewel, silkyJewel.getUnlocalizedName());
        GameRegistry.registerItem(necroticBone, necroticBone.getUnlocalizedName());
        GameRegistry.registerItem(reinforcement, reinforcement.getUnlocalizedName());
        GameRegistry.registerItem(paperStack, paperStack.getUnlocalizedName());
        GameRegistry.registerItem(searedBrick, searedBrick.getUnlocalizedName());
        GameRegistry.registerItem(cobaltIngot, cobaltIngot.getUnlocalizedName());
        GameRegistry.registerItem(arditeIngot, arditeIngot.getUnlocalizedName());
        GameRegistry.registerItem(manyullynIngot, manyullynIngot.getUnlocalizedName());
        GameRegistry.registerItem(copperIngot, copperIngot.getUnlocalizedName());
        GameRegistry.registerItem(tinIngot, tinIngot.getUnlocalizedName());
        GameRegistry.registerItem(aluminumIngot, aluminumIngot.getUnlocalizedName());
        GameRegistry.registerItem(bronzeIngot, bronzeIngot.getUnlocalizedName());
        GameRegistry.registerItem(aluBrassIngot, aluBrassIngot.getUnlocalizedName());
        GameRegistry.registerItem(alumiteIngot, alumiteIngot.getUnlocalizedName());
        GameRegistry.registerItem(steelIngot, steelIngot.getUnlocalizedName());
        GameRegistry.registerItem(obsidianIngot, obsidianIngot.getUnlocalizedName());
        GameRegistry.registerItem(pigIronIngot, pigIronIngot.getUnlocalizedName());
        GameRegistry.registerItem(ironNugget, ironNugget.getUnlocalizedName());
        GameRegistry.registerItem(copperNugget, copperNugget.getUnlocalizedName());
        GameRegistry.registerItem(tinNugget, tinNugget.getUnlocalizedName());
        GameRegistry.registerItem(aluminumNugget, aluminumNugget.getUnlocalizedName());
        GameRegistry.registerItem(aluBrassNugget, aluBrassNugget.getUnlocalizedName());
        GameRegistry.registerItem(obsidianNugget, obsidianNugget.getUnlocalizedName());
        GameRegistry.registerItem(arditeNugget, arditeNugget.getUnlocalizedName());
        GameRegistry.registerItem(cobaltNugget, cobaltNugget.getUnlocalizedName());
        GameRegistry.registerItem(manyullynNugget, manyullynNugget.getUnlocalizedName());
        GameRegistry.registerItem(bronzeNugget, bronzeNugget.getUnlocalizedName());
        GameRegistry.registerItem(alumiteNugget, alumiteNugget.getUnlocalizedName());
        GameRegistry.registerItem(steelNugget, steelNugget.getUnlocalizedName());
        GameRegistry.registerItem(pigIronNugget, pigIronNugget.getUnlocalizedName());
        GameRegistry.registerItem(glueBall, glueBall.getUnlocalizedName());
        GameRegistry.registerItem(rawAluminum, rawAluminum.getUnlocalizedName());
        GameRegistry.registerItem(arditeDust, arditeDust.getUnlocalizedName());
        GameRegistry.registerItem(cobaltDust, cobaltDust.getUnlocalizedName());
        GameRegistry.registerItem(aluminumDust, aluminumDust.getUnlocalizedName());
        GameRegistry.registerItem(manyullynDust, manyullynDust.getUnlocalizedName());
        GameRegistry.registerItem(aluBrassDust, aluBrassDust.getUnlocalizedName());
        GameRegistry.registerItem(patternAndCast, patternAndCast.getUnlocalizedName());

        this.oreDictRegistry();
        MiningLevelHelper.init();

        TinkersRebornModifiers.INSTANCE.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        this.registerMaterials();

        TinkersRebornModifiers.INSTANCE.init(e);

        castShard = Pattern.newStackWithToolPart(shard);
        castIngot = Pattern.newStackWithIdentifier(Pattern.CAST_INGOT);
        castNugget = Pattern.newStackWithIdentifier(Pattern.CAST_NUGGET);

        TinkersRebornRegistry.getMaterialIntegrations()
            .forEach(MaterialIntegration::preInit);

        TinkersRebornRegistry.addFluidForCast();

        proxy.initialize();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        TinkersRebornModifiers.INSTANCE.postInit(e);

        if (shard != null) {
            for (TinkersRebornMaterial material : TinkersRebornRegistry.getAllMaterialList()) {
                ItemStack shardStack = shard.getNewPartWithMaterial(material.identifier);
                material.addRecipeMatch(new RecipeMatch.ItemCombination(VALUE_Shard, shardStack));
                material.setShard(shardStack);
            }
        }
        TinkersRebornRegistry.getMaterialIntegrations()
            .forEach(MaterialIntegration::integrate);
    }

    /**
     * register all base material in here
     */
    private void registerMaterials() {
        woodMaterial = new TinkersRebornMaterial("Wood", 0x755821).setCraftable(true);
        woodMaterial.addItem("stickWood", 1, VALUE_Shard);
        woodMaterial.addItem("plankWood", 1, VALUE_Ingot);
        woodMaterial.addItem("logWood", 1, VALUE_Ingot * 4);
        woodMaterial.addTrait(ecological);

        stoneMaterial = new TinkersRebornMaterial("Stone", 0x7F7F7F).setCraftable(true);
        stoneMaterial.addItem("cobblestone", 1, VALUE_Ingot);
        stoneMaterial.addItem("stone", 1, VALUE_Ingot);
        stoneMaterial.setRepresentativeItem(Blocks.cobblestone);
        stoneMaterial.addTrait(cheapskate, MaterialStatusType.HEAD);
        stoneMaterial.addTrait(cheap);

        flintMaterial = new TinkersRebornMaterial("Flint", 0x484848).setCraftable(true);
        flintMaterial.addItem(Items.flint, 1, VALUE_Ingot);
        flintMaterial.setRepresentativeItem(Items.flint);
        flintMaterial.addTrait(crude2, MaterialStatusType.HEAD);
        flintMaterial.addTrait(crude);

        cactusMaterial = new TinkersRebornMaterial("Cactus", 0x12690B).setCraftable(true);
        cactusMaterial.addItem("blockCactus", 1, VALUE_Ingot);
        cactusMaterial.setRepresentativeItem(Blocks.cactus);
        cactusMaterial.addTrait(prickly, MaterialStatusType.HEAD);
        cactusMaterial.addTrait(spiky);

        obsidianMaterial = new TinkersRebornMaterial("Obsidian", 0xAA7FF5).setCraftable(true);
        obsidianFluid = TinkersRebornFluid
            .createMolten(obsidianMaterial.identifier, obsidianMaterial.materialTextColor, obsidianMaterial.identifier);
        obsidianMaterial.addItem("obsidian", 1, VALUE_Ingot);
        obsidianMaterial.setRepresentativeItem(Blocks.obsidian);
        obsidianMaterial.addTrait(duritos);

        netherrackMaterial = new TinkersRebornMaterial("Netherrack", 0x833238).setCraftable(true);
        netherrackMaterial.addItem("netherrack", 1, VALUE_Ingot);
        netherrackMaterial.setRepresentativeItem(Blocks.netherrack);
        netherrackMaterial.addTrait(aridiculous, MaterialStatusType.HEAD);
        netherrackMaterial.addTrait(hellish, MaterialStatusType.HEAD);
        netherrackMaterial.addTrait(hellish);

        endStoneMaterial = new TinkersRebornMaterial("Endstone", 0xE0D890).setCraftable(true);
        endStoneMaterial.addItem("endstone", 1, VALUE_Ingot);
        endStoneMaterial.setRepresentativeItem(Blocks.end_stone);
        endStoneMaterial.addTrait(alien, MaterialStatusType.HEAD);
        endStoneMaterial.addTrait(enderference);
        endStoneMaterial.addTrait(enderference, MaterialStatusType.PROJECTILE);

        boneMaterial = new TinkersRebornMaterial("Bone", 0xEDEBCA).setCraftable(true);
        boneMaterial.addItem("bone", 1, VALUE_Ingot);
        // see ItemDye
        boneMaterial.addItem(new ItemStack(Items.dye, 1, 15), 1, VALUE_Fragment); // bonemeal
        boneMaterial.setRepresentativeItem(Items.bone);
        boneMaterial.addTrait(splintering, MaterialStatusType.HEAD);
        boneMaterial.addTrait(splitting, MaterialStatusType.SHAFT);
        boneMaterial.addTrait(fractured);

        paperMaterial = new TinkersRebornMaterial("Paper", 0xFFFFFF).setCraftable(true);
        paperMaterial.addItem("paper", 1, VALUE_Fragment);
        paperMaterial.setRepresentativeItem(Items.paper);
        paperMaterial.addTrait(writable2, MaterialStatusType.HEAD);
        paperMaterial.addTrait(writable);

        slimeMaterial = new TinkersRebornMaterial("Slime", 0x6EB065).setCraftable(true);
        slimeMaterial.addItem("slimecrystalGreen", 1, VALUE_Ingot);
        slimeMaterial.addTrait(slimeyGreen);

        blueSlimeMaterial = new TinkersRebornMaterial("BlueSlime", 0x66AEB0).setCraftable(true);
        blueSlimeMaterial.addItem("slimecrystalBlue", 1, VALUE_Ingot);
        blueSlimeMaterial.addTrait(slimeyBlue);

        ironMaterial = new TinkersRebornMaterial("Iron", 0xDADADA);
        ironFluid = TinkersRebornFluid
            .createMolten(ironMaterial.identifier, ironMaterial.materialTextColor, ironMaterial.identifier, 769);
        ironMaterial.addCommonItems("Iron");
        ironMaterial.setRepresentativeItem(Items.iron_ingot);
        ironMaterial.addTrait(magnetic2, MaterialStatusType.HEAD);
        ironMaterial.addTrait(magnetic);

        pigIronMaterial = new TinkersRebornMaterial("PigIron", 0xF0A8A4);
        pigIronFluid = TinkersRebornFluid.createMolten(
            pigIronMaterial.identifier,
            pigIronMaterial.materialTextColor,
            pigIronMaterial.identifier,
            600);
        pigIronMaterial.addCommonItems("Pigiron");
        pigIronMaterial.addTrait(baconlicious, MaterialStatusType.HEAD);
        pigIronMaterial.addTrait(tasty, MaterialStatusType.HEAD);
        pigIronMaterial.addTrait(tasty);

        cobaltMaterial = new TinkersRebornMaterial("Cobalt", 0x2376DD);
        cobaltFluid = TinkersRebornFluid
            .createMolten(cobaltMaterial.identifier, cobaltMaterial.materialTextColor, cobaltMaterial.identifier, 950);
        cobaltMaterial.addCommonItems("Cobalt");
        cobaltMaterial.addTrait(momentum, MaterialStatusType.HEAD);
        cobaltMaterial.addTrait(lightweight);

        arditeMaterial = new TinkersRebornMaterial("Ardite", 0xF18D2A);
        arditeFluid = TinkersRebornFluid
            .createMolten(arditeMaterial.identifier, arditeMaterial.materialTextColor, arditeMaterial.identifier, 860);
        arditeMaterial.addCommonItems("Ardite");
        arditeMaterial.addTrait(stonebound, MaterialStatusType.HEAD);
        arditeMaterial.addTrait(petramor);

        manyullynMaterial = new TinkersRebornMaterial("Manyullyn", 0x7338A5);
        manyullynFluid = TinkersRebornFluid.createMolten(
            manyullynMaterial.identifier,
            manyullynMaterial.materialTextColor,
            manyullynMaterial.identifier,
            1000);
        manyullynMaterial.addCommonItems("Manyullyn");
        manyullynMaterial.addTrait(insatiable, MaterialStatusType.HEAD);
        manyullynMaterial.addTrait(coldblooded);

        copperMaterial = new TinkersRebornMaterial("Copper", 0xCC6410);
        copperFluid = TinkersRebornFluid
            .createMolten(copperMaterial.identifier, copperMaterial.materialTextColor, copperMaterial.identifier, 542);
        copperMaterial.addCommonItems("Copper");
        copperMaterial.addTrait(established);

        bronzeMaterial = new TinkersRebornMaterial("Bronze", 0xCA9956);
        bronzeFluid = TinkersRebornFluid
            .createMolten(bronzeMaterial.identifier, bronzeMaterial.materialTextColor, bronzeMaterial.identifier, 475);
        bronzeMaterial.addCommonItems("Bronze");
        bronzeMaterial.addTrait(dense);

        alumiteMaterial = new TinkersRebornMaterial("Alumite", 0xFFA7E9);
        alumiteFluid = TinkersRebornFluid.createMolten(
            alumiteMaterial.identifier,
            alumiteMaterial.materialTextColor,
            alumiteMaterial.identifier,
            900);
        alumiteMaterial.addCommonItems("Alumite");
        alumiteMaterial.addTrait(duritos);

        steelMaterial = new TinkersRebornMaterial("Steel", 0xA0A0A0);
        steelFluid = TinkersRebornFluid
            .createMolten(steelMaterial.identifier, steelMaterial.materialTextColor, steelMaterial.identifier, 681);
        steelMaterial.addCommonItems("Steel");
        steelMaterial.addTrait(sharp, MaterialStatusType.HEAD);
        steelMaterial.addTrait(stiff);

        leadMaterial = new TinkersRebornMaterial("Lead", 0x4D4968);
        leadFluid = TinkersRebornFluid
            .createMolten(leadMaterial.identifier, leadMaterial.materialTextColor, leadMaterial.identifier, 400);
        leadMaterial.addCommonItems("Lead");
        leadMaterial.addTrait(poisonous);
        leadMaterial.addTrait(heavy);

        silverMaterial = new TinkersRebornMaterial("Silver", 0xD1ECF6);
        silverFluid = TinkersRebornFluid
            .createMolten(silverMaterial.identifier, silverMaterial.materialTextColor, silverMaterial.identifier, 480);
        silverMaterial.addTrait(holy);

        bloodBoneMaterial = new TinkersRebornMaterial("BloodBone", 0xC70000).setCraftable(true);
        bloodBoneMaterial.addItem("boneBloodied", 1, VALUE_Ingot);
        // bloodBoneMaterial.setRepresentativeItem(TinkerCommons.matBloodyBone);
        bloodBoneMaterial.addTrait(raging2, MaterialStatusType.HEAD);
        bloodBoneMaterial.addTrait(splintering, MaterialStatusType.HEAD);
        bloodBoneMaterial.addTrait(raging);
        bloodBoneMaterial.addTrait(fractured);

        stringMaterial = new TinkersRebornMaterial("String", 0xEEEEEE).setCraftable(true);
        stringMaterial.addItem(Items.string, 1, VALUE_Ingot);
        stringMaterial.setRepresentativeItem(Items.string);

        vineMaterial = new TinkersRebornMaterial("Vine", 0x40A10F).setCraftable(true);
        vineMaterial.addItem("vine", 1, VALUE_Ingot);
        vineMaterial.setRepresentativeItem(Blocks.vine);

        slimeVineMaterial = new TinkersRebornMaterial("SlimeVine", 0x74C8C7).setCraftable(true);

        blazeMaterial = new TinkersRebornMaterial("Blaze", 0xFFC100).setCraftable(true);
        blazeMaterial.addItem(Items.blaze_rod, 1, VALUE_Ingot);
        blazeMaterial.setRepresentativeItem(Items.blaze_rod);
        blazeMaterial.addTrait(hovering);

        reedMaterial = new TinkersRebornMaterial("Reed", 0xAADB74).setCraftable(true);
        reedMaterial.addItem(Items.reeds, 1, VALUE_Ingot);
        reedMaterial.setRepresentativeItem(Items.reeds);
        reedMaterial.addTrait(breakable);

        iceMaterial = new TinkersRebornMaterial("Ice", 0x97D7E0).setCraftable(true);
        iceMaterial.addItem(Blocks.packed_ice, VALUE_Ingot);
        iceMaterial.setRepresentativeItem(Blocks.packed_ice);
        iceMaterial.addTrait(freezing);

        featherMaterial = new TinkersRebornMaterial("Feather", 0xEEEEEE).setCraftable(true);
        featherMaterial.addItem(Items.feather, 1, VALUE_Ingot);
        featherMaterial.setRepresentativeItem(Items.feather);

        leafMaterial = new TinkersRebornMaterial("Leaf", 0x1D730C).setCraftable(true);
        leafMaterial.addItem("treeLeaves", 1, VALUE_Shard);
        leafMaterial.setRepresentativeItem(Blocks.leaves);

        slimeleafMaterial = new TinkersRebornMaterial("SlimeLeaf", 0x74C8C7).setCraftable(true);
        slimeleafMaterial.addItem(new ItemStack(TinkersRebornGeneral.slimeLeaves), 1, VALUE_Shard);
        slimeleafMaterial.setRepresentativeItem(TinkersRebornGeneral.slimeLeaves);

        goldFluid = TinkersRebornFluid.createMolten("gold", 0xFFD700, "gold", 532);
        tinFluid = TinkersRebornFluid.createMolten("tin", 0xE6E6FA, "tin", 350);
        emeraldFluid = TinkersRebornFluid.createMolten("emerald", 0x17DD62, "emerald", 500);
        aluminumFluid = TinkersRebornFluid.createMolten("aluminum", 0xCCCCCC, "aluminum", 330);

        this.registerBaseMaterialsStats();
        this.registerBowMaterialsStats();

        this.registerMaterialIntegrations();
    }

    private void registerBaseMaterialsStats() {
        woodMaterial.addStats(
            new HeadMaterialStats(97, 1, 0, 3.5F),
            new HandleMaterialStats(1.0F, 0),
            new ExtraMaterialStats(0),
            new ShaftMaterialStats(1.0f, 0));
        stoneMaterial.addStats(
            new HeadMaterialStats(131, 1, 1, 4.0F),
            new HandleMaterialStats(0.5F, 0),
            new ExtraMaterialStats(0));
        ironMaterial.addStats(
            new HeadMaterialStats(250, 2, 2, 6.0F),
            new HandleMaterialStats(1.3F, 0),
            new ExtraMaterialStats(0));
        flintMaterial.addStats(
            new HeadMaterialStats(171, 1, 2, 5.25F),
            new HandleMaterialStats(0.7F, 0),
            new ExtraMaterialStats(0));
        cactusMaterial.addStats(
            new HeadMaterialStats(150, 1, 2, 5.0F),
            new HandleMaterialStats(1.0F, 0),
            new ExtraMaterialStats(0));
        boneMaterial.addStats(
            new HeadMaterialStats(200, 1, 1, 4.0F),
            new HandleMaterialStats(1.0F, 0),
            new ExtraMaterialStats(0),
            new ShaftMaterialStats(0.9f, 5));
        obsidianMaterial.addStats(
            new HeadMaterialStats(89, 3, 2, 7.0F),
            new HandleMaterialStats(0.8F, 0),
            new ExtraMaterialStats(0));
        netherrackMaterial.addStats(
            new HeadMaterialStats(131, 2, 1, 4.0F),
            new HandleMaterialStats(1.2F, 0),
            new ExtraMaterialStats(0));
        slimeMaterial.addStats(
            new HeadMaterialStats(500, 0, 0, 1.5F),
            new HandleMaterialStats(1.5F, 0),
            new ExtraMaterialStats(0));
        paperMaterial.addStats(
            new HeadMaterialStats(30, 0, 0, 2.0F),
            new HandleMaterialStats(0.3F, 0),
            new ExtraMaterialStats(0));
        cobaltMaterial.addStats(
            new HeadMaterialStats(800, 4, 3, 14.0F),
            new HandleMaterialStats(1.75F, 0),
            new ExtraMaterialStats(0));
        arditeMaterial.addStats(
            new HeadMaterialStats(500, 4, 3, 8.0F),
            new HandleMaterialStats(2.0F, 0),
            new ExtraMaterialStats(0));
        manyullynMaterial.addStats(
            new HeadMaterialStats(1200, 5, 4, 9.0F),
            new HandleMaterialStats(2.5F, 0),
            new ExtraMaterialStats(0));
        copperMaterial.addStats(
            new HeadMaterialStats(180, 1, 2, 5.0F),
            new HandleMaterialStats(1.15F, 0),
            new ExtraMaterialStats(0));
        bronzeMaterial.addStats(
            new HeadMaterialStats(550, 2, 2, 8.0F),
            new HandleMaterialStats(1.3F, 0),
            new ExtraMaterialStats(0));
        alumiteMaterial.addStats(
            new HeadMaterialStats(700, 4, 3, 8.0F),
            new HandleMaterialStats(1.3F, 0),
            new ExtraMaterialStats(0));
        steelMaterial.addStats(
            new HeadMaterialStats(750, 4, 4, 10.0F),
            new HandleMaterialStats(1.3F, 0),
            new ExtraMaterialStats(0));
        blueSlimeMaterial.addStats(
            new HeadMaterialStats(1200, 0, 0, 1.5F),
            new HandleMaterialStats(2.0F, 0),
            new ExtraMaterialStats(0));
        pigIronMaterial.addStats(
            new HeadMaterialStats(250, 3, 2, 6.0F),
            new HandleMaterialStats(1.3F, 0),
            new ExtraMaterialStats(0));
        endStoneMaterial.addStats(
            new HeadMaterialStats(420, 4, 3.23F, 3.23F),
            new HandleMaterialStats(0.85F, 0),
            new ExtraMaterialStats(42));

        leadMaterial.addStats(
            new HeadMaterialStats(434, 1, 3.5F, 5.25F),
            new HandleMaterialStats(0.70f, -50),
            new ExtraMaterialStats(100));
        silverMaterial.addStats(
            new HeadMaterialStats(250, 1, 5.00F, 5.00F),
            new HandleMaterialStats(0.95f, 50),
            new ExtraMaterialStats(150));

        bloodBoneMaterial.addStats(
            new HeadMaterialStats(200, 1, 2.5F, 5.09F),
            new HandleMaterialStats(1.10f, 50),
            new ExtraMaterialStats(65));

        stringMaterial.addStats(new StringMaterialStats(1.0F));
        vineMaterial.addStats(new StringMaterialStats(1.0F));
        slimeVineMaterial.addStats(new StringMaterialStats(1.0F));

        blazeMaterial.addStats(new ShaftMaterialStats(0.8f, 3));
        reedMaterial.addStats(new ShaftMaterialStats(1.5F, 20));
        iceMaterial.addStats(new ShaftMaterialStats(0.95F, 0));

        featherMaterial.addStats(new FletchingMaterialStats(1.0F, 1.0F));
        leafMaterial.addStats(new FletchingMaterialStats(0.5F, 1.5F));
        slimeleafMaterial.addStats(new FletchingMaterialStats(0.8F, 1.25F));
    }

    private void registerBowMaterialsStats() {
        woodMaterial.addStats(new BowMaterialStats(1f, 1f, 0));
    }

    private void registerMaterialIntegrations() {
        integrate(new MaterialIntegration(ironMaterial, ironFluid, "Iron"));
        integrate(new MaterialIntegration(cobaltMaterial, cobaltFluid, "Cobalt"));
        integrate(new MaterialIntegration(arditeMaterial, arditeFluid, "Ardite"));
        integrate(new MaterialIntegration(manyullynMaterial, manyullynFluid, "Manyullyn"));
        integrate(new MaterialIntegration(copperMaterial, copperFluid, "Copper"));
        integrate(new MaterialIntegration(bronzeMaterial, bronzeFluid, "Bronze"));
        integrate(new MaterialIntegration(alumiteMaterial, alumiteFluid, "Alumite"));
        integrate(new MaterialIntegration(steelMaterial, steelFluid, "Steel"));
        integrate(new MaterialIntegration(pigIronMaterial, pigIronFluid, "PigIron"));
        integrate(new MaterialIntegration(obsidianMaterial, obsidianFluid, "Obsidian"));

        integrate(new MaterialIntegration("ingotLead", leadMaterial, leadFluid, "Lead"));
        integrate(new MaterialIntegration("ingotSilver", silverMaterial, silverFluid, "Silver"));

        integrate(new MaterialIntegration(null, null, goldFluid, "Gold"));
        integrate(new MaterialIntegration(null, tinFluid, "Tin"));
        integrate(new MaterialIntegration(null, aluminumFluid, "Aluminum"));
        integrate(new MaterialIntegration(null, null, TinkersRebornGeneral.bloodFluid, null));
        integrate(new MaterialIntegration(null, null, emeraldFluid, "Emerald"));

        integrate(new MaterialIntegration(woodMaterial, null, null));
        integrate(new MaterialIntegration(stoneMaterial, null, null));
        integrate(new MaterialIntegration(flintMaterial, null, null));
        integrate(new MaterialIntegration(cactusMaterial, null, null));
        integrate(new MaterialIntegration(boneMaterial, null, null));
        integrate(new MaterialIntegration(netherrackMaterial, null, null));
        integrate(new MaterialIntegration(endStoneMaterial, null, null));
        integrate(new MaterialIntegration(paperMaterial, null, null));
        integrate(new MaterialIntegration(slimeMaterial, null, null));
        integrate(new MaterialIntegration(blueSlimeMaterial, null, null));

        integrate(new MaterialIntegration(stringMaterial, null, null));
        integrate(new MaterialIntegration(vineMaterial, null, null));
        integrate(new MaterialIntegration(slimeVineMaterial, null, null));

        integrate(new MaterialIntegration(blazeMaterial, null, null));
        integrate(new MaterialIntegration(reedMaterial, null, null));
        integrate(new MaterialIntegration(iceMaterial, null, null));

        integrate(new MaterialIntegration(featherMaterial, null, null));
        integrate(new MaterialIntegration(leafMaterial, null, null));
        integrate(new MaterialIntegration(slimeleafMaterial, null, null));

        integrate(new MaterialIntegration(bloodBoneMaterial, null, null));
    }

    private void oreDictRegistry() {
        TinkersRebornUtils.ensureOreIsRegistered("ingotIron", new ItemStack(Items.iron_ingot));
        TinkersRebornUtils.ensureOreIsRegistered("ingotGold", new ItemStack(Items.gold_ingot));
        TinkersRebornUtils.ensureOreIsRegistered("blockIron", new ItemStack(Blocks.iron_block));
        TinkersRebornUtils.ensureOreIsRegistered("blockGold", new ItemStack(Blocks.gold_block));
        TinkersRebornUtils.ensureOreIsRegistered("nuggetGold", new ItemStack(Items.gold_nugget));

        OreDictionary.registerOre("ingotCobalt", new ItemStack(cobaltIngot));
        OreDictionary.registerOre("ingotArdite", new ItemStack(arditeIngot));
        OreDictionary.registerOre("ingotManyullyn", new ItemStack(manyullynIngot));
        OreDictionary.registerOre("ingotCopper", new ItemStack(copperIngot));
        OreDictionary.registerOre("ingotTin", new ItemStack(tinIngot));
        OreDictionary.registerOre("ingotAluminum", new ItemStack(aluminumIngot));
        OreDictionary.registerOre("ingotAluminium", new ItemStack(aluminumIngot));
        OreDictionary.registerOre("ingotBronze", new ItemStack(bronzeIngot));
        OreDictionary.registerOre("ingotAluminiumBrass", new ItemStack(aluBrassIngot));
        OreDictionary.registerOre("ingotAlumite", new ItemStack(alumiteIngot));
        OreDictionary.registerOre("ingotSteel", new ItemStack(steelIngot));
        OreDictionary.registerOre("ingotObsidian", new ItemStack(obsidianIngot));
        OreDictionary.registerOre("ingotPigIron", new ItemStack(pigIronIngot));
        OreDictionary.registerOre("itemRawRubber", new ItemStack(glueBall));

        OreDictionary.registerOre("nuggetIron", new ItemStack(ironNugget));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(copperNugget));
        OreDictionary.registerOre("nuggetTin", new ItemStack(tinNugget));
        OreDictionary.registerOre("nuggetAluminum", new ItemStack(aluminumNugget));
        OreDictionary.registerOre("nuggetAluminium", new ItemStack(aluminumNugget));
        OreDictionary.registerOre("nuggetAluminumBrass", new ItemStack(aluBrassNugget));
        OreDictionary.registerOre("nuggetAluminiumBrass", new ItemStack(aluBrassNugget));
        OreDictionary.registerOre("nuggetObsidian", new ItemStack(obsidianNugget));
        OreDictionary.registerOre("nuggetCobalt", new ItemStack(cobaltNugget));
        OreDictionary.registerOre("nuggetArdite", new ItemStack(arditeNugget));
        OreDictionary.registerOre("nuggetManyullyn", new ItemStack(manyullynNugget));
        OreDictionary.registerOre("nuggetBronze", new ItemStack(bronzeNugget));
        OreDictionary.registerOre("nuggetAlumite", new ItemStack(alumiteNugget));
        OreDictionary.registerOre("nuggetSteel", new ItemStack(steelNugget));
        OreDictionary.registerOre("nuggetPigIron", new ItemStack(pigIronNugget));

        OreDictionary.registerOre("dustArdite", new ItemStack(arditeDust));
        OreDictionary.registerOre("dustCobalt", new ItemStack(cobaltDust));
        OreDictionary.registerOre("dustAluminium", new ItemStack(aluminumDust));
        OreDictionary.registerOre("dustAluminum", new ItemStack(aluminumDust));
        OreDictionary.registerOre("dustManyullyn", new ItemStack(manyullynDust));
        OreDictionary.registerOre("dustAluminiumBrass", new ItemStack(aluBrassDust));
        OreDictionary.registerOre("dustAluminumBrass", new ItemStack(aluBrassDust));

        OreDictionary.registerOre("slimeball", new ItemStack(glueBall));

        OreDictionary.registerOre("boneWithered", new ItemStack(necroticBone));
    }

}
