package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeLeavesItemBlock extends TinkersRebornItemBlock {

    public static final String[] blockTypes = { "blue" };

    public SlimeLeavesItemBlock(Block b) {
        super(b, "tinkersreborn.slime.leaves", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
