package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeGelItemBlock extends TinkersRebornItemBlock {

    public SlimeGelItemBlock(Block b) {
        super(b, "tinkersreborn.slime.gel", TinkersRebornConfig.slimeTypes);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

}
