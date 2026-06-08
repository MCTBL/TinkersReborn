package mctbl.tinkersreborn.tools;

import static mctbl.tinkersreborn.library.MaterialID.Alumite;
import static mctbl.tinkersreborn.library.MaterialID.Ardite;
import static mctbl.tinkersreborn.library.MaterialID.BlueSlime;
import static mctbl.tinkersreborn.library.MaterialID.Bone;
import static mctbl.tinkersreborn.library.MaterialID.Bronze;
import static mctbl.tinkersreborn.library.MaterialID.Cactus;
import static mctbl.tinkersreborn.library.MaterialID.Cobalt;
import static mctbl.tinkersreborn.library.MaterialID.Copper;
import static mctbl.tinkersreborn.library.MaterialID.Flint;
import static mctbl.tinkersreborn.library.MaterialID.Iron;
import static mctbl.tinkersreborn.library.MaterialID.Manyullyn;
import static mctbl.tinkersreborn.library.MaterialID.Netherrack;
import static mctbl.tinkersreborn.library.MaterialID.Obsidian;
import static mctbl.tinkersreborn.library.MaterialID.Paper;
import static mctbl.tinkersreborn.library.MaterialID.PigIron;
import static mctbl.tinkersreborn.library.MaterialID.Slime;
import static mctbl.tinkersreborn.library.MaterialID.Steel;
import static mctbl.tinkersreborn.library.MaterialID.Stone;
import static mctbl.tinkersreborn.library.MaterialID.Wood;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mctbl.tinkersreborn.library.ITinkersRebornModule;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.utils.MiningLevelHelper;
import mctbl.tinkersreborn.smeltery.blocks.TinkersRebornFluid;
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
import mctbl.tinkersreborn.tools.items.BoltCore;
import mctbl.tinkersreborn.tools.items.BowString;
import mctbl.tinkersreborn.tools.items.Fletching;
import mctbl.tinkersreborn.tools.items.MaterialItem;
import mctbl.tinkersreborn.tools.items.Pattern;
import mctbl.tinkersreborn.tools.items.Pickaxe;
import mctbl.tinkersreborn.tools.items.TinkersRebornToolPart;
import mctbl.tinkersreborn.tools.materials.ExtraMaterialStats;
import mctbl.tinkersreborn.tools.materials.HandleMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
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

    public static TinkersRebornToolPart bowString;
    public static TinkersRebornToolPart fletching;
    public static TinkersRebornToolPart boltCore;

    // Tools
    public static ToolCore pickaxe;

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

    public static Item patternAndCast;
    public static Item creativeModifier; // TODO

    public static Fluid ironFluid;
    public static Fluid obsidianFluid;
    public static Fluid cobaltFluid;
    public static Fluid arditeFluid;
    public static Fluid manyullynFluid;
    public static Fluid copperFluid;
    public static Fluid bronzeFluid;
    public static Fluid alumiteFluid;
    public static Fluid steelFluid;
    public static Fluid pigIronFluid;

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

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        TinkersRebornEvents tre = new TinkersRebornEvents();
        MinecraftForge.EVENT_BUS.register(tre);
        FMLCommonHandler.instance()
            .bus()
            .register(tre);

        toolStation = new ToolStationBlock();
        GameRegistry
            .registerBlock(toolStation, TinkersRebornToolStationItemBlock.class, toolStation.getUnlocalizedName());
        GameRegistry.registerTileEntity(TinkersRebornToolStationLogic.class, toolStation.getUnlocalizedName());

        toolForge = new ToolForgeBlock();
        GameRegistry.registerBlock(toolForge, TinkersRebornToolForgeItemBlock.class, toolForge.getUnlocalizedName());
        GameRegistry.registerTileEntity(TinkersRebornToolForgeLogic.class, toolForge.getUnlocalizedName());

        partBuilder = new PartBuilderBlock();
        GameRegistry
            .registerBlock(partBuilder, TinkersRebornPartBuilderItemBlock.class, partBuilder.getUnlocalizedName());
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

        arrowhead = new TinkersRebornToolPart("arrowhead", "ArrowHead");
        arrowShaft = new TinkersRebornToolPart("arrow_shaft", "ArrowShaft", MaterialStatusType.SHAFT);
        axeHead = new TinkersRebornToolPart("axe_head", "AxeHead");
        battlesignHead = new TinkersRebornToolPart("battlesign_head", "SignHead");
        binding = new TinkersRebornToolPart("binding", "Binding");
        bowLimb = new TinkersRebornToolPart("bow_limb", "BowLimb", MaterialStatusType.BOW);
        chiselHead = new TinkersRebornToolPart("chisel_head", "ChiselHead");
        crossbar = new TinkersRebornToolPart("crossbar", "Crossbar");
        crossbowBody = new TinkersRebornToolPart("crossbow_body", "CrossbowBody");
        crossbowLimb = new TinkersRebornToolPart("crossbow_limb", "CrossbowLimb", MaterialStatusType.BOW);
        excavatorHead = new TinkersRebornToolPart("excavator_head", "ExcavatorHead");
        frypanHead = new TinkersRebornToolPart("frypan_head", "FrypanHead");
        fullGuard = new TinkersRebornToolPart("full_guard", "FullGuard");
        hammerHead = new TinkersRebornToolPart("hammer_head", "HammerHead");
        knifeBlade = new TinkersRebornToolPart("knife_blade", "KnifeBlade");
        largeplate = new TinkersRebornToolPart("largeplate", "LargePlate");
        largeGuard = new TinkersRebornToolPart("large_guard", "LargeGuard");
        largeSwordBlade = new TinkersRebornToolPart("large_sword_blade", "LargeSwordBlade");
        lumberaxeHead = new TinkersRebornToolPart("lumberaxe_head", "LumberAxeHead");
        mediumGuard = new TinkersRebornToolPart("medium_guard", "MediumGuard");
        pickaxeHead = new TinkersRebornToolPart("pickaxe_head", "PickaxeHead");
        rod = new TinkersRebornToolPart("rod", "ToolRod");
        scytheHead = new TinkersRebornToolPart("scythe_head", "ScytheHead");
        shard = new TinkersRebornToolPart("shard", "ToolShard");
        shovelHead = new TinkersRebornToolPart("shovel_head", "ShovelHead");
        shuriken = new TinkersRebornToolPart("shuriken", "Shuriken");
        swordBlade = new TinkersRebornToolPart("sword_blade", "SwordBlade");
        toughbind = new TinkersRebornToolPart("toughbind", "ToughBinding");
        toughrod = new TinkersRebornToolPart("toughrod", "ToughRod");
        bowString = new BowString();
        fletching = new Fletching();
        boltCore = new BoltCore();

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

        pickaxe = new Pickaxe();
        GameRegistry.registerItem(pickaxe, pickaxe.getUnlocalizedName());
        TinkersRebornRegistry.tools.add(pickaxe);

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

        patternAndCast = new Pattern();
        GameRegistry.registerItem(patternAndCast, patternAndCast.getUnlocalizedName());

        this.registerMaterials();
        this.oreDictRegistry();
        MiningLevelHelper.init();
    }

    @Override
    public void init(FMLInitializationEvent e) {

        proxy.initialize();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * register all base material in here
     */
    private void registerMaterials() {

        woodMaterial = new TinkersRebornMaterial(Wood, "Wood", 0x755821).setCraftable(true);
        stoneMaterial = new TinkersRebornMaterial(Stone, "Stone", 0x7F7F7F).setCraftable(true);
        ironMaterial = new TinkersRebornMaterial(Iron, "Iron", 0xDADADA);
        flintMaterial = new TinkersRebornMaterial(Flint, "Flint", 0x484848).setCraftable(true);
        cactusMaterial = new TinkersRebornMaterial(Cactus, "Cactus", 0x12690B).setCraftable(true);
        boneMaterial = new TinkersRebornMaterial(Bone, "Bone", 0xEDEBCA).setCraftable(true);
        obsidianMaterial = new TinkersRebornMaterial(Obsidian, "Obsidian", 0xAA7FF5).setCraftable(true);
        netherrackMaterial = new TinkersRebornMaterial(Netherrack, "Netherrack", 0x833238).setCraftable(true);
        slimeMaterial = new TinkersRebornMaterial(Slime, "Slime", 0x6EB065).setCraftable(true);
        paperMaterial = new TinkersRebornMaterial(Paper, "Paper", 0xFFFFFF).setCraftable(true);
        cobaltMaterial = new TinkersRebornMaterial(Cobalt, "Cobalt", 0x2376DD);
        arditeMaterial = new TinkersRebornMaterial(Ardite, "Ardite", 0xF18D2A);
        manyullynMaterial = new TinkersRebornMaterial(Manyullyn, "Manyullyn", 0x7338A5);
        copperMaterial = new TinkersRebornMaterial(Copper, "Copper", 0xCC6410);
        bronzeMaterial = new TinkersRebornMaterial(Bronze, "Bronze", 0xCA9956);
        alumiteMaterial = new TinkersRebornMaterial(Alumite, "Alumite", 0xFFA7E9);
        steelMaterial = new TinkersRebornMaterial(Steel, "Steel", 0xA0A0A0);
        blueSlimeMaterial = new TinkersRebornMaterial(BlueSlime, "BlueSlime", 0x66AEB0).setCraftable(true);
        pigIronMaterial = new TinkersRebornMaterial(PigIron, "PigIron", 0xF0A8A4);

        TinkersRebornRegistry.addMaterialToMap(woodMaterial);
        TinkersRebornRegistry.addMaterialToMap(stoneMaterial);
        TinkersRebornRegistry.addMaterialToMap(ironMaterial);
        TinkersRebornRegistry.addMaterialToMap(flintMaterial);
        TinkersRebornRegistry.addMaterialToMap(cactusMaterial);
        TinkersRebornRegistry.addMaterialToMap(boneMaterial);
        TinkersRebornRegistry.addMaterialToMap(obsidianMaterial);
        TinkersRebornRegistry.addMaterialToMap(netherrackMaterial);
        TinkersRebornRegistry.addMaterialToMap(slimeMaterial);
        TinkersRebornRegistry.addMaterialToMap(paperMaterial);
        TinkersRebornRegistry.addMaterialToMap(cobaltMaterial);
        TinkersRebornRegistry.addMaterialToMap(arditeMaterial);
        TinkersRebornRegistry.addMaterialToMap(manyullynMaterial);
        TinkersRebornRegistry.addMaterialToMap(copperMaterial);
        TinkersRebornRegistry.addMaterialToMap(bronzeMaterial);
        TinkersRebornRegistry.addMaterialToMap(alumiteMaterial);
        TinkersRebornRegistry.addMaterialToMap(steelMaterial);
        TinkersRebornRegistry.addMaterialToMap(blueSlimeMaterial);
        TinkersRebornRegistry.addMaterialToMap(pigIronMaterial);

        this.registerBaseMaterialsStats();
        this.registerMaterialsFluid();
    }

    private void registerBaseMaterialsStats() {
        woodMaterial.addStats(
            new HeadMaterialStats(97, 1, 0, 3.5F),
            new HandleMaterialStats(1.0F, 0),
            new ExtraMaterialStats(0));
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
            new ExtraMaterialStats(0));
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
    }

    private void registerMaterialsFluid() {
        ironFluid = new TinkersRebornFluid(ironMaterial, true);
        obsidianFluid = new TinkersRebornFluid(obsidianMaterial, true);
        cobaltFluid = new TinkersRebornFluid(cobaltMaterial, true);
        arditeFluid = new TinkersRebornFluid(arditeMaterial, true);
        manyullynFluid = new TinkersRebornFluid(manyullynMaterial, true);
        copperFluid = new TinkersRebornFluid(copperMaterial, true);
        bronzeFluid = new TinkersRebornFluid(bronzeMaterial, true);
        alumiteFluid = new TinkersRebornFluid(alumiteMaterial, true);
        steelFluid = new TinkersRebornFluid(steelMaterial, true);
        pigIronFluid = new TinkersRebornFluid(pigIronMaterial, true);
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
    }
}
