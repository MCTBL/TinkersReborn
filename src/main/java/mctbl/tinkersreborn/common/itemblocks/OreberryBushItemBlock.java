package mctbl.tinkersreborn.common.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class OreberryBushItemBlock extends TinkersRebornItemBlock {

    public OreberryBushItemBlock(Block b) {
        super(b, "tinkersreborn.OreberryBush", TinkersRebornConfig.oreberryBushTypes);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

}
