package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeGelItemBlock extends TinkersRebornItemBlock {

    public static final String[] blockTypes = { "blue", "green", "purple" };

    public SlimeGelItemBlock(Block b) {
        super(b, "tinkersreborn.slime.gel", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
