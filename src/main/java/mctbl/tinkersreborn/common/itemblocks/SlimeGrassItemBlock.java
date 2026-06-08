package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeGrassItemBlock extends TinkersRebornItemBlock {

    public static final String[] blockTypes = { "blue" };

    public SlimeGrassItemBlock(Block b) {
        super(b, "tinkersreborn.slime.grass", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
