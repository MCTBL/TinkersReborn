package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class SlimeTallGrassItem extends TinkersRebornItemBlock {

    public static final String[] blockTypes = { "tallgrass" };

    public SlimeTallGrassItem(Block b) {
        super(b, "tinkersreborn.slime.grass.tall", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
