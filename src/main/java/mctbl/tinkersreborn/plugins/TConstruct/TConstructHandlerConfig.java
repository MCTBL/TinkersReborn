package mctbl.tinkersreborn.plugins.TConstruct;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;

public class TConstructHandlerConfig {

    private TConstructHandlerConfig() {
        /* This utility class should not be instantiated */
    }

    public static String TConstruct = "TConstruct";

    public static Map<String, String> tinkerConstructBlockReplaceMap = new HashMap<>();
    public static Map<String, String> tinkerConstructItemReplaceMap = new HashMap<>();

    public static void preInit() {
        if (TinkersRebornConfig.exportTConstructHandlerDefaultConfig) {
            saveDefault();
        }
        setupConfig();
    }

    private static void saveDefault() {
        Configuration cfg = new Configuration(
            new File(TinkersReborn.cfgDirectory + "/Tinkersreborn/TConstructHandler.cfg"));
        cfg.addCustomCategoryComment("TConstruct", "");

        List<String> replaceList = new ArrayList<>();
        replaceList.add("TConstruct:liquid.slime;TinkersReborn:fluid.tinkersreborn.blue_slime");
        replaceList.add("TConstruct:slime.gel;TinkersReborn:tinkersreborn.slime.gel");
        replaceList.add("TConstruct:slime.gel:1;TinkersReborn:tinkersreborn.slime.gel:1");
        replaceList.add("TConstruct:slime.gel:2;TinkersReborn:tinkersreborn.slime.gel:2");
        replaceList.add("TConstruct:slime.grass;TinkersReborn:tinkersreborn.slime.grass");
        replaceList.add("TConstruct:slime.grass.tall;TinkersReborn:tinkersreborn.slime.grass.tall");
        replaceList.add("TConstruct:slime.leaves;TinkersReborn:tinkersreborn.slime.leaves");
        replaceList.add("TConstruct:slime.sapling;TinkersReborn:tinkersreborn.slime.sapling");
        replaceList.add("TConstruct:decoration.stonetorch;TinkersReborn:tinkersreborn.stonetorch");
        replaceList.add("TConstruct:decoration.stoneladder;minecraft:ladder");
        replaceList.add("TConstruct:decoration.multibrick;minecraft:obsidian");
        replaceList.add("TConstruct:decoration.multibrick:1;minecraft:sandstone");
        replaceList.add("TConstruct:decoration.multibrick:2;minecraft:netherrack");
        replaceList.add("TConstruct:decoration.multibrick:3;minecraft:stonebrick");
        replaceList.add("TConstruct:decoration.multibrick:4;minecraft:iron_block");
        replaceList.add("TConstruct:decoration.multibrick:5;minecraft:gold_block");
        replaceList.add("TConstruct:decoration.multibrick:6;minecraft:lapis_block");
        replaceList.add("TConstruct:decoration.multibrick:7;minecraft:diamond_block");
        replaceList.add("TConstruct:decoration.multibrick:8;minecraft:redstone_block");
        replaceList.add("TConstruct:decoration.multibrick:10;TinkersReborn:tinkersreborn.slime.gel");
        replaceList.add("TConstruct:decoration.multibrick:11;TinkersReborn:tinkersreborn.slime.gel:1");
        replaceList.add("TConstruct:decoration.multibrick:12;minecraft:end_stone");
        replaceList.add("TConstruct:decoration.multibrick:13;minecraft:obsidian");
        replaceList.add("TConstruct:decoration.multibrickfancy;minecraft:obsidian");
        replaceList.add("TConstruct:decoration.multibrickfancy:1;minecraft:sandstone");
        replaceList.add("TConstruct:decoration.multibrickfancy:2;minecraft:netherrack");
        replaceList.add("TConstruct:decoration.multibrickfancy:3;minecraft:stonebrick");
        replaceList.add("TConstruct:decoration.multibrickfancy:4;minecraft:iron_block");
        replaceList.add("TConstruct:decoration.multibrickfancy:5;minecraft:gold_block");
        replaceList.add("TConstruct:decoration.multibrickfancy:6;minecraft:lapis_block");
        replaceList.add("TConstruct:decoration.multibrickfancy:7;minecraft:diamond_block");
        replaceList.add("TConstruct:decoration.multibrickfancy:8;minecraft:redstone_block");
        replaceList.add("TConstruct:decoration.multibrickfancy:10;TinkersReborn:tinkersreborn.slime.gel");
        replaceList.add("TConstruct:decoration.multibrickfancy:11;TinkersReborn:tinkersreborn.slime.gel:1");
        replaceList.add("TConstruct:decoration.multibrickfancy:12;minecraft:end_stone");
        replaceList.add("TConstruct:decoration.multibrickfancy:13;minecraft:obsidian");
        replaceList.add("TConstruct:decoration.multibrickfancy:14;minecraft:stonebrick");
        replaceList.add("TConstruct:decoration.multibrickfancy:15;minecraft:stonebrick");
        replaceList.add("TConstruct:decoration.multibrickmetal;TinkersReborn:tinkersreborn.metalblock:8");
        replaceList.add("TConstruct:decoration.multibrickmetal:1;TinkersReborn:tinkersreborn.metalblock:1");
        replaceList.add("TConstruct:decoration.multibrickmetal:2;TinkersReborn:tinkersreborn.metalblock");
        replaceList.add("TConstruct:decoration.multibrickmetal:3;TinkersReborn:tinkersreborn.metalblock:2");
        replaceList.add("TConstruct:decoration.multibrickmetal:4;TinkersReborn:tinkersreborn.metalblock:8");
        replaceList.add("TConstruct:decoration.multibrickmetal:5;TinkersReborn:tinkersreborn.metalblock:1");
        replaceList.add("TConstruct:decoration.multibrickmetal:6;TinkersReborn:tinkersreborn.metalblock");
        replaceList.add("TConstruct:decoration.multibrickmetal:7;TinkersReborn:tinkersreborn.metalblock:2");
        replaceList.add("TConstruct:ore.berries.one;TinkersReborn:tinkersreborn.OreberryBush");
        replaceList.add("TConstruct:ore.berries.one:1;TinkersReborn:tinkersreborn.OreberryBush:1");
        replaceList.add("TConstruct:ore.berries.one:2;TinkersReborn:tinkersreborn.OreberryBush:2");
        replaceList.add("TConstruct:ore.berries.one:3;TinkersReborn:tinkersreborn.OreberryBush:3");
        replaceList.add("TConstruct:ore.berries.one:4;TinkersReborn:tinkersreborn.OreberryBush");
        replaceList.add("TConstruct:ore.berries.one:5;TinkersReborn:tinkersreborn.OreberryBush:1");
        replaceList.add("TConstruct:ore.berries.one:6;TinkersReborn:tinkersreborn.OreberryBush:2");
        replaceList.add("TConstruct:ore.berries.one:7;TinkersReborn:tinkersreborn.OreberryBush:3");
        replaceList.add("TConstruct:ore.berries.one:8;TinkersReborn:tinkersreborn.OreberryBush");
        replaceList.add("TConstruct:ore.berries.one:9;TinkersReborn:tinkersreborn.OreberryBush:1");
        replaceList.add("TConstruct:ore.berries.one:10;TinkersReborn:tinkersreborn.OreberryBush:2");
        replaceList.add("TConstruct:ore.berries.one:11;TinkersReborn:tinkersreborn.OreberryBush:3");
        replaceList.add("TConstruct:ore.berries.one:12;TinkersReborn:tinkersreborn.OreberryBush");
        replaceList.add("TConstruct:ore.berries.one:13;TinkersReborn:tinkersreborn.OreberryBush:1");
        replaceList.add("TConstruct:ore.berries.one:14;TinkersReborn:tinkersreborn.OreberryBush:2");
        replaceList.add("TConstruct:ore.berries.one:15;TinkersReborn:tinkersreborn.OreberryBush:3");
        replaceList.add("TConstruct:ore.berries.two;TinkersReborn:tinkersreborn.OreberryBush:4");
        replaceList.add("TConstruct:ore.berries.two:1;TinkersReborn:tinkersreborn.OreberryBush:5");
        replaceList.add("TConstruct:ore.berries.two:4;TinkersReborn:tinkersreborn.OreberryBush:4");
        replaceList.add("TConstruct:ore.berries.two:5;TinkersReborn:tinkersreborn.OreberryBush:5");
        replaceList.add("TConstruct:ore.berries.two:8;TinkersReborn:tinkersreborn.OreberryBush:4");
        replaceList.add("TConstruct:ore.berries.two:9;TinkersReborn:tinkersreborn.OreberryBush:5");
        replaceList.add("TConstruct:ore.berries.two:12;TinkersReborn:tinkersreborn.OreberryBush:4");
        replaceList.add("TConstruct:ore.berries.two:13;TinkersReborn:tinkersreborn.OreberryBush:5");
        replaceList.add("TConstruct:SearedBrick:1;TinkersReborn:tinkersreborn.MetalOre");
        replaceList.add("TConstruct:SearedBrick:2;TinkersReborn:tinkersreborn.MetalOre:1");
        replaceList.add("TConstruct:SearedBrick:3;TinkersReborn:tinkersreborn.MetalOre:2");
        replaceList.add("TConstruct:SearedBrick:4;TinkersReborn:tinkersreborn.MetalOre:3");
        replaceList.add("TConstruct:SearedBrick:5;TinkersReborn:tinkersreborn.MetalOre:4");
        replaceList.add("TConstruct:GravelOre;TinkersReborn:tinkersreborn.GravelOre");
        replaceList.add("TConstruct:GravelOre:1;TinkersReborn:tinkersreborn.GravelOre:1");
        replaceList.add("TConstruct:GravelOre:2;TinkersReborn:tinkersreborn.GravelOre:2");
        replaceList.add("TConstruct:GravelOre:3;TinkersReborn:tinkersreborn.GravelOre:3");
        replaceList.add("TConstruct:GravelOre:4;TinkersReborn:tinkersreborn.GravelOre:4");
        replaceList.add("TConstruct:MetalBlock;TinkersReborn:tinkersreborn.metalblock");
        replaceList.add("TConstruct:MetalBlock:1;TinkersReborn:tinkersreborn.metalblock:1");
        replaceList.add("TConstruct:MetalBlock:2;TinkersReborn:tinkersreborn.metalblock:2");
        replaceList.add("TConstruct:MetalBlock:3;TinkersReborn:tinkersreborn.metalblock:3");
        replaceList.add("TConstruct:MetalBlock:4;TinkersReborn:tinkersreborn.metalblock:4");
        replaceList.add("TConstruct:MetalBlock:5;TinkersReborn:tinkersreborn.metalblock:5");
        replaceList.add("TConstruct:MetalBlock:6;TinkersReborn:tinkersreborn.metalblock:6");
        replaceList.add("TConstruct:MetalBlock:7;TinkersReborn:tinkersreborn.metalblock:7");
        replaceList.add("TConstruct:MetalBlock:8;TinkersReborn:tinkersreborn.metalblock:8");
        replaceList.add("TConstruct:MetalBlock:9;TinkersReborn:tinkersreborn.metalblock:9");
        replaceList.add("TConstruct:MetalBlock:10;TinkersReborn:tinkersreborn.metalblock:10");
        replaceList.add("TConstruct:CraftedSoil;TinkersReborn:tinkersreborn.slime.sand:1");
        replaceList.add("TConstruct:CraftedSoil:1;TinkersReborn:tinkersreborn.grout");
        replaceList.add("TConstruct:CraftedSoil:2;TinkersReborn:tinkersreborn.slime.sand");
        replaceList.add("TConstruct:CraftedSoil:3;TinkersReborn:tinkersreborn.graveyardsoil");
        replaceList.add("TConstruct:CraftedSoil:4;TinkersReborn:tinkersreborn.consecratedsoil");
        replaceList.add("TConstruct:CraftedSoil:5;TinkersReborn:tinkersreborn.slime.dirt");
        replaceList.add("TConstruct:SearedSlab;TinkersReborn:tinkersreborn.SmelteryBlock");
        replaceList.add("TConstruct:SearedSlab:1;TinkersReborn:tinkersreborn.SmelteryBlock");
        replaceList.add("TConstruct:SearedSlab:2;TinkersReborn:tinkersreborn.SmelteryBlock");
        replaceList.add("TConstruct:SearedSlab:3;TinkersReborn:tinkersreborn.SmelteryBlock");
        replaceList.add("TConstruct:SearedSlab:4;TinkersReborn:tinkersreborn.SmelteryBlock");
        replaceList.add("TConstruct:SearedSlab:5;TinkersReborn:tinkersreborn.SmelteryBlock");
        replaceList.add("TConstruct:SearedSlab:6;TinkersReborn:tinkersreborn.SmelteryBlock");
        replaceList.add("TConstruct:SearedSlab:7;TinkersReborn:tinkersreborn.SmelteryBlock");
        replaceList.add("TConstruct:GlueBlock;TinkersReborn:tinkersreborn.glueblock");
        replaceList.add("TConstruct:GlassBlock;TinkersReborn:tinkersreborn.clearGlassBlock");
        replaceList.add("TConstruct:GlassPane;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassBlock.StainedClear;TinkersReborn:tinkersreborn.coloredGlassBlock:15");
        replaceList.add("TConstruct:GlassBlock.StainedClear:1;TinkersReborn:tinkersreborn.coloredGlassBlock:14");
        replaceList.add("TConstruct:GlassBlock.StainedClear:2;TinkersReborn:tinkersreborn.coloredGlassBlock:13");
        replaceList.add("TConstruct:GlassBlock.StainedClear:3;TinkersReborn:tinkersreborn.coloredGlassBlock:12");
        replaceList.add("TConstruct:GlassBlock.StainedClear:4;TinkersReborn:tinkersreborn.coloredGlassBlock:11");
        replaceList.add("TConstruct:GlassBlock.StainedClear:5;TinkersReborn:tinkersreborn.coloredGlassBlock:10");
        replaceList.add("TConstruct:GlassBlock.StainedClear:6;TinkersReborn:tinkersreborn.coloredGlassBlock:9");
        replaceList.add("TConstruct:GlassBlock.StainedClear:7;TinkersReborn:tinkersreborn.coloredGlassBlock:8");
        replaceList.add("TConstruct:GlassBlock.StainedClear:8;TinkersReborn:tinkersreborn.coloredGlassBlock:7");
        replaceList.add("TConstruct:GlassBlock.StainedClear:9;TinkersReborn:tinkersreborn.coloredGlassBlock:6");
        replaceList.add("TConstruct:GlassBlock.StainedClear:10;TinkersReborn:tinkersreborn.coloredGlassBlock:5");
        replaceList.add("TConstruct:GlassBlock.StainedClear:11;TinkersReborn:tinkersreborn.coloredGlassBlock:4");
        replaceList.add("TConstruct:GlassBlock.StainedClear:12;TinkersReborn:tinkersreborn.coloredGlassBlock:3");
        replaceList.add("TConstruct:GlassBlock.StainedClear:13;TinkersReborn:tinkersreborn.coloredGlassBlock:2");
        replaceList.add("TConstruct:GlassBlock.StainedClear:14;TinkersReborn:tinkersreborn.coloredGlassBlock:1");
        replaceList.add("TConstruct:GlassBlock.StainedClear:15;TinkersReborn:tinkersreborn.coloredGlassBlock");
        replaceList.add("TConstruct:GlassPaneClearStained;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:1;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:2;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:3;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:4;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:5;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:6;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:7;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:8;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:9;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:10;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:11;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:12;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:13;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:14;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:GlassPaneClearStained:15;TinkersReborn:tinkersreborn.clearGlassPane");
        replaceList.add("TConstruct:goldHead;TinkersReborn:tinkersreborn.goldenhead");
        replaceList.add("TConstruct:strangeFood;TinkersReborn:item.tinkersreborn.strangefood:2");
        replaceList.add("TConstruct:strangeFood:1;TinkersReborn:item.tinkersreborn.strangefood:1");
        replaceList.add("TConstruct:strangeFood:2;TinkersReborn:item.tinkersreborn.strangefood");
        replaceList.add("TConstruct:oreBerries;TinkersReborn:item.tinkersreborn.oreberry");
        replaceList.add("TConstruct:oreBerries:1;TinkersReborn:item.tinkersreborn.oreberry:1");
        replaceList.add("TConstruct:oreBerries:2;TinkersReborn:item.tinkersreborn.oreberry:2");
        replaceList.add("TConstruct:oreBerries:3;;TinkersReborn:item.tinkersreborn.oreberry:3");
        replaceList.add("TConstruct:oreBerries:4;TinkersReborn:item.tinkersreborn.oreberry:4");
        replaceList.add("TConstruct:oreBerries:5;TinkersReborn:item.tinkersreborn.oreberry:5");

        cfg.get(
            TConstruct,
            "tinkerConstructBlockReplaceMap",
            replaceList.toArray(new String[] {}),
            "such like this TConstruct:Smeltery:1 ; tinkersreborn:smeltery_drain")
            .getStringList();

        replaceList.add("TConstruct:ToolStationBlock;TinkersReborn:tinkersreborn.ToolStation");
        replaceList.add("TConstruct:ToolStationBlock:1;TinkersReborn:tinkersreborn.PartBuilder");
        replaceList.add("TConstruct:ToolStationBlock:2;TinkersReborn:tinkersreborn.PartBuilder:1");
        replaceList.add("TConstruct:ToolStationBlock:3;TinkersReborn:tinkersreborn.PartBuilder:2");
        replaceList.add("TConstruct:ToolStationBlock:4;TinkersReborn:tinkersreborn.PartBuilder:3");
        replaceList.add("TConstruct:ToolForgeBlock;TinkersReborn:tinkersreborn.ToolFroge");
        replaceList.add("TConstruct:ToolForgeBlock:1;TinkersReborn:tinkersreborn.ToolFroge:1");
        replaceList.add("TConstruct:ToolForgeBlock:2;TinkersReborn:tinkersreborn.ToolFroge:2");
        replaceList.add("TConstruct:ToolForgeBlock:3;TinkersReborn:tinkersreborn.ToolFroge:3");
        replaceList.add("TConstruct:ToolForgeBlock:4;TinkersReborn:tinkersreborn.ToolFroge:4");
        replaceList.add("TConstruct:ToolForgeBlock:5;TinkersReborn:tinkersreborn.ToolFroge:5");
        replaceList.add("TConstruct:ToolForgeBlock:6;TinkersReborn:tinkersreborn.ToolFroge:6");
        replaceList.add("TConstruct:ToolForgeBlock:7;TinkersReborn:tinkersreborn.ToolFroge:7");
        replaceList.add("TConstruct:ToolForgeBlock:8;TinkersReborn:tinkersreborn.ToolFroge:8");
        replaceList.add("TConstruct:ToolForgeBlock:9;TinkersReborn:tinkersreborn.ToolFroge:9");
        replaceList.add("TConstruct:ToolForgeBlock:10;TinkersReborn:tinkersreborn.ToolFroge:10");
        replaceList.add("TConstruct:ToolForgeBlock:11;TinkersReborn:tinkersreborn.ToolFroge:11");
        replaceList.add("TConstruct:ToolForgeBlock:12;TinkersReborn:tinkersreborn.ToolFroge:12");
        replaceList.add("TConstruct:ToolForgeBlock:13;TinkersReborn:tinkersreborn.ToolFroge:13");
        replaceList.add("TConstruct:CraftingStation;TinkersReborn:tinkersreborn.CraftingStation");
        replaceList.add("TConstruct:CraftingSlab;TinkersReborn:tinkersreborn.CraftingStation");
        replaceList.add("TConstruct:CraftingSlab:1;TinkersReborn:tinkersreborn.ToolStation");
        replaceList.add("TConstruct:CraftingSlab:2;TinkersReborn:tinkersreborn.PartBuilder");
        replaceList.add("TConstruct:CraftingSlab:5;TinkersReborn:tinkersreborn.ToolFroge");
        replaceList.add("TConstruct:FurnaceSlab;minecraft:furnace");
        replaceList.add("TConstruct:Smeltery;TinkersReborn:tinkersreborn.SmelteryController");
        replaceList.add("TConstruct:Smeltery:1;TinkersReborn:tinkersreborn.Drain");
        replaceList.add("TConstruct:Smeltery:2;TinkersReborn:tinkersreborn.SmelteryBlock");
        replaceList.add("TConstruct:Smeltery:4;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:Smeltery:5;TinkersReborn:tinkersreborn.SmelteryBlock:1");
        replaceList.add("TConstruct:Smeltery:6;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:Smeltery:7;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:Smeltery:8;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:Smeltery:9;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:Smeltery:10;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:Smeltery:11;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:1;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:2;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:4;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:5;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:6;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:7;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:8;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:9;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:10;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SmelteryNether:11;TinkersReborn:tinkersreborn.SmelteryBlock:2");
        replaceList.add("TConstruct:SearedBlock;TinkersReborn:tinkersreborn.SearedBlock");
        replaceList.add("TConstruct:SearedBlock:1;TinkersReborn:tinkersreborn.SearedBlock:1");
        replaceList.add("TConstruct:SearedBlock:2;TinkersReborn:tinkersreborn.SearedBlock:2");
        replaceList.add("TConstruct:SearedBlockNether;TinkersReborn:tinkersreborn.SearedBlock");
        replaceList.add("TConstruct:SearedBlockNether:1;TinkersReborn:tinkersreborn.SearedBlock:1");
        replaceList.add("TConstruct:SearedBlockNether:2;TinkersReborn:tinkersreborn.SearedBlock:2");
        replaceList.add("TConstruct:CastingChannel;TinkersReborn:tinkersreborn.SearedBlock.CastingChannel");
        replaceList.add("TConstruct:Armor.DryingRack;TinkersReborn:tinkersreborn.DryingRack");
        replaceList.add("TConstruct:blankPattern;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:blankPattern:1;TinkersReborn:tinkersreborn.Pattern:0:cast_blank");
        replaceList.add("TConstruct:blankPattern:2;TinkersReborn:tinkersreborn.Pattern:0:cast_blank");
        replaceList.add("TConstruct:blankPattern:3;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:materials;TinkersReborn:tinkersreborn.material.PaperStack");
        replaceList.add("TConstruct:materials:1;TinkersReborn:tinkersreborn.material.SlimeCrystal");
        replaceList.add("TConstruct:materials:2;TinkersReborn:tinkersreborn.material.SearedBrick");
        replaceList.add("TConstruct:materials:3;TinkersReborn:tinkersreborn.material.CobaltIngot");
        replaceList.add("TConstruct:materials:4;TinkersReborn:tinkersreborn.material.ArditeIngot");
        replaceList.add("TConstruct:materials:5;TinkersReborn:tinkersreborn.material.ManyullynIngot");
        replaceList.add("TConstruct:materials:6;TinkersReborn:tinkersreborn.material.Mossball");
        replaceList.add("TConstruct:materials:7;TinkersReborn:tinkersreborn.material.LavaCrystal");
        replaceList.add("TConstruct:materials:8;TinkersReborn:tinkersreborn.material.NecroticBone");
        replaceList.add("TConstruct:materials:9;TinkersReborn:tinkersreborn.material.CopperIngot");
        replaceList.add("TConstruct:materials:10;TinkersReborn:tinkersreborn.material.TinIngot");
        replaceList.add("TConstruct:materials:11;TinkersReborn:tinkersreborn.material.AluminumIngot");
        replaceList.add("TConstruct:materials:12;TinkersReborn:tinkersreborn.material.RawAluminum");
        replaceList.add("TConstruct:materials:13;TinkersReborn:tinkersreborn.material.BronzeIngot");
        replaceList.add("TConstruct:materials:14;TinkersReborn:tinkersreborn.material.AluBrassIngot");
        replaceList.add("TConstruct:materials:15;TinkersReborn:tinkersreborn.material.AlumiteIngot");
        replaceList.add("TConstruct:materials:16;TinkersReborn:tinkersreborn.material.SteelIngot");
        replaceList.add("TConstruct:materials:17;TinkersReborn:tinkersreborn.material.BlueSlimeCrystal");
        replaceList.add("TConstruct:materials:18;TinkersReborn:tinkersreborn.material.ObsidianIngot");
        replaceList.add("TConstruct:materials:19;TinkersReborn:tinkersreborn.material.IronNugget");
        replaceList.add("TConstruct:materials:20;TinkersReborn:tinkersreborn.material.CopperNugget");
        replaceList.add("TConstruct:materials:21;TinkersReborn:tinkersreborn.material.TinNugget");
        replaceList.add("TConstruct:materials:22;TinkersReborn:tinkersreborn.material.AluminumNugget");
        replaceList.add("TConstruct:materials:24;TinkersReborn:tinkersreborn.material.AluBrassNugget");
        replaceList.add("TConstruct:materials:25;TinkersReborn:tinkersreborn.material.SilkyCloth");
        replaceList.add("TConstruct:materials:26;TinkersReborn:tinkersreborn.material.SilkyJewel");
        replaceList.add("TConstruct:materials:27;TinkersReborn:tinkersreborn.material.ObsidianNugget");
        replaceList.add("TConstruct:materials:28;TinkersReborn:tinkersreborn.material.CobaltNugget");
        replaceList.add("TConstruct:materials:29;TinkersReborn:tinkersreborn.material.ArditeNugget");
        replaceList.add("TConstruct:materials:30;TinkersReborn:tinkersreborn.material.ManyullynNugget");
        replaceList.add("TConstruct:materials:31;TinkersReborn:tinkersreborn.material.BronzeNugget");
        replaceList.add("TConstruct:materials:32;TinkersReborn:tinkersreborn.material.AlumiteNugget");
        replaceList.add("TConstruct:materials:33;TinkersReborn:tinkersreborn.material.SteelNugget");
        replaceList.add("TConstruct:materials:34;TinkersReborn:tinkersreborn.material.PigIronIngot");
        replaceList.add("TConstruct:materials:35;TinkersReborn:tinkersreborn.material.PigIronNugget");
        replaceList.add("TConstruct:materials:36;TinkersReborn:tinkersreborn.material.GlueBall");
        replaceList.add("TConstruct:materials:37;TinkersReborn:tinkersreborn.material.SearedBrick");
        replaceList.add("TConstruct:materials:38;TinkersReborn:tinkersreborn.material.ArditeDust");
        replaceList.add("TConstruct:materials:39;TinkersReborn:tinkersreborn.material.CobaltDust");
        replaceList.add("TConstruct:materials:40;TinkersReborn:tinkersreborn.material.AluminumDust");
        replaceList.add("TConstruct:materials:41;TinkersReborn:tinkersreborn.material.ManyullynDust");
        replaceList.add("TConstruct:materials:42;TinkersReborn:tinkersreborn.material.AluBrassDust");
        replaceList.add("TConstruct:materials:43;TinkersReborn:tinkersreborn.material.Reinforcement");
        replaceList.add("TConstruct:woodPattern;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:1;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:2;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:3;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:4;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:5;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:6;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:7;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:8;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:9;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:10;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:11;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:12;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:13;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:14;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:15;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:16;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:17;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:18;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:19;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:20;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:21;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:22;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:23;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:24;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:woodPattern:25;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:Pattern;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:Pattern:1;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:Pattern:2;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:Pattern:3;TinkersReborn:tinkersreborn.Pattern:0:pattern_blank");
        replaceList.add("TConstruct:manualBook;TinkersReborn:item.tinkerbook:0:materialsandyou");
        replaceList.add("TConstruct:manualBook:1;TinkersReborn:item.tinkerbook:0:materialsandyou");
        replaceList.add("TConstruct:manualBook:2;TinkersReborn:item.tinkerbook:0:materialsandyou");
        replaceList.add("TConstruct:manualBook:3;TinkersReborn:item.tinkerbook:0:materialsandyou");
        replaceList.add("TConstruct:manualBook:4;TinkersReborn:item.tinkerbook:0:materialsandyou");
        replaceList.add("TConstruct:jerky;TinkersReborn:item.tinkersreborn.jerky");
        replaceList.add("TConstruct:jerky:1;TinkersReborn:item.tinkersreborn.jerky:1");
        replaceList.add("TConstruct:jerky:2;TinkersReborn:item.tinkersreborn.jerky:2");
        replaceList.add("TConstruct:jerky:3;TinkersReborn:item.tinkersreborn.jerky:3");
        replaceList.add("TConstruct:jerky:4;TinkersReborn:item.tinkersreborn.jerky:4");
        replaceList.add("TConstruct:jerky:5;TinkersReborn:item.tinkersreborn.jerky:5");
        replaceList.add("TConstruct:jerky:6;TinkersReborn:item.tinkersreborn.jerky:7");
        replaceList.add("TConstruct:jerky:7;TinkersReborn:item.tinkersreborn.jerky:6");
        replaceList.add("TConstruct:heartCanister;TinkersReborn:item.tinkersreborn.canister");
        replaceList.add("TConstruct:heartCanister:1;TinkersReborn:item.tinkersreborn.canister:1");
        replaceList.add("TConstruct:heartCanister:2;TinkersReborn:item.tinkersreborn.canister:2");
        replaceList.add("TConstruct:heartCanister:3;TinkersReborn:item.tinkersreborn.canister:3");
        replaceList.add("TConstruct:heartCanister:4;TinkersReborn:item.tinkersreborn.canister:4");
        replaceList.add("TConstruct:heartCanister:5;TinkersReborn:item.tinkersreborn.canister:5");
        replaceList.add("TConstruct:heartCanister:6;TinkersReborn:item.tinkersreborn.canister:6");

        replaceList.add("TConstruct:metalPattern;TinkersReborn:tinkersreborn.Pattern:0:cast_ingot");
        replaceList.add("TConstruct:metalPattern:1;TinkersReborn:tinkersreborn.Pattern:0:rod");
        replaceList.add("TConstruct:metalPattern:2;TinkersReborn:tinkersreborn.Pattern:0:pickaxe_head");
        replaceList.add("TConstruct:metalPattern:3;TinkersReborn:tinkersreborn.Pattern:0:shovel_head");
        replaceList.add("TConstruct:metalPattern:4;TinkersReborn:tinkersreborn.Pattern:0:axe_head");
        replaceList.add("TConstruct:metalPattern:5;TinkersReborn:tinkersreborn.Pattern:0:sword_blade");
        replaceList.add("TConstruct:metalPattern:6;TinkersReborn:tinkersreborn.Pattern:0:large_guard");
        replaceList.add("TConstruct:metalPattern:7;TinkersReborn:tinkersreborn.Pattern:0:medium_guard");
        replaceList.add("TConstruct:metalPattern:8;TinkersReborn:tinkersreborn.Pattern:0:crossbar");
        replaceList.add("TConstruct:metalPattern:9;TinkersReborn:tinkersreborn.Pattern:0:binding");
        replaceList.add("TConstruct:metalPattern:10;TinkersReborn:tinkersreborn.Pattern:0:shield_core");
        replaceList.add("TConstruct:metalPattern:11;TinkersReborn:tinkersreborn.Pattern:0:cast_blank");
        replaceList.add("TConstruct:metalPattern:12;TinkersReborn:tinkersreborn.Pattern:0:knife_blade");
        replaceList.add("TConstruct:metalPattern:13;TinkersReborn:tinkersreborn.Pattern:0:chisel_head");
        replaceList.add("TConstruct:metalPattern:14;TinkersReborn:tinkersreborn.Pattern:0:toughrod");
        replaceList.add("TConstruct:metalPattern:15;TinkersReborn:tinkersreborn.Pattern:0:toughbind");
        replaceList.add("TConstruct:metalPattern:16;TinkersReborn:tinkersreborn.Pattern:0:largeplate");
        replaceList.add("TConstruct:metalPattern:17;TinkersReborn:tinkersreborn.Pattern:0:lumberaxe_head");
        replaceList.add("TConstruct:metalPattern:18;TinkersReborn:tinkersreborn.Pattern:0:scythe_head");
        replaceList.add("TConstruct:metalPattern:19;TinkersReborn:tinkersreborn.Pattern:0:excavator_head");
        replaceList.add("TConstruct:metalPattern:20;TinkersReborn:tinkersreborn.Pattern:0:large_sword_blade");
        replaceList.add("TConstruct:metalPattern:21;TinkersReborn:tinkersreborn.Pattern:0:hammer_head");
        replaceList.add("TConstruct:metalPattern:22;TinkersReborn:tinkersreborn.Pattern:0:cast_blank");
        replaceList.add("TConstruct:metalPattern:25;TinkersReborn:tinkersreborn.Pattern:0:arrowhead");
        replaceList.add("TConstruct:metalPattern:26;TinkersReborn:tinkersreborn.Pattern:0:cast_gem");
        replaceList.add("TConstruct:metalPattern:27;TinkersReborn:tinkersreborn.Pattern:0:cast_nugget");
        replaceList.add("TConstruct:Cast;TinkersReborn:tinkersreborn.Pattern:0:shuriken");
        replaceList.add("TConstruct:Cast:1;TinkersReborn:tinkersreborn.Pattern:0:crossbow_limb");
        replaceList.add("TConstruct:Cast:2;TinkersReborn:tinkersreborn.Pattern:0:crossbow_body");
        replaceList.add("TConstruct:Cast:3;TinkersReborn:tinkersreborn.Pattern:0:bow_limb");

        cfg.get(
            TConstruct,
            "tinkerConstructItemReplaceMap",
            replaceList.toArray(new String[] {}),
            "such like this TConstruct:Smeltery:1 ; tinkersreborn:smeltery_drain")
            .getStringList();

        cfg.save();
    }

    private static void setupConfig() {
        Configuration cfg = new Configuration(
            new File(TinkersReborn.cfgDirectory + "/Tinkersreborn/TConstructHandler.cfg"));
        cfg.load();

        String[] blockReplaceList = cfg
            .get(
                TConstruct,
                "tinkerConstructBlockReplaceMap",
                new String[] {},
                "such like this TConstruct:Smeltery:1 ; tinkersreborn:smeltery_drain")
            .getStringList();
        for (String line : blockReplaceList) {
            final String[] split = line.split(";");
            if (split.length == 2) {
                tinkerConstructBlockReplaceMap.put(split[0].trim(), split[1].trim());
            }
        }
        tinkerConstructBlockReplaceMap = Collections.unmodifiableMap(tinkerConstructBlockReplaceMap);

        String[] itemReplaceList = cfg
            .get(
                TConstruct,
                "tinkerConstructItemReplaceMap",
                new String[] {},
                "such like this TConstruct:Smeltery:1 ; tinkersreborn:smeltery_drain")
            .getStringList();
        for (String line : itemReplaceList) {
            final String[] split = line.split(";");
            if (split.length == 2) {
                tinkerConstructItemReplaceMap.put(split[0].trim(), split[1].trim());
            }
        }
        tinkerConstructItemReplaceMap = Collections.unmodifiableMap(tinkerConstructItemReplaceMap);

        cfg.save();
    }

}
