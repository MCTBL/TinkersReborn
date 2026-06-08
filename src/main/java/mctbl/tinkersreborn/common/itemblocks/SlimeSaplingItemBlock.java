package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeSaplingItemBlock extends TinkersRebornItemBlock {

    public static final String[] blockTypes = { "bluegreen" };

    public SlimeSaplingItemBlock(Block b) {
        super(b, "tinkersreborn.slime.sapling", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
