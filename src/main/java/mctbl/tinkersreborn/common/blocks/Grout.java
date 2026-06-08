package mctbl.tinkersreborn.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;

public class Grout extends TinkersRebornBlock {

    public Grout() {
        super(Material.gourd, "tinkersreborn.grout", 3.0F, new String[] { "grout" });
        this.setHarvestLevel("shovel", -1);
        this.setLightOpacity(0);
        this.stepSound = Block.soundTypeGravel;
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
    }

}
