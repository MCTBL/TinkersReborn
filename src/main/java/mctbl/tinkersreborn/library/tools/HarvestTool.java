package mctbl.tinkersreborn.library.tools;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;

import com.google.common.collect.ImmutableSet;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.tools.Category;

public abstract class HarvestTool extends ToolCore {

    // Materials are known at compile time, can use ImmutableSet.of()
    protected static final Set<Material> pickaxeEffectiveMaterials = ImmutableSet.of(
        Material.rock,
        Material.iron,
        Material.ice,
        Material.glass,
        Material.piston,
        Material.anvil,
        Material.packedIce);

    // Blocks require reflection to match vanilla, built via temp set then frozen
    protected static final Set<Block> pickaxeEffectiveBlocks;

    protected static final Set<Material> shovelEffectiveMaterials = ImmutableSet.of(
        Material.grass,
        Material.ground,
        Material.sand,
        Material.snow,
        Material.craftedSnow,
        Material.clay,
        Material.cake);

    protected static final Set<Block> shovelEffectiveBlocks;

    protected static final Set<Material> axeEffectiveMaterials = ImmutableSet
        .of(Material.wood, Material.vine, Material.plants, Material.gourd, Material.cactus);

    protected static final Set<Block> axeEffectiveBlocks;

    protected static final Set<Material> kamaEffectiveMaterials = ImmutableSet.of(
        Material.web,
        Material.leaves,
        Material.plants,
        Material.vine,
        Material.gourd,
        Material.cactus,
        Material.cloth,
        Material.sponge);

    static {
        // Pickaxe effective blocks (via reflection from vanilla)
        Set<Block> pickBlocks = new HashSet<>();
        try {
            Field effectSetField = ItemPickaxe.class.getDeclaredField("field_150915_c");
            effectSetField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Block> blockSet = (Set<Block>) effectSetField.get(null);
            pickBlocks.addAll(blockSet);
        } catch (Exception e) {
            TinkersReborn.LOG.warn("Tinkers Pickaxe get error when try to get vanila pickaxe's effective block list");
        }
        pickaxeEffectiveBlocks = ImmutableSet.copyOf(pickBlocks);

        // Shovel effective blocks
        Set<Block> shovelBlocks = new HashSet<>();
        try {
            Field effectSetField = ItemSpade.class.getDeclaredField("field_150916_c");
            effectSetField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Block> blockSet = (Set<Block>) effectSetField.get(null);
            shovelBlocks.addAll(blockSet);
        } catch (Exception e) {
            TinkersReborn.LOG.warn("Tinkers Shovel get error when try to get vanila shovel's effective block list");
        }
        shovelEffectiveBlocks = ImmutableSet.copyOf(shovelBlocks);

        // Axe effective blocks
        Set<Block> axeBlocks = new HashSet<>();
        try {
            Field effectSetField = ItemAxe.class.getDeclaredField("field_150917_c");
            effectSetField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Block> blockSet = (Set<Block>) effectSetField.get(null);
            axeBlocks.addAll(blockSet);
        } catch (Exception e) {
            TinkersReborn.LOG.warn("Tinkers Hatchet get error when try to get vanila axe's effective block list");
        }
        axeEffectiveBlocks = ImmutableSet.copyOf(axeBlocks);
    }

    protected HarvestTool(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        this.categoryTags.add(Category.HARVEST);
        this.categoryTags.add(Category.TOOL);
    }

}
