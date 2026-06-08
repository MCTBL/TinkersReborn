package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class TinkersRebornMetalItemBlock extends TinkersRebornItemBlock {

    public TinkersRebornMetalItemBlock(Block b) {
        super(b, "tinkersreborn.metalblock", TinkersRebornConfig.metalTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
