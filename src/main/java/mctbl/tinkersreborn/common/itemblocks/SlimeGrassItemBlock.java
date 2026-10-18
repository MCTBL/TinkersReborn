package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeGrassItemBlock extends TinkersRebornItemBlock {

    public SlimeGrassItemBlock(Block b) {
        super(b, "tinkersreborn.slime.grass", TinkersRebornConfig.slimeTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
