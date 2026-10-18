package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeDirtItemBlock extends TinkersRebornItemBlock {

    public SlimeDirtItemBlock(Block b) {
        super(b, "tinkersreborn.slime.dirt", TinkersRebornConfig.slimeTypes);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

}
