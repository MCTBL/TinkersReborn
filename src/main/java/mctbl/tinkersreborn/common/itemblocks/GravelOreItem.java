package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class GravelOreItem extends TinkersRebornItemBlock {

    public GravelOreItem(Block b) {
        super(b, "tinkersreborn.GravelOre", TinkersRebornConfig.gravelOreTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
