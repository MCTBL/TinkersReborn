package mctbl.tinkersreborn.common.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;

public class MetalOre extends TinkersRebornBlock {

    public MetalOre() {
        super(Material.rock, "tinkersreborn.MetalOre", 10.0F, TinkersRebornConfig.oreTypes);

        this.setHarvestLevel("pickaxe", 4, 1); // cobalt
        this.setHarvestLevel("pickaxe", 4, 2); // ardite
        this.setHarvestLevel("pickaxe", 1, 3); // copper
        this.setHarvestLevel("pickaxe", 1, 4); // tin
        this.setHarvestLevel("pickaxe", 1, 5); // aluminum

        this.setCreativeTab(TinkersRebornRegistry.blockTab);
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta <= 2) return 10f;
        else return 3f;
    }

    @Override
    public void getSubBlocks(Item b, CreativeTabs tab, List<ItemStack> list) {
        for (int iter = 0; iter < textureNames.length; iter++) {
            list.add(new ItemStack(b, 1, iter));
        }
    }

}
