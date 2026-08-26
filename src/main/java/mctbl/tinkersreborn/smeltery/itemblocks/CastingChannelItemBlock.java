package mctbl.tinkersreborn.smeltery.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class CastingChannelItemBlock extends TinkersRebornItemBlock {

    public CastingChannelItemBlock(Block b) {
        super(b, "tinkersreborn.SearedBlock.CastingChannel", new String[0]);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

}
