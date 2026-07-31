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
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.common.blocks.GravelOre;
import mctbl.tinkersreborn.library.utils.MiningLevelHelper;
import mctbl.tinkersreborn.library.utils.MiningLevelHelper.MiningLevel;

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

        // vanllia block override
        harvestLevelOverride();
        // ore dict block override
        oreDictOverride();
        // extra override
        blockOverride();
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
                String tool = block.getHarvestTool(m);
                if (tool == null) continue;
                cfg.get("blocks_" + tool, key.toString() + ":" + m, block.getHarvestLevel(m));
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

        StringBuilder comment = new StringBuilder("write ore dict under here\n");
        comment.append("Mining Levels:\n");
        for (MiningLevel level : MiningLevelHelper.levelList) {
            comment.append(String.format("  %d - %s%n", level.levelIdx, level.getLocalization()));
        }
        cfg.setCategoryComment("oreDictLevels", comment.toString());

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

    /**
     * Exact per-block override via TinkersRebornHarvestLevelOverride.cfg.
     * <p>
     * Format: categories named {@code blocks_<toolclass>} containing properties
     * {@code modid:name:metadata = harvestLevel}.
     * <ul>
     * <li>metadata = -1 → applies to all metas (wildcard)</li>
     * <li>harvestLevel = -1 → removes tool effectiveness for this block</li>
     * </ul>
     */
    private static void blockOverride() {
        Configuration cfg = new Configuration(
            new File(TinkersReborn.cfgDirectory + "/Tinkersreborn/TinkersRebornHarvestLevelOverride.cfg"));
        cfg.load();

        // write help comment into the Info category
        StringBuilder comment = new StringBuilder();
        comment.append("Exact per-block harvest level overrides. Runs AFTER vanilla mapping and ore-dict mapping.\n");
        comment.append("Copy entries from TinkersRebornHarvestLevelDefault.cfg, then change the number.\n\n");
        comment.append("Format: <modid>:<name>:<metadata> = <harvestlevel>\n");
        comment.append("  -1 as metadata = apply to ALL metas for this block\n");
        comment.append("  -1 as harvest level = remove tool effectiveness for this block\n\n");
        comment.append("Mining Levels:\n");
        for (MiningLevel level : MiningLevelHelper.levelList) {
            comment.append(String.format("  %d - %s%n", level.levelIdx, level.getLocalization()));
        }
        cfg.setCategoryComment("info", comment.toString());

        for (String catName : cfg.getCategoryNames()) {
            if (!catName.startsWith("blocks_")) continue;

            String toolClass = catName.substring(7); // "blocks_pickaxe" → "pickaxe"
            ConfigCategory cat = cfg.getCategory(catName);

            for (Property prop : cat.values()) {
                String key = prop.getName();
                int lastColon = key.lastIndexOf(':');
                if (lastColon < 0) continue;

                String blockId = key.substring(0, lastColon);
                String metaStr = key.substring(lastColon + 1);
                int meta;
                try {
                    meta = Integer.parseInt(metaStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                int harvestLevel = prop.getInt();

                if (!Block.blockRegistry.containsKey(blockId)) continue;
                Block block = (Block) Block.blockRegistry.getObject(blockId);

                if (meta == -1) {
                    block.setHarvestLevel(toolClass, harvestLevel);
                    if (TinkersRebornConfig.debug) {
                        TinkersReborn.LOG.info(
                            String.format(
                                "Block Override: Changed Harvest Level of %s (all metas) to %d for tool %s",
                                block.getUnlocalizedName(),
                                harvestLevel,
                                toolClass));
                    }
                } else {
                    block.setHarvestLevel(toolClass, harvestLevel, meta);
                    if (TinkersRebornConfig.debug) {
                        TinkersReborn.LOG.info(
                            String.format(
                                "Block Override: Changed Harvest Level of %s:%d to %d for tool %s",
                                block.getUnlocalizedName(),
                                meta,
                                harvestLevel,
                                toolClass));
                    }
                }
            }
        }

        cfg.save();
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
