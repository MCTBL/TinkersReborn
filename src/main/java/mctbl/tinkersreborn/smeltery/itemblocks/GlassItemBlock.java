package mctbl.tinkersreborn.smeltery.itemblocks;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;
import net.minecraft.block.Block;

public class GlassItemBlock extends TinkersRebornItemBlock {

    public GlassItemBlock(Block b) {
        super(b, "tinkersreborn.block.glass", new String[] { "pure", "soul" });
        setMaxDamage(0);
        setHasSubtypes(true);
    }
}
