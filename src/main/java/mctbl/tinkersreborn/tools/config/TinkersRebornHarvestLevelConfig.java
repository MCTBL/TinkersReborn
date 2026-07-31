package mctbl.tinkersreborn.tools.config;

import static mctbl.tinkersreborn.library.utils.MiningLevelHelper.getVanillaHarvestLevelMapping;
import static net.minecraft.init.Blocks.diamond_block;
import static net.minecraft.init.Blocks.diamond_ore;
import static net.minecraft.init.Blocks.emerald_block;
import static net.minecraft.init.Blocks.emerald_ore;
import static net.minecraft.init.Blocks.gold_block;
import static net.minecraft.init.Blocks.gold_ore;
import static net.minecraft.init.Blocks.lit_redstone_ore;
import static net.minecraft.init.Blocks.redstone_ore;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.blocks.GravelOre;
import mctbl.tinkersreborn.library.utils.MiningLevelHelper;

public class TinkersRebornHarvestLevelConfig {

    public static List<String[]> oreDictLevels = new ArrayList<>();

    private TinkersRebornHarvestLevelConfig() {}

    public static void postInit() {
        // ensure that the forgehooks are in place
        new ForgeHooks(); // this ensures that the static initializer of ForgeHooks is called already. Otherwise it
        // overwrites our Harvestlevel changes.
        // see ForgeHooks.initTools()

        // blocks part
        if (TinkersRebornConfig.exportHarvestLevelDefaultConfig) {
            saveDeafult();
        }

        harvestLevelOverride();
        oreDictOverride();
        // tools part
    }

    private static void saveDeafult() {
        Configuration cfg = new Configuration(
            new File(TinkersReborn.cfgDirectory + "/Tinkersreborn/TinkersRebornHarvestLevelDefault.cfg"));
        // blocks can not really be added automatically because derp
        for (Object key : Block.blockRegistry.getKeys()) {
            Block block = (Block) Block.blockRegistry.getObject(key);

            int meta = -1;
            LinkedList<Integer> metas = new LinkedList<Integer>();
            while (++meta < 16) {
                if (block.getHarvestLevel(meta) == -1) continue;
                try {
                    String s = new ItemStack(block, 1, meta).getDisplayName();
                    if (s == null || s.isEmpty()) continue;
                } catch (Exception e) {
                    // bad practice to catch exception, but it ensures that mc doesn't crash if modders do weird stuff
                    continue;
                }

                metas.add(meta);
            }

            if (metas.isEmpty()) continue;

            // write it down
            for (Integer m : metas) {
                cfg.get("blocks_" + block.getHarvestTool(m), key.toString() + ":" + m, block.getHarvestLevel(m));
            }
        }
        cfg.save();
    }

    private static void harvestLevelOverride() {
        Blocks.obsidian.setHarvestLevel("pickaxe", getVanillaHarvestLevelMapping(3));
        Blocks.enchanting_table.setHarvestLevel("pickaxe", getVanillaHarvestLevelMapping(3));
        for (Block block : new Block[] { emerald_ore, emerald_block, diamond_ore, diamond_block, gold_ore, gold_block,
            redstone_ore, lit_redstone_ore }) {
            block.setHarvestLevel("pickaxe", getVanillaHarvestLevelMapping(2));
        }
        Blocks.iron_ore.setHarvestLevel("pickaxe", getVanillaHarvestLevelMapping(1));
        Blocks.iron_block.setHarvestLevel("pickaxe", getVanillaHarvestLevelMapping(1));
        Blocks.iron_bars.setHarvestLevel("pickaxe", getVanillaHarvestLevelMapping(1));
        Blocks.lapis_ore.setHarvestLevel("pickaxe", getVanillaHarvestLevelMapping(1));
        Blocks.lapis_block.setHarvestLevel("pickaxe", getVanillaHarvestLevelMapping(1));
        Blocks.quartz_ore.setHarvestLevel("pickaxe", getVanillaHarvestLevelMapping(0));
    }

    private static void readCfg() {
        Configuration cfg = new Configuration(
            new File(TinkersReborn.cfgDirectory + "/Tinkersreborn/TinkersRebornOreDictHarvestLevelOverride.cfg"));
        cfg.load();
        cfg.setCategoryComment("oreDictLevels", "write ore dict under here");
        for (int level = 0; level < MiningLevelHelper.levelList.size(); level++) {
            oreDictLevels.add(
                cfg.get("oreDictLevels", String.valueOf(level), new String[0])
                    .getStringList());
        }
        cfg.save();
    }

    private static void oreDictOverride() {
        readCfg();
        for (int i = 0; i < oreDictLevels.size(); ++i) for (String materialName : oreDictLevels.get(i)) {
            for (String prefix : TinkersRebornConfig.oreDictPrefixes)
                for (ItemStack oreStack : OreDictionary.getOres(prefix + materialName)) modifyBlock(oreStack, i);
        }
    }

    public static void modifyBlock(ItemStack stack, int harvestLevel) {
        Block block = Block.getBlockFromItem(stack.getItem());

        int meta = stack.getItemDamage();
        Integer[] metas;
        if (meta == OreDictionary.WILDCARD_VALUE)
            metas = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        else metas = new Integer[] { meta };

        for (int m : metas) {
            try {
                TinkersReborn.LOG.debug(
                    String.format(
                        "Changed Harvest Level of %s from %d to %d",
                        stack.getUnlocalizedName(),
                        block.getHarvestLevel(m),
                        harvestLevel));

                // gravelore gets shovel level instead of pickaxe.
                if (block instanceof GravelOre) block.setHarvestLevel("shovel", harvestLevel, m);
                else block.setHarvestLevel("pickaxe", harvestLevel, m);

                TinkersReborn.LOG.info(
                    String.format(
                        "Block Override: Changed Harvest Level of %s to %d",
                        stack.getUnlocalizedName(),
                        harvestLevel));
            } catch (Exception e) {
                // exception can occur if stuff does weird things metadatas
            }
        }
    }

}
