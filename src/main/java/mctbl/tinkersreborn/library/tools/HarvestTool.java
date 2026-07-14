package mctbl.tinkersreborn.library.tools;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.tools.Category;

public abstract class HarvestTool extends ToolCore {

    protected static final Set<Material> pickaxeEffectiveMaterials = new HashSet<>();
    protected static final Set<Block> pickaxeEffectiveBlocks = new HashSet<>();

    protected static final Set<Material> shovelEffectiveMaterials = new HashSet<>();
    protected static final Set<Block> shovelEffectiveBlocks = new HashSet<>();

    protected static final Set<Material> axeEffectiveMaterials = new HashSet<>();
    protected static final Set<Block> axeEffectiveBlocks = new HashSet<>();

    static {
        pickaxeEffectiveMaterials.add(Material.rock);
        pickaxeEffectiveMaterials.add(Material.iron);
        pickaxeEffectiveMaterials.add(Material.ice);
        pickaxeEffectiveMaterials.add(Material.glass);
        pickaxeEffectiveMaterials.add(Material.piston);
        pickaxeEffectiveMaterials.add(Material.anvil);
        pickaxeEffectiveMaterials.add(Material.packedIce);

        try {
            Field effectSetField = ItemPickaxe.class.getDeclaredField("field_150915_c");
            effectSetField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Block> blockSet = (Set<Block>) effectSetField.get(null);
            pickaxeEffectiveBlocks.addAll(blockSet);
        } catch (Exception e) {
            TinkersReborn.LOG.warn("Tinkers Pickaxe get error when try to get vanila pickaxe's effective block list");
        }

        shovelEffectiveMaterials.add(Material.grass);
        shovelEffectiveMaterials.add(Material.ground);
        shovelEffectiveMaterials.add(Material.sand);
        shovelEffectiveMaterials.add(Material.snow);
        shovelEffectiveMaterials.add(Material.craftedSnow);
        shovelEffectiveMaterials.add(Material.clay);
        shovelEffectiveMaterials.add(Material.cake);

        try {
            Field effectSetField = ItemSpade.class.getDeclaredField("field_150916_c");
            effectSetField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Block> blockSet = (Set<Block>) effectSetField.get(null);
            shovelEffectiveBlocks.addAll(blockSet);
        } catch (Exception e) {
            TinkersReborn.LOG.warn("Tinkers Shovel get error when try to get vanila shovel's effective block list");
        }

        axeEffectiveMaterials.add(Material.wood);
        axeEffectiveMaterials.add(Material.vine);
        axeEffectiveMaterials.add(Material.plants);
        axeEffectiveMaterials.add(Material.gourd);
        axeEffectiveMaterials.add(Material.cactus);

        try {
            Field effectSetField = ItemAxe.class.getDeclaredField("field_150917_c");
            effectSetField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Block> blockSet = (Set<Block>) effectSetField.get(null);
            axeEffectiveBlocks.addAll(blockSet);
        } catch (Exception e) {
            TinkersReborn.LOG.warn("Tinkers Hatchet get error when try to get vanila axe's effective block list");
        }
    }

    protected HarvestTool(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        this.categoryTags.add(Category.HARVEST);
        this.categoryTags.add(Category.TOOL);
    }

}
