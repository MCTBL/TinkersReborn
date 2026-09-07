package mctbl.tinkersreborn.smeltery.itemblocks;

import net.minecraft.block.Block;

import mctbl.tinkersreborn.library.itemblocks.TinkersRebornItemBlock;

public class GlassItemBlock extends TinkersRebornItemBlock {

    public GlassItemBlock(Block b) {
        super(b, "tinkersreborn.block.glass", new String[] { "pure", "soul" });
        setMaxDamage(0);
        setHasSubtypes(true);
    }
}
