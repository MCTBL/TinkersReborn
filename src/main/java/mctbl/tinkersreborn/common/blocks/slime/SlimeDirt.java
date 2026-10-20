package mctbl.tinkersreborn.common.blocks.slime;

import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.blocks.TinkersRebornBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class SlimeDirt extends TinkersRebornBlock {

    public SlimeDirt() {
        super(
            Material.gourd,
            "tinkersreborn.slime.dirt",
            3.0F,
            new String[] { "slime/slimedirt_blue", "slime/slimedirt_green", "slime/slimedirt_purple",
                "slime/slimedirt_magma" });
        this.setHarvestLevel("shovel", -1);
        this.setLightOpacity(0);
        this.stepSound = Block.soundTypeGravel;
        this.setCreativeTab(TinkersRebornRegistry.blockTab);
    }
    
}
