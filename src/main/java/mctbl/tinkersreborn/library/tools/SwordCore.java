package mctbl.tinkersreborn.library.tools;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.tools.Category;

public abstract class SwordCore extends ToolCore {

    protected static final Set<Material> swordEffectiveMaterials = new HashSet<>();

    static {
        swordEffectiveMaterials.add(Material.web);
        swordEffectiveMaterials.add(Material.vine);
        swordEffectiveMaterials.add(Material.coral);
        swordEffectiveMaterials.add(Material.gourd);
        swordEffectiveMaterials.add(Material.leaves);
    }

    protected SwordCore(String toolTypeName, int partAmount) {
        super(toolTypeName, partAmount);
        this.categoryTags.add(Category.WEAPON);

        // extended compatibility
        this.setHarvestLevel("sword", 0);
    }

    @Override
    public boolean isEffective(Block block) {
        return swordEffectiveMaterials.contains(block.getMaterial());
    }

    @Override
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
        if (block == Blocks.web) {
            return super.getDigSpeed(itemstack, block, metadata) * 7.5F;
        }
        return super.getDigSpeed(itemstack, block, metadata);
    }

    @Override
    public float miningSpeedModifier() {
        return 0.5F;
    }
}
