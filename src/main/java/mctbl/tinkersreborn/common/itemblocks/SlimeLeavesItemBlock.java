package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeLeavesItemBlock extends TinkersRebornItemBlock {

    public SlimeLeavesItemBlock(Block b) {
        super(b, "tinkersreborn.slime.leaves", TinkersRebornConfig.slimeTypes);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

}
